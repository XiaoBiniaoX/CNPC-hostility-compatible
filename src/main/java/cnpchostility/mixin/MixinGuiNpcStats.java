package bin.cnpchostility.mixin;

import bin.cnpchostility.client.gui.GuiNpcTraits;
import noppes.npcs.client.gui.mainmenu.GuiNpcStats;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiNpcStats.class, remap = false)
public abstract class MixinGuiNpcStats {
    @Unique
    private static final int CNPCHOSTILITY_TRAITS_BUTTON = 2200;

    @Inject(method = "m_7856_", at = @At("TAIL"))
    private void cnpchostility$addTraitsButton(CallbackInfo ci) {
        GuiNpcStats screen = (GuiNpcStats) (Object) this;
        screen.addButton(new GuiButtonNop(
                screen,
                CNPCHOSTILITY_TRAITS_BUTTON,
                screen.guiLeft + 355,
                screen.guiTop + 186,
                56,
                20,
                "gui.cnpchostility.traits"
        ));
    }

    @Inject(method = "buttonEvent", at = @At("HEAD"), cancellable = true)
    private void cnpchostility$openTraits(GuiButtonNop button, CallbackInfo ci) {
        if (button.id == CNPCHOSTILITY_TRAITS_BUTTON) {
            GuiNpcStats screen = (GuiNpcStats) (Object) this;
            screen.setSubGui(new GuiNpcTraits(((AccessorGuiNPCInterface2) screen).cnpchostility$getNpc()));
            ci.cancel();
        }
    }
}
