package com.chinaex123.sky_isle.mixin;

import com.chinaex123.sky_isle.worldgen.dimensions.EndSkyIsland;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * 末影龙战斗的 Mixin。
 * <p>
 * 当末地使用空岛维度生成器时，为出口传送门指定一个固定的生成位置，
 * 避免依赖原版基于地形扫描确定传送门位置。
 */
@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {

    /** 末影龙战斗所在的服务端世界 */
    @Shadow
    private ServerLevel level;

    /** 出口传送门的位置，可能尚未确定 */
    @Shadow
    private @Nullable BlockPos exitPortalLocation;

    /**
     * 在生成出口传送门前设置其位置。
     * <p>
     * 仅当末地使用空岛维度生成器且出口传送门位置尚未确定时，
     * 将其固定为坐标 (0, 63, 0)。
     *
     * @param activated 是否为已激活状态
     * @param ci        回调信息
     */
    @Inject(method = "spawnExitPortal", at = @At("HEAD"))
    private void skyIsle$setExitPortalLocation(boolean activated, CallbackInfo ci) {
        if (level.getChunkSource().getGenerator() instanceof EndSkyIsland) {
            if (exitPortalLocation == null) {
                exitPortalLocation = BlockPos.ZERO.atY(63);
            }
        }
    }
}