package bin.cnpchostility.mixin;

import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.events.LHAttackListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Mixin(value = LHAttackListener.class, remap = false)
public class MixinLHAttackListener {

    @Redirect(
            method = "onHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/xkmc/l2hostility/content/capability/mob/MobTraitCap;traitEvent(Ljava/util/function/BiConsumer;)V"
            )
    )
    private void cnpchostility$filterFriendlyTraitEffects(
            MobTraitCap cap,
            BiConsumer<MobTrait, Integer> action,
            AttackCache cache,
            ItemStack weapon) {
        LivingEntity attacker = cache.getAttacker();
        LivingEntity target = cache.getAttackTarget();
        if (!(attacker instanceof EntityNPCInterface npc)) {
            cap.traitEvent(action);
            return;
        }

        if (target instanceof Player player) {
            if (!npc.getFaction().isAggressiveToPlayer(player)) {
                return;
            }
        } else if (target instanceof EntityNPCInterface targetNpc
                && !npc.getFaction().isAggressiveToNpc(targetNpc)) {
            return;
        }

        cap.traitEvent(action);
    }
}
