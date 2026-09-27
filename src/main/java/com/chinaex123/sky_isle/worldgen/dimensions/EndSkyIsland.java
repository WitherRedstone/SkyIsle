package com.chinaex123.sky_isle.worldgen.dimensions;

import com.chinaex123.sky_isle.config.SLConfig;
import com.chinaex123.sky_isle.worldgen.SkyIsleNoiseBasedChunkGenerator;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * 末地空岛区块生成器。
 * <p>
 * 继承自空岛噪声区块生成器，用于末地维度。
 * 在区块装饰阶段按配置生成结构与末地水晶柱，
 * 并过滤掉原版化石等不适用于空岛的结构。
 */
public class EndSkyIsland extends SkyIsleNoiseBasedChunkGenerator {

    /** 区块生成器编解码器，定义生物群系源与噪声生成设置两个字段 */
    public static final MapCodec<EndSkyIsland> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
            ).apply(instance, instance.stable(EndSkyIsland::new))
    );

    /**
     * 构造末地空岛区块生成器。
     *
     * @param biomeSource 生物群系源
     * @param settings    噪声生成设置
     */
    public EndSkyIsland(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    /**
     * 获取空岛平台在 X 轴上的位置。
     *
     * @return 平台 X 坐标
     */
    public int getPlatformX() {
        return SLConfig.END_POS_X.get();
    }

    /**
     * 获取空岛平台在 Y 轴上的位置。
     *
     * @return 平台 Y 坐标
     */
    public int getPlatformY() {
        return SLConfig.END_POS_Y.get();
    }

    /**
     * 获取空岛平台在 Z 轴上的位置。
     *
     * @return 平台 Z 坐标
     */
    public int getPlatformZ() {
        return SLConfig.END_POS_Z.get();
    }

    /**
     * 获取区块生成器编解码器。
     *
     * @return 区块生成器编解码器
     */
    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    /**
     * 获取出生点所在的高度。
     *
     * @param level 高度访问器
     * @return 出生点高度
     */
    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return 64;
    }

    /**
     * 应用生物群系装饰。
     * <p>
     * 当结构与末地水晶柱的生成均未启用时直接返回。
     * 启用结构生成时，遍历结构注册表并逐个放置结构，同时过滤掉不适用于空岛的结构；
     * 启用末地水晶柱生成时，仅定位并放置路径为 end_spike 的已放置特征。
     *
     * @param level            世界生成级别
     * @param chunk            当前区块
     * @param structureManager 结构管理器
     */
    @Override
    public void applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        if (!SLConfig.ENABLE_STRUCTURE_GENERATION.get() && !SLConfig.ENABLE_END_SPIKES.get()) {
            return;
        }

        ChunkPos chunkPos = chunk.getPos();
        SectionPos sectionPos = SectionPos.of(chunkPos, level.getMinSectionY());
        BlockPos minChunkPos = sectionPos.origin();
        BoundingBox writableArea = getWritableArea(chunk);

        if (SLConfig.ENABLE_STRUCTURE_GENERATION.get()) {
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

        if (SLConfig.ENABLE_END_SPIKES.get()) {
            var placedFeatures = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);

            WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
            long decorationSeed = random.setDecorationSeed(level.getSeed(), minChunkPos.getX(), minChunkPos.getZ());
            int surfaceStepOrdinal = GenerationStep.Decoration.SURFACE_STRUCTURES.ordinal();

            for (PlacedFeature feature : placedFeatures) {
                Identifier loc = placedFeatures.getKey(feature);
                if (loc != null && loc.getPath().equals("end_spike")) {
                    random.setFeatureSeed(decorationSeed, 0, surfaceStepOrdinal);
                    feature.placeWithBiomeCheck(level, this, random, minChunkPos);
                }
            }
        }
    }

    /**
     * 判断结构是否应被过滤掉。
     *
     * @param structure 待判断的结构
     * @param registry  结构注册表
     * @return 应被过滤返回 true
     */
    private static boolean isFilteredStructure(Structure structure, Registry<Structure> registry) {
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