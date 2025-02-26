package boeseset.gamephases.kube;

import boeseset.gamephases.GamePhases;
import boeseset.gamephases.api.Phase;
import dev.latvian.mods.kubejs.event.EventJS;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;

public class GamePhasesEventJS extends EventJS {

    private static final Map<String, Phase> PHASES = new HashMap<>();
    private final MinecraftServer server;

    public GamePhasesEventJS(MinecraftServer server) {
        this.server = server;
        PHASES.clear();
    }

    public Phase phase(String id) {
        Phase phase = new Phase(id, server.getRecipeManager());
        PHASES.put(id, phase);
        return phase;
    }

    public static Map<String, Phase> getPhases() {
        return PHASES;
    }

    public static void sync(MinecraftServer server) {
        PacketByteBuf packet = PacketByteBufs.create();

        // Save phases to packet
        NbtCompound tag = new NbtCompound();
        NbtList l = new NbtList();
        PHASES.forEach((id, phase) -> {
            NbtCompound inner = new NbtCompound();
            inner.putString("ID", id);
            inner.put("PhaseData", phase.toTag());
            l.add(inner);
        });

        tag.put("Phases", l);
        packet.writeNbt(tag);

        // Send packet to all players
        server.getPlayerManager().getPlayerList().forEach(player -> {
            player.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(GamePhases.ALL_PHASE_SYNC_ID,  packet));
        });
    }
}
