package bin.cnpchostility.mixin;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "noppes.npcs.entity.EntityNPCInterface", remap = false)
public class MixinEntityNPCInterface {

    @Inject(method = {"tick", "m_8119_"}, at = @At("TAIL"), require = 0)
    private void cnpchostility$clearStaleInvisibleFlag(CallbackInfo ci) {
        LivingEntity target = (LivingEntity) (Object) this;
        if (target.level().isClientSide() || !target.isInvisible() || target.hasEffect(MobEffects.INVISIBILITY)) return;
        boolean hasInvisibleTrait = target.getCapability(MobTraitCap.CAPABILITY)
                .map(cap -> cap.traits.keySet().stream()
                        .anyMatch(trait -> "l2hostility:invisible".equals(trait.getID())))
                .orElse(false);
        if (!hasInvisibleTrait) target.setInvisible(false);
    }

    @Inject(method = {"mobInteract", "m_6071_"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void cnpchostility$mobInteract(
            Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        cnpchostility$handleInstall(player, hand, cir);
    }

    @Inject(method = {"hurt", "m_6469_"}, at = @At("HEAD"), cancellable = true, require = 0)
    private void cnpchostility$hurt(
            DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        cnpchostility$handleRemoveByAttack(source, amount, cir);
    }

    @Inject(method = "reset", at = @At("HEAD"), require = 0)
    private void cnpchostility$onReset(CallbackInfo ci) {
        LivingEntity target = (LivingEntity) (Object) this;
        target.getTags().remove("cnpchostility:lord_summoned");
    }

    @Unique
    private void cnpchostility$handleInstall(
            Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {

        if (hand != InteractionHand.MAIN_HAND) return;
        if (player.level().isClientSide()) return;
        if (!player.isCreative()) return;

        ItemStack stack = player.getItemInHand(hand);
        MobTrait trait = cnpchostility$getTraitFromItem(stack);
        if (trait == null) return;

        if (cnpchostility$isSplitTrait(trait)) {
            player.sendSystemMessage(
                    Component.translatable("message.cnpchostility.split_denied")
                            .withStyle(ChatFormatting.RED));
            cir.setReturnValue(InteractionResult.FAIL);
            return;
        }

        LivingEntity target = (LivingEntity) (Object) this;
        var opt = target.getCapability(MobTraitCap.CAPABILITY).resolve();
        if (opt.isEmpty()) return;

        MobTraitCap cap = opt.get();
        int maxLevel = trait.getMaxLevel();

        if (cap.traits.containsKey(trait)) {
            int current = cap.traits.get(trait);
            if (current >= maxLevel) {
                player.sendSystemMessage(
                        Component.translatable("message.cnpchostility.add_max",
                                trait.getDesc(), target.getDisplayName(), maxLevel
                        ).withStyle(ChatFormatting.GOLD));
            } else {
                int newLevel = current + 1;
                cap.traits.put(trait, newLevel);
                cap.syncToClient(target);
                target.setHealth(target.getMaxHealth());
                player.sendSystemMessage(
                        Component.translatable("message.cnpchostility.add_upgrade",
                                trait.getDesc(), target.getDisplayName(), current, newLevel
                        ).withStyle(ChatFormatting.GREEN));
            }
        } else {
            int level = 1;
            cap.traits.put(trait, level);
            trait.initialize(target, level);
            trait.postInit(target, level);
            cap.syncToClient(target);
            target.setHealth(target.getMaxHealth());
            if (maxLevel > 1) {
                player.sendSystemMessage(
                        Component.translatable("message.cnpchostility.add_success_lv",
                                trait.getDesc(), target.getDisplayName(), level, maxLevel
                        ).withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(
                        Component.translatable("message.cnpchostility.add_success",
                                trait.getDesc(), target.getDisplayName()
                        ).withStyle(ChatFormatting.GREEN));
            }
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Unique
    private void cnpchostility$handleRemoveByAttack(
            DamageSource source, float amount,
            CallbackInfoReturnable<Boolean> cir) {

        if (!(source.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!player.isCreative()) return;

        ItemStack stack = player.getMainHandItem();
        MobTrait trait = cnpchostility$getTraitFromItem(stack);
        if (trait == null) return;

        LivingEntity target = (LivingEntity) (Object) this;
        var opt = target.getCapability(MobTraitCap.CAPABILITY).resolve();
        if (opt.isEmpty()) return;

        MobTraitCap cap = opt.get();

        if (!cap.traits.containsKey(trait)) {
            player.sendSystemMessage(
                    Component.translatable("message.cnpchostility.remove_fail",
                            trait.getDesc(), target.getDisplayName()
                    ).withStyle(ChatFormatting.RED));
            cir.setReturnValue(false);
            return;
        }

        int oldLevel = cap.traits.remove(trait);
        cap.syncToClient(target);
        target.setHealth(target.getMaxHealth());
        player.sendSystemMessage(
                Component.translatable("message.cnpchostility.remove_success",
                        trait.getDesc(), target.getDisplayName(), oldLevel
                ).withStyle(ChatFormatting.GREEN));

        cir.setReturnValue(false);
    }

    @Unique
    private static boolean cnpchostility$isSplitTrait(MobTrait trait) {
        return trait != null && "l2hostility:split".equals(trait.getID());
    }

    @Unique
    private static MobTrait cnpchostility$getTraitFromItem(ItemStack stack) {
        if (stack.isEmpty()) return null;
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return null;
        if (!id.getNamespace().equals("l2hostility")) return null;
        var traitsRegistry = dev.xkmc.l2hostility.init.registrate.LHTraits.TRAITS.get();
        return traitsRegistry.getValue(id);
    }
}
