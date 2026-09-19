package bin.cnpchostility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.effect.MobEffects;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderNPCInterface.class, remap = false)
public class MixinRenderNPCInterface {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cnpchostility$skipInvisibleNpc(
            EntityNPCInterface npc,
            float entityYaw,
            float partialTicks,
            PoseStack matrixStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci) {
        boolean traitInvisible = npc.getCapability(MobTraitCap.CAPABILITY)
                .map(cap -> cap.traits.keySet().stream()
                        .anyMatch(trait -> "l2hostility:invisible".equals(trait.getID())))
                .orElse(false);
        if (npc.hasEffect(MobEffects.INVISIBILITY) || traitInvisible) {
            ci.cancel();
        }
    }
}
