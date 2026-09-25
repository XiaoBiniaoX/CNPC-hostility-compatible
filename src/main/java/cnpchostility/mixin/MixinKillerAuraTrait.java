package bin.cnpchostility.mixin;

import dev.xkmc.l2hostility.content.traits.legendary.KillerAuraTrait;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = KillerAuraTrait.class, remap = false)
public class MixinKillerAuraTrait {

    @Redirect(
            method = "tick(Lnet/minecraft/world/entity/LivingEntity;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;",
                    remap = false
            ),
            require = 1
    )
    private List<LivingEntity> cnpchostility$filterFriendlyAuraTargets(
            Level level,
            Class<LivingEntity> entityClass,
            AABB bounds,
            LivingEntity source,
            int traitLevel) {
        List<LivingEntity> candidates = level.getEntitiesOfClass(entityClass, bounds);
        if (!(source instanceof EntityNPCInterface npc)) {
            return candidates;
        }

        return candidates.stream().filter(candidate -> {
            if (candidate instanceof Player player) {
                return npc.getFaction().isAggressiveToPlayer(player);
            }
            if (candidate instanceof EntityNPCInterface targetNpc) {
                return npc.getFaction().isAggressiveToNpc(targetNpc);
            }
            return true;
        }).toList();
    }
}
