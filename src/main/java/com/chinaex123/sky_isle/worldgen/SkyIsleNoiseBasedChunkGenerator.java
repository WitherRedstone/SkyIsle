package com.chinaex123.sky_isle.worldgen;


import com.chinaex123.sky_isle.config.SLConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.concurrent.CompletableFuture;

/**
 * 空岛噪声区块生成器基类。
 * <p>
 * 继承自噪声区块生成器，但屏蔽常规地形生成：
 * 不填充噪声地形、不构建表面、不进行雕刻，
 * 仅按配置决定是否生成结构，并过滤掉不适用于空岛的原版结构。
 * 具体平台的生成位置由各维度的子类实现。
 */
public class SkyIsleNoiseBasedChunkGenerator extends NoiseBasedChunkGenerator {

    /**
     * 构造空岛噪声区块生成器。
     *
     * @param biomeSource 生物群系源
     * @param settings    噪声生成设置
     */
    public SkyIsleNoiseBasedChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    /**
     * 获取指定位置的基础高度。
     * <p>
     * 空岛维度不生成常规地形，统一返回固定高度 64。
     *
     * @param x              方块 X 坐标
     * @param z              方块 Z 坐标
     * @param type           高度图类型
     * @param heightAccessor 高度访问器
     * @param randomState    随机状态
     * @return 固定高度 64
     */
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
        return 64;
    }

    /**
     * 从噪声填充区块。
     * <p>
     * 空岛不生成噪声地形，直接返回已完成的原区块。
     *
     * @param blender          区块混合器
     * @param randomState      随机状态
     * @param structureManager 结构管理器
     * @param chunk            当前区块
     * @return 已完成的原区块
     */
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    /**
     * 构建地表。
     * <p>
     * 空岛不生成常规地表，此方法为空实现。
     *
     * @param region           世界生成区域
     * @param structureManager 结构管理器
     * @param randomState      随机状态
     * @param chunk            当前区块
     */
    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunk) {
    }

    /**
     * 应用雕刻。
     * <p>
     * 空岛不生成洞穴等雕刻，此方法为空实现。
     *
     * @param region           世界生成区域
     * @param seed             世界种子
     * @param randomState      随机状态
     * @param biomeManager     生物群系管理器
     * @param structureManager 结构管理器
     * @param chunk            当前区块
     */
    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunk) {
    }

    /**
     * 创建结构。
     * <p>
     * 仅在配置启用结构生成时，才委托父类执行结构创建。
     *
     * @param registryAccess         注册表访问
     * @param state                  区块生成器结构状态
     * @param structureManager       结构管理器
     * @param centerChunk            中心区块
     * @param structureTemplateManager 结构模板管理器
     * @param level                  维度键
     */
    @Override
    public void createStructures(RegistryAccess registryAccess, ChunkGeneratorStructureState state, StructureManager structureManager,
                                 ChunkAccess centerChunk, StructureTemplateManager structureTemplateManager, ResourceKey<Level> level) {
        if (SLConfig.ENABLE_STRUCTURE_GENERATION.get()) {
            super.createStructures(registryAccess, state, structureManager, centerChunk, structureTemplateManager, level);
        }
    }

    /**
     * 应用生物群系装饰。
     * <p>
     * 仅在配置启用结构生成时执行：遍历结构注册表并逐个放置结构，
     * 同时过滤掉不适用于空岛的结构。
     *
     * @param level            世界生成级别
     * @param chunk            当前区块
     * @param structureManager 结构管理器
     */
    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        if (!SLConfig.ENABLE_STRUCTURE_GENERATION.get()) {
            return;
        }

        ChunkPos chunkPos = chunk.getPos();
        SectionPos sectionPos = SectionPos.of(chunkPos, level.getMinSectionY());
        BoundingBox writableArea = getWritableArea(chunk);

        var structureRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        for (Structure structure : structureRegistry) {
            if (structureManager.shouldGenerateStructures()) {
                if (isFilteredStructure(structure, structureRegistry)) continue;
                structureManager.startsForStructure(sectionPos, structure).forEach(start -> {
                    start.placeInChunk(level, structureManager, this, level.getRandom(), writableArea, chunkPos);
                });
            }
        }
    }

    /**
     * 判断结构是否应被过滤掉。
     * <p>
     * 当前过滤原版命名空间下路径为 fossil 的结构。
     *
     * @param structure 待判断的结构
     * @param registry  结构注册表
     * @return 应被过滤返回 true
     */
    private static boolean isFilteredStructure(Structure structure, net.minecraft.core.Registry<Structure> registry) {
        Identifier loc = registry.getKey(structure);
        if (loc == null) return false;
        return loc.getNamespace().equals("minecraft") && loc.getPath().equals("fossil");
    }

    /**
     * 获取区块的可写入区域。
     * <p>
     * 以区块的最小坐标与当前高度范围构造边界盒。
     *
     * @param chunk 当前区块
     * @return 可写入区域的边界盒
     */
    private static BoundingBox getWritableArea(ChunkAccess chunk) {
        ChunkPos chunkpos = chunk.getPos();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();

        LevelHeightAccessor levelheightaccessor = chunk.getHeightAccessorForGeneration();
        int k = levelheightaccessor.getMinY() + 1;
        int l = levelheightaccessor.getMaxY();
        return new BoundingBox(i, k, j, i + 15, l, j + 15);
    }
}