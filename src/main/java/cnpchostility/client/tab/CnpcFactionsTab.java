package bin.cnpchostility.client.tab;

import dev.xkmc.l2tabs.tabs.core.BaseTab;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.client.gui.player.tabs.InventoryTabFactions;

public final class CnpcFactionsTab extends BaseTab<CnpcFactionsTab> {

    public CnpcFactionsTab(
            TabToken<CnpcFactionsTab> token,
            TabManager manager,
            ItemStack stack,
            Component title) {
        super(token, manager, stack, title);
    }

    @Override
    public void onTabClicked() {
        new InventoryTabFactions().onTabClicked();
    }
}
