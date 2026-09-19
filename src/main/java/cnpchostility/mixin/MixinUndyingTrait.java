package bin.cnpchostility.mixin;

import bin.cnpchostility.NpcDeathContext;
import dev.xkmc.l2hostility.content.traits.legendary.UndyingTrait;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = UndyingTrait.class, remap = false)
public class MixinUndyingTrait {

    @Redirect(
            method = "onDeath",
            at = @At(value = "INVOKE", target = "Ldev/xkmc/l2hostility/content/traits/legendary/UndyingTrait;validTarget(Lnet/minecraft/world/entity/LivingEntity;)Z")
    )
    private boolean cnpchostility$respectNpcCurse(UndyingTrait trait, LivingEntity entity) {
        if (entity instanceof EntityNPCInterface && NpcDeathContext.consumeCurseAtDeath(entity)) {
            return false;
        }
        return trait.validTarget(entity);
    }
}
