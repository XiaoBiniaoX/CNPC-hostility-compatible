package bin.cnpchostility.mixin;

import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiNPCInterface.class, remap = false)
public interface AccessorGuiNPCInterface2 {
    @Accessor("npc")
    EntityNPCInterface cnpchostility$getNpc();
}
