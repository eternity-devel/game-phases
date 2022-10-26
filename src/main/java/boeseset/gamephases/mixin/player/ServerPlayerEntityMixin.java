package boeseset.gamephases.mixin.player;

import boeseset.gamephases.kube.GamePhasesEventJS;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(
            method = "moveToWorld",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void beforeTeleport(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        boolean allowed = GamePhasesEventJS.getPhases().values().stream().filter(phase -> phase.restricts(destination)).allMatch(phase -> phase.hasUnlocked((PlayerEntity) (Object) this));
        PlayerEntity player = (PlayerEntity) (Object) this;
        if(!allowed) {
            player.sendSystemMessage(new LiteralText("§4Your are not allowed to teleport to the destination.\nPhase not unlocked.§f."), Util.NIL_UUID);
            cir.cancel();
        }
    }
}
