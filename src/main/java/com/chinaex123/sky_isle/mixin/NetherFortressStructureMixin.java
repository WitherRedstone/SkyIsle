package com.chinaex123.sky_isle.mixin;

import com.chinaex123.sky_isle.config.SLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * 下界要塞结构的 Mixin。
 * <p>
 * 在配置启用时，将下界要塞的生成内容限制为仅包含一个烈焰人刷怪房
 */
@Mixin(NetherFortressStructure.class)
public class NetherFortressStructureMixin {

    /**
     * 在查找生成点时注入，仅生成烈焰人刷怪房。
     * <p>
     * 若配置未启用则直接放行原逻辑；
     * 否则在区块起始位置构建一个怪物王座结构作为唯一起始部件。
     *
     * @param context 结构生成上下文
     * @param cir     回调信息，用于设置返回值
     */
    @Inject(method = "findGenerationPoint", at = @At("HEAD"), cancellable = true)
    private void onlyBlazeSpawnerRoom(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (!SLConfig.BLAZE_ROOM_ONLY.get()) return;

        ChunkPos chunkPos = context.chunkPos();
        BlockPos startPos = new BlockPos(chunkPos.getMinBlockX(), 64, chunkPos.getMinBlockZ());

        cir.setReturnValue(Optional.of(new Structure.GenerationStub(startPos, (builder) -> {
            RandomSource random = context.random();
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BoundingBox box = BoundingBox.orientBox(
                    chunkPos.getMinBlockX() + 2, 64, chunkPos.getMinBlockZ() + 2,
                    -2, 0, 0, 7, 8, 9, direction
            );
            NetherFortressPieces.MonsterThrone throne = new NetherFortressPieces.MonsterThrone(0, box, direction);
            builder.addPiece(throne);
        })));
    }
}