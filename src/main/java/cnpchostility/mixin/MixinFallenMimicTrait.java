package bin.cnpchostility.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "net.kayn.fallen_traits.content.traits.legendary.MimicTrait", remap = false)
public class MixinFallenMimicTrait {

    @Redirect(
            method = "postInit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;m_8061_(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V",
                    remap = false
            )
    )
    private void cnpchostility$refreshNpcEquipment(
            LivingEntity entity,
            EquipmentSlot slot,
            ItemStack stack) {
        entity.setItemSlot(slot, stack);
        if (entity instanceof EntityNPCInterface npc) {
            npc.updateClient = true;
        }
    }
}
