package boeseset.gamephases.mixin.player;

import boeseset.gamephases.GamePhases;
import com.mojang.authlib.GameProfile;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onPlayerRespawned(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void respawnPlayerMixin(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> info, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld,
                                    Optional<Object> optional, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity) {

        // Set on client
        GamePhases.getPhaseData(player).sync();

        // Set on server
        GamePhases.getPhaseData(serverPlayerEntity).set(GamePhases.getPhaseData(player).getPhases());

    }

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onPlayerConnected(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onConnectPlayerMixin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci, GameProfile gameProfile, UserCache userCache, Optional<GameProfile> optional, String string, NbtCompound nbtCompound, RegistryKey<World> registryKey, ServerWorld serverWorld, ServerWorld serverWorld2, String string2, WorldProperties worldProperties, ServerPlayNetworkHandler serverPlayNetworkHandler){
        boolean isFirstTimeJoin = nbtCompound == null;

        if (isFirstTimeJoin && server != null){
            GamePhases.getPhaseData(player).sync();
        }
    }
}
