package com.chinaex123.sky_isle.event;

import com.chinaex123.sky_isle.SkyIsle;
import com.chinaex123.sky_isle.config.SLConfig;
import com.chinaex123.sky_isle.worldgen.StructureLoader;
import com.chinaex123.sky_isle.worldgen.dimensions.OverworldSkyIsland;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 空岛模组事件处理器。
 * <p>
 * 负责处理与空岛玩法相关的事件：设置主世界出生点、
 * 按配置将玩家传送到指定维度的空岛平台、
 * 以及在玩家重生时将其传送回对应维度的平台。
 */
@EventBusSubscriber(modid = SkyIsle.MODID)
public class SkyIsleEvents {

    /**
     * 处理创建出生点事件。
     * <p>
     * 仅对主世界且使用空岛区块生成器时生效：
     * 以平台位置与结构尺寸计算出生点中心，取其地表高度上方一格作为出生点，
     * 写入重生数据并取消原出生点创建逻辑。
     *
     * @param event 创建出生点事件
     */
    @SubscribeEvent
    public static void onCreateSpawnPosition(LevelEvent.CreateSpawnPosition event) {
        LevelAccessor level = event.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (serverLevel.dimension() != Level.OVERWORLD) return;

        if (serverLevel.getChunkSource().getGenerator() instanceof OverworldSkyIsland overworldIsland) {
            int posX = overworldIsland.getPlatformX();
            int posZ = overworldIsland.getPlatformZ();

            int spawnX = posX;
            int spawnZ = posZ;

            String structureName = SLConfig.OVERWORLD_STRUCTURE_NAME.get();
            var template = StructureLoader.loadStructure(serverLevel, structureName, "overworld");
            if (template.isPresent()) {
                Vec3i size = template.get().getSize();
                spawnX = posX + (size.getX() - 1) / 2;
                spawnZ = posZ + (size.getZ() - 1) / 2;
            }

            int spawnY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, spawnX, spawnZ) + 1;
            BlockPos spawnPos = new BlockPos(spawnX, spawnY, spawnZ);
            serverLevel.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, spawnPos, 0.0F, 0.0F));
            event.setCanceled(true);
        }
    }

    /**
     * 处理玩家刻事件。
     * <p>
     * 当配置的出生维度不是主世界时，在玩家首次进入主世界后
     * 标记已处理，并按配置将其传送到下界或末地的空岛平台。
     *
     * @param event 玩家刻事件
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        String spawnDim = SLConfig.SPAWN_DIMENSION.get();
        if ("overworld".equals(spawnDim)) return;
        if (player.level().dimension() != Level.OVERWORLD) return;
        if (!player.getPersistentData().getBoolean("sky_isle:custom_spawned").orElse(false)) {
            player.getPersistentData().putBoolean("sky_isle:custom_spawned", true);
            if ("the_nether".equals(spawnDim)) {
                teleportToNetherSpawn(player);
            } else if ("the_end".equals(spawnDim)) {
                teleportToEndSpawn(player);
            }
        }
    }

    /**
     * 处理玩家重生事件。
     * <p>
     * 当配置的出生维度不是主世界时，除末地征服情形外，
     * 将重生玩家传送到对应维度的空岛平台。
     *
     * @param event 玩家重生事件
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        String spawnDim = SLConfig.SPAWN_DIMENSION.get();
        if ("overworld".equals(spawnDim)) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.isEndConquered()) return;
        if ("the_nether".equals(spawnDim)) {
            teleportToNetherSpawn(player);
        } else if ("the_end".equals(spawnDim)) {
            teleportToEndSpawn(player);
        }
    }

    /**
     * 将玩家传送到下界空岛平台。
     *
     * @param player 目标玩家
     */
    private static void teleportToNetherSpawn(ServerPlayer player) {
        ServerLevel targetLevel = player.level().getServer().getLevel(Level.NETHER);
        if (targetLevel == null) return;
        teleportToDimensionSpawn(player, targetLevel, SLConfig.NETHER_POS_X.get(), SLConfig.NETHER_POS_Z.get(),
                SLConfig.NETHER_RESPAWN_STRUCTURE_NAME.get(), "the_nether");
    }

    /**
     * 将玩家传送到末地空岛平台。
     *
     * @param player 目标玩家
     */
    private static void teleportToEndSpawn(ServerPlayer player) {
        ServerLevel targetLevel = player.level().getServer().getLevel(Level.END);
        if (targetLevel == null) return;
        teleportToDimensionSpawn(player, targetLevel, SLConfig.END_POS_X.get(), SLConfig.END_POS_Z.get(),
                SLConfig.END_RESPAWN_STRUCTURE_NAME.get(), "the_end");
    }

    /**
     * 将玩家传送到指定维度的空岛平台。
     * <p>
     * 以重生结构的尺寸计算平台中心坐标，
     * 取该位置地表高度上方一格作为落点并执行传送。
     *
     * @param player        目标玩家
     * @param targetLevel   目标维度世界
     * @param structureName 重生结构名称
     * @param dimension     维度目录名称
     */
    private static void teleportToDimensionSpawn(ServerPlayer player, ServerLevel targetLevel, int posX, int posZ, String structureName, String dimension) {
        double centerX = posX + 0.5;
        double centerZ = posZ + 0.5;

        var template = StructureLoader.loadStructure(targetLevel, structureName, dimension);
        if (template.isPresent()) {
            Vec3i size = template.get().getSize();
            centerX = posX + (size.getX() - 1) / 2.0 + 0.5;
            centerZ = posZ + (size.getZ() - 1) / 2.0 + 0.5;
        }

        int spawnY = targetLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) centerX, (int) centerZ) + 1;

        TeleportTransition transition = new TeleportTransition(
                targetLevel, new Vec3(centerX, spawnY, centerZ),
                Vec3.ZERO, player.getYRot(), player.getXRot(),
                TeleportTransition.DO_NOTHING
        );
        player.teleport(transition);
    }
}