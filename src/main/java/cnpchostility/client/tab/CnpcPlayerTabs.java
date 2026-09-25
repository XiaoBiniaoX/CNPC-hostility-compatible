package bin.cnpchostility.client.tab;

import dev.xkmc.l2tabs.tabs.core.TabRegistry;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class CnpcPlayerTabs {

    public static TabToken<CnpcInventoryTab> INVENTORY;
    public static TabToken<CnpcFactionsTab> FACTIONS;
    public static TabToken<CnpcQuestsTab> QUESTS;

    private CnpcPlayerTabs() {
    }

    public static void register() {
        if (INVENTORY != null) {
            return;
        }

        INVENTORY = TabRegistry.registerTab(
                5100,
                CnpcInventoryTab::new,
                () -> Blocks.CRAFTING_TABLE.asItem(),
                Component.translatable("inventory.inventory"));
        FACTIONS = TabRegistry.registerTab(
                5101,
                CnpcFactionsTab::new,
                () -> Items.RED_BANNER,
                Component.translatable("faction.factions"));
        QUESTS = TabRegistry.registerTab(
                5102,
                CnpcQuestsTab::new,
                () -> Items.BOOK,
                Component.translatable("quest.quest_log"));
        TabRegistry.refreshIndex();
    }
}
