package com.chinaex123.sky_isle.worldgen;

import com.chinaex123.sky_isle.SkyIsle;
import com.chinaex123.sky_isle.config.SLConfig;
import com.chinaex123.sky_isle.worldgen.dimensions.EndSkyIsland;
import com.chinaex123.sky_isle.worldgen.dimensions.NetherSkyIsland;
import com.chinaex123.sky_isle.worldgen.dimensions.OverworldSkyIsland;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

import java.util.Optional;

/**
 * 空岛结构放置器。
 * <p>
 * 监听区块加载事件，根据当前维度所使用的空岛区块生成器类型，
 * 在对应平台位置放置预设结构，并通过存档数据避免重复放置。
 * 主世界结构放置后会尝试补齐下界与末地的结构。
 */
@EventBusSubscriber(modid = SkyIsle.MODID)
public class SkyIslandPlacer {

    /**
     * 处理区块加载事件。
     * <p>
     * 仅在服务端执行，并按当前维度的空岛区块生成器类型分发到对应的放置逻辑。
     *
     * @param event 区块加载事件
     */
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        if (serverLevel.getChunkSource().getGenerator() instanceof OverworldSkyIsland) {
            placeOverworldStructure(serverLevel, event.getChunk());
        } else if (serverLevel.getChunkSource().getGenerator() instanceof NetherSkyIsland) {
            placeNetherStructure(serverLevel, event.getChunk());
        } else if (serverLevel.getChunkSource().getGenerator() instanceof EndSkyIsland) {
            placeEndStructure(serverLevel, event.getChunk());
        }
    }

    /**
     * 放置主世界结构。
     * <p>
     * 若已放置过则跳过；仅当加载区块与配置的平台位置处于同一区块时执行。
     * 放置成功后标记存档状态，并尝试补齐下界与末地结构。
     *
     * @param serverLevel 服务端世界
     * @param chunk       已加载的区块
     */
    private static void placeOverworldStructure(ServerLevel serverLevel, LevelChunk chunk) {
        SkyIsleSavedData data = serverLevel.getDataStorage().computeIfAbsent(SkyIsleSavedData.TYPE);
        if (data.isOverworldStructurePlaced()) return;

        int posX = SLConfig.STRUCTURE_POS_X.get();
        int posY = SLConfig.STRUCTURE_POS_Y.get();
        int posZ = SLConfig.STRUCTURE_POS_Z.get();

        int chunkX = chunk.getPos().x();
        int chunkZ = chunk.getPos().z();
        if (chunkX != (posX >> 4) || chunkZ != (posZ >> 4)) return;

        String structureName = SLConfig.OVERWORLD_STRUCTURE_NAME.get();
        Optional<StructureTemplate> template = StructureLoader.loadStructure(serverLevel, structureName, "overworld");

        if (template.isPresent()) {
            BlockPos placePos = new BlockPos(posX, posY, posZ);
            StructureLoader.placeStructure(serverLevel, template.get(), placePos);
            data.setOverworldStructurePlaced(true);

            ServerLevel netherLevel = serverLevel.getServer().getLevel(Level.NETHER);
            if (netherLevel != null) {
                placeNetherStructureIfNeeded(netherLevel);
            }

            ServerLevel endLevel = serverLevel.getServer().getLevel(Level.END);
            if (endLevel != null) {
                placeEndStructureIfNeeded(endLevel);
            }
        }
    }

    /**
     * 放置下界结构。
     * <p>
     * 若已放置过则跳过；仅当加载区块与配置的下界平台位置处于同一区块时执行。
     *
     * @param serverLevel 服务端世界
     * @param chunk       已加载的区块
     */
    private static void placeNetherStructure(ServerLevel serverLevel, LevelChunk chunk) {
        SkyIsleSavedData data = serverLevel.getDataStorage().computeIfAbsent(SkyIsleSavedData.TYPE);
        if (data.isNetherStructurePlaced()) return;

        int posX = SLConfig.NETHER_POS_X.get();
        int posY = SLConfig.NETHER_POS_Y.get();
        int posZ = SLConfig.NETHER_POS_Z.get();

        int chunkX = chunk.getPos().x();
        int chunkZ = chunk.getPos().z();
        if (chunkX != (posX >> 4) || chunkZ != (posZ >> 4)) return;

        placeNetherStructureIfNeeded(serverLevel);
    }

    /**
     * 按需放置下界结构。
     * <p>
     * 若已放置过则跳过；根据配置的出生维度选择重生结构或普通结构并放置。
     *
     * @param netherLevel 下界世界
     */
    private static void placeNetherStructureIfNeeded(ServerLevel netherLevel) {
        SkyIsleSavedData data = netherLevel.getDataStorage().computeIfAbsent(SkyIsleSavedData.TYPE);
        if (data.isNetherStructurePlaced()) return;

        int posX = SLConfig.NETHER_POS_X.get();
        int posY = SLConfig.NETHER_POS_Y.get();
        int posZ = SLConfig.NETHER_POS_Z.get();

        String structureName = "the_nether".equals(SLConfig.SPAWN_DIMENSION.get())
                ? SLConfig.NETHER_RESPAWN_STRUCTURE_NAME.get()
                : SLConfig.NETHER_STRUCTURE_NAME.get();
        Optional<StructureTemplate> template = StructureLoader.loadStructure(netherLevel, structureName, "the_nether");

        if (template.isPresent()) {
            BlockPos placePos = new BlockPos(posX, posY, posZ);
            StructureLoader.placeStructure(netherLevel, template.get(), placePos);
            data.setNetherStructurePlaced(true);
        }
    }

    /**
     * 放置末地结构。
     * <p>
     * 若已放置过则跳过；仅当加载区块与配置的末地平台位置处于同一区块时执行。
     *
     * @param serverLevel 服务端世界
     * @param chunk       已加载的区块
     */
    private static void placeEndStructure(ServerLevel serverLevel, LevelChunk chunk) {
        SkyIsleSavedData data = serverLevel.getDataStorage().computeIfAbsent(SkyIsleSavedData.TYPE);
        if (data.isEndStructurePlaced()) return;

        int posX = SLConfig.END_POS_X.get();
        int posZ = SLConfig.END_POS_Z.get();

        int chunkX = chunk.getPos().x();
        int chunkZ = chunk.getPos().z();
        if (chunkX != (posX >> 4) || chunkZ != (posZ >> 4)) return;

        placeEndStructureIfNeeded(serverLevel);
    }

    /**
     * 按需放置末地结构。
     * <p>
     * 若已放置过则跳过；加载配置的末地重生结构并放置。
     *
     * @param endLevel 末地世界
     */
    private static void placeEndStructureIfNeeded(ServerLevel endLevel) {
        SkyIsleSavedData data = endLevel.getDataStorage().computeIfAbsent(SkyIsleSavedData.TYPE);
        if (data.isEndStructurePlaced()) return;

        int posX = SLConfig.END_POS_X.get();
        int posY = SLConfig.END_POS_Y.get();
        int posZ = SLConfig.END_POS_Z.get();

        String structureName = SLConfig.END_RESPAWN_STRUCTURE_NAME.get();
        Optional<StructureTemplate> template = StructureLoader.loadStructure(endLevel, structureName, "the_end");

        if (template.isPresent()) {
            BlockPos placePos = new BlockPos(posX, posY, posZ);
            StructureLoader.placeStructure(endLevel, template.get(), placePos);
            data.setEndStructurePlaced(true);
        }
    }
}