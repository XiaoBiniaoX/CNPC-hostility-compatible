package bin.cnpchostility.mixin;

import bin.cnpchostility.util.NPCUtil;
import dev.xkmc.l2hostility.content.traits.legendary.LegendaryTrait;
import dev.xkmc.l2hostility.content.traits.legendary.MasterTrait;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MasterTrait.class, remap = false)
public class MixinMasterTrait {

    @Inject(method = "allow", at = @At("HEAD"), cancellable = true)
    private void cnpchostility$allowOnNPC(LivingEntity le, int difficulty, int maxModLv, CallbackInfoReturnable<Boolean> cir) {
        if (NPCUtil.isNPC(le)) {
            cir.setReturnValue(true);
        }
    }
}
