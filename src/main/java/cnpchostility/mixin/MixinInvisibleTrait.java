package bin.cnpchostility.mixin;

import bin.cnpchostility.util.NPCUtil;
import dev.xkmc.l2hostility.content.traits.common.InvisibleTrait;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InvisibleTrait.class, remap = false)
public class MixinInvisibleTrait {

    @Inject(method = "tick", at = @At("RETURN"))
    private void cnpchostility$onTick(LivingEntity mob, int level, CallbackInfo ci) {
        if (!mob.level().isClientSide() && NPCUtil.isNPC(mob)) {
            mob.setInvisible(true);
        }
    }

}
