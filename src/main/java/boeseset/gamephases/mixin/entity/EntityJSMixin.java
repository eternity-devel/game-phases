package boeseset.gamephases.mixin.entity;

import boeseset.gamephases.GamePhases;
import dev.latvian.mods.kubejs.entity.EntityJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityJS.class)
public class EntityJSMixin {

    @Shadow @Final public Entity minecraftEntity;

    public boolean hasPhase(String phase) {
        if(minecraftEntity instanceof PlayerEntity player) {
            return GamePhases.getPhaseData(player).has(phase);
        } else {
            return false;
        }
    }
}
