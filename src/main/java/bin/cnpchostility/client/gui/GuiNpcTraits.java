package bin.cnpchostility.client.gui;

import bin.cnpchostility.network.CNPCNetwork;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuiNpcTraits extends GuiNPCInterface {
    private static final int ROW_HEIGHT = 24;
    private static final int COLUMNS = 2;
    private static final int COLUMN_GAP = 4;
    private static final Path FAVORITES_FILE = Minecraft.getInstance().gameDirectory.toPath()
            .resolve("config").resolve("cnpchostility-trait-favorites.txt");
    private final EntityNPCInterface npc;
    private final List<MobTrait> traits = new ArrayList<>();
    private final Map<ResourceLocation, Integer> selected = new LinkedHashMap<>();
    private final Set<ResourceLocation> favorites = new HashSet<>();
    private int scroll;

    public GuiNpcTraits(EntityNPCInterface npc) {
        super(npc);
        this.npc = npc;
        this.imageWidth = 320;
        this.imageHeight = 220;
        this.drawDefaultBackground = false;
        loadFavorites();
        dev.xkmc.l2hostility.init.registrate.LHTraits.TRAITS.get().getValues()
                .forEach(this.traits::add);
        sortTraits();
        npc.getCapability(MobTraitCap.CAPABILITY).ifPresent(cap -> cap.traits.forEach((trait, level) -> {
            ResourceLocation id = ResourceLocation.tryParse(trait.getID());
            if (id != null) {
                this.selected.put(id, level);
            }
        }));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(this.guiLeft, this.guiTop, this.guiLeft + this.imageWidth, this.guiTop + this.imageHeight, 0xFF373737);
        graphics.fill(this.guiLeft + 1, this.guiTop + 1, this.guiLeft + this.imageWidth - 1, this.guiTop + this.imageHeight - 1, 0xFFC6C6C6);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, Component.translatable("gui.cnpchostility.traits.title"), this.width / 2, this.guiTop + 8, 0x404040);

        int top = this.guiTop + 25;
        int bottom = this.guiTop + this.imageHeight - 25;
        int columnWidth = (this.imageWidth - 40 - COLUMN_GAP) / COLUMNS;
        graphics.fill(this.guiLeft + 8, top - 4, this.guiLeft + this.imageWidth - 14, bottom + 4, 0xFF373737);
        graphics.fill(this.guiLeft + 9, top - 3, this.guiLeft + this.imageWidth - 15, bottom + 3, 0xFF8B8B8B);
        List<Component> hoveredTooltip = null;
        graphics.enableScissor(this.guiLeft + 8, top, this.guiLeft + this.imageWidth - 14, bottom);
        for (int i = 0; i < this.traits.size(); i++) {
            MobTrait trait = this.traits.get(i);
            ResourceLocation id = ResourceLocation.tryParse(trait.getID());
            if (id == null) {
                continue;
            }
            int column = i % COLUMNS;
            int row = i / COLUMNS;
            int x = this.guiLeft + 12 + column * (columnWidth + COLUMN_GAP);
            int y = top + row * ROW_HEIGHT - this.scroll;
            if (y + ROW_HEIGHT < top || y > bottom) {
                continue;
            }
            boolean disabled = isDisabled(id);
            boolean active = this.selected.containsKey(id);
            int border = this.favorites.contains(id) ? 0xFFFFD700 : active ? 0xFFFFFFFF : 0xFFA0A0A0;
            int color = disabled ? 0xFF202020 : active ? 0xFF6A6A6A : 0xFFC6C6C6;
            graphics.fill(x, y, x + columnWidth, y + 21, border);
            graphics.fill(x + 1, y + 1, x + columnWidth - 1, y + 20, color);
            ItemStack icon = traitItem(id);
            if (!icon.isEmpty()) {
                graphics.renderItem(icon, x + 3, y + 2);
            }
            graphics.drawString(this.font, trait.getDesc(), x + 24, y + 7,
                    disabled ? 0x606060 : active ? 0xFFFFFF : 0x404040, false);
            if (active && !disabled) {
                String level = "Lv." + this.selected.get(id);
                graphics.drawString(this.font, level, x + columnWidth - this.font.width(level) - 5, y + 7, 0xFFFF80, false);
            }
            if (mouseX >= x && mouseX < x + columnWidth && mouseY >= y && mouseY < y + 21) {
                hoveredTooltip = new ArrayList<>();
                hoveredTooltip.add(trait.getFullDesc(this.selected.get(id)));
                if (disabled) {
                    hoveredTooltip.add(Component.translatable("gui.cnpchostility.traits.disabled"));
                } else {
                    trait.addDetail(hoveredTooltip);
                    if (!I18n.exists(trait.getDescriptionId() + ".desc")) {
                        hoveredTooltip.add(Component.translatable("gui.cnpchostility.traits.default_desc"));
                    }
                    hoveredTooltip.add(Component.translatable("gui.cnpchostility.traits.remove_hint"));
                }
            }
        }
        graphics.disableScissor();
        drawScrollbar(graphics, top, bottom);
        if (hoveredTooltip != null) {
            graphics.renderComponentTooltip(this.font, hoveredTooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int top = this.guiTop + 25;
        int bottom = this.guiTop + this.imageHeight - 25;
        int columnWidth = (this.imageWidth - 40 - COLUMN_GAP) / COLUMNS;
        int relativeX = (int) mouseX - (this.guiLeft + 12);
        int column = relativeX / (columnWidth + COLUMN_GAP);
        if (column >= 0 && column < COLUMNS
                && relativeX % (columnWidth + COLUMN_GAP) < columnWidth
                && mouseY >= top && mouseY < bottom) {
            int row = (int) (mouseY - top + this.scroll) / ROW_HEIGHT;
            int index = row * COLUMNS + column;
            if (index >= 0 && index < this.traits.size()) {
                ResourceLocation id = ResourceLocation.tryParse(this.traits.get(index).getID());
                if (id != null && !isDisabled(id)) {
                    if (button == 0 && Screen.hasAltDown()) {
                        if (!this.favorites.add(id)) {
                            this.favorites.remove(id);
                        }
                        saveFavorites();
                        sortTraits();
                        return true;
                    }
                    if (button == 0 && Screen.hasShiftDown()) {
                        this.selected.remove(id);
                        CNPCNetwork.saveNpcTraits(this.npc.getId(), this.selected);
                    } else if (button == 1) {
                        int level = this.selected.getOrDefault(id, 0) - 1;
                        if (level <= 0) {
                            this.selected.remove(id);
                        } else {
                            this.selected.put(id, level);
                        }
                    } else {
                        this.selected.put(id, Math.min(100, this.selected.getOrDefault(id, 0) + 1));
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int rows = (this.traits.size() + COLUMNS - 1) / COLUMNS;
        int max = Math.max(0, rows * ROW_HEIGHT - (this.imageHeight - 50));
        this.scroll = Math.max(0, Math.min(max, this.scroll + (delta < 0 ? ROW_HEIGHT : -ROW_HEIGHT)));
        return true;
    }

    @Override
    public void save() {
        CNPCNetwork.saveNpcTraits(this.npc.getId(), this.selected);
    }

    private void drawScrollbar(GuiGraphics graphics, int top, int bottom) {
        int visible = bottom - top;
        int content = ((this.traits.size() + COLUMNS - 1) / COLUMNS) * ROW_HEIGHT;
        int x = this.guiLeft + this.imageWidth - 19;
        graphics.fill(x, top, x + 6, bottom, 0xFF101010);
        if (content > visible) {
            int thumb = Math.max(16, visible * visible / content);
            int y = top + this.scroll * (visible - thumb) / (content - visible);
            graphics.fill(x + 1, y, x + 5, y + thumb, 0xFFAAAAAA);
        }
    }

    private static boolean isDisabled(ResourceLocation id) {
        return "l2hostility".equals(id.getNamespace())
                && ("split".equals(id.getPath()) || "growth".equals(id.getPath()));
    }

    private void sortTraits() {
        this.traits.sort(Comparator
                .comparingInt((MobTrait trait) -> {
                    ResourceLocation id = ResourceLocation.tryParse(trait.getID());
                    if (id != null && isDisabled(id)) {
                        return 2;
                    }
                    return id != null && this.favorites.contains(id) ? 0 : 1;
                })
                .thenComparing(MobTrait::getID));
    }

    private static ItemStack traitItem(ResourceLocation id) {
        var item = ForgeRegistries.ITEMS.getValue(id);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private void loadFavorites() {
        if (!Files.isRegularFile(FAVORITES_FILE)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(FAVORITES_FILE, StandardCharsets.UTF_8)) {
                ResourceLocation id = ResourceLocation.tryParse(line.trim());
                if (id != null) {
                    this.favorites.add(id);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void saveFavorites() {
        try {
            Files.createDirectories(FAVORITES_FILE.getParent());
            List<String> ids = this.favorites.stream().map(ResourceLocation::toString).sorted().toList();
            Files.write(FAVORITES_FILE, ids, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }
}
