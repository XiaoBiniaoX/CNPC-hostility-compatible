package bin.cnpchostility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderCustomNpc.class, remap = false)
public class MixinRenderCustomNpc {

    @Shadow
    private EntityNPCInterface npc;

    @Inject(method = "render", at = @At("HEAD"), remap = false)
    private void cnpchostility$preRender(EntityCustomNpc entity, float entityYaw, float partialTicks,
                                          PoseStack matrixStack, MultiBufferSource buffer,
                                          int packedLight, CallbackInfo ci) {
        this.npc = entity;
        System.out.println("[CNPCHostility] RenderCustomNpc.render called for: " + entity);
    }

    @Redirect(
        method = "render",
        at = @At(value = "INVOKE",
                 target = "Lnoppes/npcs/entity/data/DataDisplay;isVisibleTo(Lnet/minecraft/world/entity/player/Player;)Z"),
        remap = false,
        require = 0
    )
    private boolean cnpchostility$redirectIsVisibleTo(DataDisplay display, Player player) {
        if (this.npc != null && this.npc.hasEffect(MobEffects.INVISIBILITY)) {
            System.out.println("[CNPCHostility] NPC has INVISIBILITY, forcing visible for render");
            return true;
        }
        return display.isVisibleTo(player);
    }
}
