package bin.cnpchostility.mixin;

import bin.cnpchostility.util.NPCUtil;
import dev.xkmc.l2hostility.content.traits.common.FieryTrait;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "dev.xkmc.l2hostility.content.traits.common.FieryTrait", remap = false)
public class MixinFieryTrait {

    @Redirect(method = "onHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setSecondsOnFire(I)V", remap = false), remap = false)
    private void cnpchostility$redirectSetFire(LivingEntity instance, int seconds) {
        if (!NPCUtil.isNPC(instance)) {
            instance.setSecondsOnFire(seconds);
        }
    }
}
