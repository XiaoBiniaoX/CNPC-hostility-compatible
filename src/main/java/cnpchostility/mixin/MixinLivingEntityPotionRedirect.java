package bin.cnpchostility.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntityPotionRedirect {

    @Unique
    private static boolean cnpchostility$isNPC(LivingEntity entity) {
        Class<?> clazz = entity.getClass();
        do {
            if (clazz.getName().equals("noppes.npcs.entity.EntityNPCInterface")) {
                return true;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return false;
    }

    @Inject(method = "addEffect", at = @At("RETURN"), cancellable = true)
    private void cnpchostility$forceAllowInvisibility(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        boolean original = cir.getReturnValue();
        if (!original && effect != null) {
            MobEffect type = effect.getEffect();
            if (type == MobEffects.INVISIBILITY) {
                if (cnpchostility$isNPC((LivingEntity)(Object)this)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "canBeAffected", at = @At("RETURN"), cancellable = true)
    private void cnpchostility$forceCanBeAffected(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        boolean original = cir.getReturnValue();
        if (!original && effect != null) {
            MobEffect type = effect.getEffect();
            if (type == MobEffects.INVISIBILITY) {
                if (cnpchostility$isNPC((LivingEntity)(Object)this)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
