package bin.cnpchostility.mixin;

import dev.xkmc.l2library.capability.entity.GeneralCapabilityHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.xkmc.l2library.capability.entity.GeneralCapabilityHolder", remap = false)
public abstract class MixinGeneralCapabilityHolder {

    @Accessor("id")
    abstract ResourceLocation cnpchostility$getId();

    @Inject(method = "shouldHaveCap", at = @At("HEAD"), cancellable = true)
    private void cnpchostility$shouldHaveCap(ICapabilityProvider entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity le) {
            Class<?> clazz = le.getClass();
            do {
                if (clazz.getName().equals("noppes.npcs.entity.EntityNPCInterface")) {
                    if (cnpchostility$getId().equals(new ResourceLocation("l2hostility", "traits"))) {
                        cir.setReturnValue(true);
                    }
                    return;
                }
                clazz = clazz.getSuperclass();
            } while (clazz != null);
        }
    }
}
