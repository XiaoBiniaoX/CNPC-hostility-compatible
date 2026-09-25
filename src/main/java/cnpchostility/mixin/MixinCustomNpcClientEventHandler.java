package bin.cnpchostility.mixin;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraftforge.client.event.ScreenEvent;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.gui.player.tabs.InventoryTabFactions;
import noppes.npcs.client.gui.player.tabs.InventoryTabQuests;
import noppes.npcs.client.gui.player.tabs.InventoryTabVanilla;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ClientEventHandler.class, remap = false)
public class MixinCustomNpcClientEventHandler {

    @Redirect(
            method = "guiPostInit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/event/ScreenEvent$Init$Post;addListener(Lnet/minecraft/client/gui/components/events/GuiEventListener;)V"
            )
    )
    private void cnpchostility$replacePlayerTabsWithL2Tabs(
            ScreenEvent.Init.Post event,
            GuiEventListener listener) {
        if (listener instanceof InventoryTabVanilla
                || listener instanceof InventoryTabFactions
                || listener instanceof InventoryTabQuests) {
            return;
        }
        event.addListener(listener);
    }
}
