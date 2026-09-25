package bin.cnpchostility.mixin;

import dev.xkmc.l2hostility.events.ClientEvents;
import net.minecraftforge.client.event.RenderNameTagEvent;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ClientEvents.class, remap = false)
public class MixinClientEvents {

    @ModifyVariable(method = "renderNameTag", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static float cnpchostility$offsetNpcTraits(float offset, RenderNameTagEvent event) {
        if (event.getEntity() instanceof EntityNPCInterface npc && npc.display.showName()) {
            offset += npc.getBbHeight() * (npc.display.getTitle().isEmpty() ? 0.15F : 0.25F);
        }
        return offset;
    }
}
