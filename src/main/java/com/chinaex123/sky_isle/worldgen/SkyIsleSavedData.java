package com.chinaex123.sky_isle.worldgen;

import com.chinaex123.sky_isle.SkyIsle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * 空岛结构放置状态存档数据。
 * <p>
 * 记录主世界、下界与末地三个维度的平台结构是否已放置，
 * 通过 SavedDataType 与 Codec 持久化到世界存档中，
 * 避免重复放置结构。
 */
public class SkyIsleSavedData extends SavedData {

    /** 主世界结构是否已放置 */
    private boolean overworldStructurePlaced;
    /** 下界结构是否已放置 */
    private boolean netherStructurePlaced;
    /** 末地结构是否已放置 */
    private boolean endStructurePlaced;

    /** 本数据类的编解码器，包含三个维度的放置状态字段 */
    private static final Codec<SkyIsleSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("overworld_structure_placed").forGetter(sd -> sd.overworldStructurePlaced),
            Codec.BOOL.fieldOf("nether_structure_placed").forGetter(sd -> sd.netherStructurePlaced),
            Codec.BOOL.fieldOf("end_structure_placed").forGetter(sd -> sd.endStructurePlaced)
    ).apply(instance, instance.stable(SkyIsleSavedData::new)));

    /** 本数据类的注册类型，包含标识符、构造函数、编解码器与数据修复类型 */
    public static final SavedDataType<SkyIsleSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(SkyIsle.MODID, "structure_placed"),
            SkyIsleSavedData::new,
            CODEC,
            null
    );

    /**
     * 构造默认的空岛结构放置状态，三个维度均为未放置。
     */
    public SkyIsleSavedData() {
        this.overworldStructurePlaced = false;
        this.netherStructurePlaced = false;
        this.endStructurePlaced = false;
    }

    /**
     * 由已有的放置状态构造存档数据。
     *
     * @param overworldStructurePlaced 主世界结构是否已放置
     * @param netherStructurePlaced    下界结构是否已放置
     * @param endStructurePlaced       末地结构是否已放置
     */
    public SkyIsleSavedData(boolean overworldStructurePlaced, boolean netherStructurePlaced, boolean endStructurePlaced) {
        this.overworldStructurePlaced = overworldStructurePlaced;
        this.netherStructurePlaced = netherStructurePlaced;
        this.endStructurePlaced = endStructurePlaced;
    }

    /**
     * 判断主世界结构是否已放置。
     *
     * @return 已放置返回 true
     */
    public boolean isOverworldStructurePlaced() {
        return overworldStructurePlaced;
    }

    /**
     * 设置主世界结构的放置状态，并标记为脏数据。
     *
     * @param placed 是否已放置
     */
    public void setOverworldStructurePlaced(boolean placed) {
        this.overworldStructurePlaced = placed;
        setDirty();
    }

    /**
     * 判断下界结构是否已放置。
     *
     * @return 已放置返回 true
     */
    public boolean isNetherStructurePlaced() {
        return netherStructurePlaced;
    }

    /**
     * 设置下界结构的放置状态，并标记为脏数据。
     *
     * @param placed 是否已放置
     */
    public void setNetherStructurePlaced(boolean placed) {
        this.netherStructurePlaced = placed;
        setDirty();
    }

    /**
     * 判断末地结构是否已放置。
     *
     * @return 已放置返回 true
     */
    public boolean isEndStructurePlaced() {
        return endStructurePlaced;
    }

    /**
     * 设置末地结构的放置状态，并标记为脏数据。
     *
     * @param placed 是否已放置
     */
    public void setEndStructurePlaced(boolean placed) {
        this.endStructurePlaced = placed;
        setDirty();
    }
}