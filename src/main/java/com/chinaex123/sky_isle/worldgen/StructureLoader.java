package com.chinaex123.sky_isle.worldgen;

import com.chinaex123.sky_isle.SkyIsle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

/**
 * 结构模板加载与放置工具。
 * <p>
 * 负责在配置目录下准备各维度的默认结构文件，
 * 从磁盘加载结构模板并缓存，以及在指定位置放置结构。
 * 结构文件按维度分目录存放，命名区分主世界、下界与末地的预设。
 */
public class StructureLoader {

    /** 主世界可用的结构名称列表 */
    private static final List<String> OVERWORLD_STRUCTURES = List.of(
            "overworld_oak",
            "overworld_acacia",
            "overworld_bamboo"
    );

    /** 下界可用的结构名称列表 */
    private static final List<String> NETHER_STRUCTURES = List.of(
            "the_nether_default",
            "the_nether_respawn"
    );

    /** 末地可用的结构名称列表 */
    private static final List<String> END_STRUCTURES = List.of(
            "the_end_respawn"
    );

    /** 已缓存的结构模板 */
    private static StructureTemplate cachedTemplate;
    /** 已缓存结构模板对应的名称 */
    private static String cachedTemplateName;

    /**
     * 确保各维度的结构目录存在，并补齐缺失的默认结构文件。
     * <p>
     * 依次处理主世界、下界与末地目录，逐个复制内置的默认结构文件。
     */
    public static void ensureDirectories() {
        try {
            Path overworldDir = Path.of("config", "sky_isle", "structures", "overworld");
            Files.createDirectories(overworldDir);
            for (String name : OVERWORLD_STRUCTURES) {
                copyDefaultStructure(name, "overworld");
            }

            Path netherDir = Path.of("config", "sky_isle", "structures", "the_nether");
            Files.createDirectories(netherDir);
            for (String name : NETHER_STRUCTURES) {
                copyDefaultStructure(name, "the_nether");
            }

            Path endDir = Path.of("config", "sky_isle", "structures", "the_end");
            Files.createDirectories(endDir);
            for (String name : END_STRUCTURES) {
                copyDefaultStructure(name, "the_end");
            }
        } catch (Exception e) {
            SkyIsle.LOGGER.error("[StructureLoader.ensureDirectories] 无法创建结构目录]", e);
        }
    }

    /**
     * 将内置的默认结构文件复制到配置目录。
     * <p>
     * 若目标文件已存在则跳过；资源不存在时也直接返回。
     *
     * @param name      结构名称
     * @param dimension 维度目录名称
     */
    private static void copyDefaultStructure(String name, String dimension) {
        Path targetPath = Path.of("config", "sky_isle", "structures", dimension, name + ".nbt");
        if (Files.exists(targetPath)) return;

        String resourcePath = "/data/" + SkyIsle.MODID + "/structure/" + name + ".nbt";
        try (InputStream stream = StructureLoader.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                return;
            }
            Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            SkyIsle.LOGGER.error("[StructureLoader.copyDefaultStructure] 复制默认结构文件失败: {}", name, e);
        }
    }

    /**
     * 加载指定名称与维度的结构模板。
     * <p>
     * 若与上次缓存的结构名称一致则直接返回缓存；
     * 否则从配置目录读取 NBT 文件并解析为结构模板，成功后更新缓存。
     *
     * @param level     服务端世界，用于获取方块注册表
     * @param name      结构名称
     * @param dimension 维度目录名称
     * @return 结构模板，加载失败或文件不存在时返回空 Optional
     */
    public static Optional<StructureTemplate> loadStructure(ServerLevel level, String name, String dimension) {
        if (cachedTemplate != null && name.equals(cachedTemplateName)) {
            return Optional.of(cachedTemplate);
        }

        try {
            Path structurePath = Path.of("config", "sky_isle", "structures", dimension, name + ".nbt");
            File file = structurePath.toFile();

            if (!file.exists()) {
                return Optional.empty();
            }

            CompoundTag nbt = NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
            HolderGetter<Block> blockLookup = level.registryAccess().lookupOrThrow(Registries.BLOCK);
            StructureTemplate template = new StructureTemplate();
            template.load(blockLookup, nbt);

            cachedTemplate = template;
            cachedTemplateName = name;

            return Optional.of(template);
        } catch (Exception e) {
            SkyIsle.LOGGER.error("[StructureLoader.loadStructure] 加载结构模板文件失败: {}", name, e);
            return Optional.empty();
        }
    }

    /**
     * 在指定位置放置结构模板。
     * <p>
     * 使用无镜像、无旋转且包含实体的放置设置，
     * 以结构方块更新标志级别放置到世界中。
     *
     * @param level    服务端世界
     * @param template 结构模板
     * @param pos      放置位置
     */
    public static void placeStructure(ServerLevel level, StructureTemplate template, BlockPos pos) {
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setMirror(Mirror.NONE)
                .setRotation(Rotation.NONE)
                .setIgnoreEntities(false);

        template.placeInWorld(level, pos, pos, settings, level.getRandom(), 2);
    }

    /**
     * 清除已缓存的结构模板。
     * <p>
     * 通常在结构文件被修改或重载配置后调用，以强制下次重新读取。
     */
    public static void invalidateCache() {
        cachedTemplate = null;
        cachedTemplateName = null;
    }
}