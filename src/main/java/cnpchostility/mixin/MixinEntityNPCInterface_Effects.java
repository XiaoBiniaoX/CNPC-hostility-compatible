package bin.cnpchostility.mixin;

import bin.cnpchostility.NpcDeathContext;
import dev.xkmc.l2complements.init.registrate.LCEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "noppes.npcs.entity.EntityNPCInterface", remap = false)
public class MixinEntityNPCInterface_Effects {

    @Inject(method = {"die", "m_6667_"}, at = @At("HEAD"), require = 0)
    private void cnpchostility$captureCurseAtDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        NpcDeathContext.capture(self, self.hasEffect(LCEffects.CURSE.get()));
    }

    @Inject(method = {"die", "m_6667_"}, at = @At("RETURN"), require = 0)
    private void cnpchostility$clearDeathContext(DamageSource source, CallbackInfo ci) {
        NpcDeathContext.clear((LivingEntity) (Object) this);
    }

    @Inject(method = {"tick", "m_8119_"}, at = @At("HEAD"), require = 0)
    private void cnpchostility$expireDeathContext(CallbackInfo ci) {
        NpcDeathContext.clear((LivingEntity) (Object) this);
    }

    @Inject(method = {"canBeAffected", "m_7301_"}, at = @At("RETURN"), cancellable = true, require = 0)
    private void cnpchostility$allowInvisibility(
            MobEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && effect != null && effect.getEffect() == MobEffects.INVISIBILITY) {
            cir.setReturnValue(true);
        }
    }
}
