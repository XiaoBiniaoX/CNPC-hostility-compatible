package bin.cnpchostility.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
    private static final ResourceLocation CURSE_ID = ResourceLocation.tryParse("l2complements:curse");

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

    @Unique
    private static boolean cnpchostility$isCurseEffect(MobEffect effect) {
        if (effect == null) return false;
        ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
        return id != null && id.equals(CURSE_ID);
    }

    @Inject(method = "addEffect", at = @At("RETURN"), cancellable = true)
    private void cnpchostility$forceAllowCurseInvisibility(MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        boolean original = cir.getReturnValue();
        if (!original && effect != null) {
            MobEffect type = effect.getEffect();
            if (type == MobEffects.INVISIBILITY || cnpchostility$isCurseEffect(type)) {
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
            if (type == MobEffects.INVISIBILITY || cnpchostility$isCurseEffect(type)) {
                if (cnpchostility$isNPC((LivingEntity)(Object)this)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
