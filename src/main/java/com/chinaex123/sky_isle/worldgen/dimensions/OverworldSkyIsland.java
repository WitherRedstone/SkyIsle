package com.chinaex123.sky_isle.worldgen.dimensions;

import com.chinaex123.sky_isle.config.SLConfig;
import com.chinaex123.sky_isle.worldgen.SkyIsleNoiseBasedChunkGenerator;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * 主世界空岛区块生成器。
 * <p>
 * 继承自空岛噪声区块生成器，用于主世界维度。
 * 空岛平台位置由配置项提供，基础高度统一返回维度最低高度，
 * 出生点高度基于平台配置高度计算。
 */
public class OverworldSkyIsland extends SkyIsleNoiseBasedChunkGenerator {

    /** 区块生成器编解码器，定义生物群系源与噪声生成设置两个字段 */
    public static final MapCodec<OverworldSkyIsland> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings)
            ).apply(instance, instance.stable(OverworldSkyIsland::new))
    );

    /**
     * 构造主世界空岛区块生成器。
     *
     * @param biomeSource 生物群系源
     * @param settings    噪声生成设置
     */
    public OverworldSkyIsland(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    /**
     * 获取空岛平台在 X 轴上的位置。
     *
     * @return 平台 X 坐标
     */
    public int getPlatformX() {
        return SLConfig.STRUCTURE_POS_X.get();
    }

    /**
     * 获取空岛平台在 Y 轴上的位置。
     *
     * @return 平台 Y 坐标
     */
    public int getPlatformY() {
        return SLConfig.STRUCTURE_POS_Y.get();
    }

    /**
     * 获取空岛平台在 Z 轴上的位置。
     *
     * @return 平台 Z 坐标
     */
    public int getPlatformZ() {
        return SLConfig.STRUCTURE_POS_Z.get();
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
     * 获取指定位置的基础高度。
     * <p>
     * 空岛维度不使用常规地形高度，统一返回维度最低高度。
     *
     * @param x              方块 X 坐标
     * @param z              方块 Z 坐标
     * @param type           高度图类型
     * @param heightAccessor 高度访问器
     * @param randomState    随机状态
     * @return 维度最低高度
     */
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
        return heightAccessor.getMinY();
    }

    /**
     * 获取出生点所在的高度。
     * <p>
     * 基于配置的主世界平台高度加一。
     *
     * @param level 高度访问器
     * @return 出生点高度
     */
    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return SLConfig.STRUCTURE_POS_Y.get() + 1;
    }
}