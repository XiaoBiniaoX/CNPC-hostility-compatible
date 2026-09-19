package bin.cnpchostility.handler;

import bin.cnpchostility.CNPCHostility;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.init.data.LHConfig;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CNPCHostility.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CNPCFieryHandler {

    private static boolean isNPC(LivingEntity entity) {
        Class<?> clazz = entity.getClass();
        do {
            if (clazz.getName().equals("noppes.npcs.entity.EntityNPCInterface")) {
                return true;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return false;
    }

    private static boolean hasFiery(LivingEntity entity) {
        var opt = entity.getCapability(MobTraitCap.CAPABILITY).resolve();
        if (opt.isEmpty()) return false;
        for (var entry : opt.get().traits.entrySet()) {
            if ("l2hostility:fiery".equals(entry.getKey().getID())) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onNPCAttack(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;
        if (event.getAmount() <= 0) return;
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity) event.getSource().getDirectEntity();
        if (!isNPC(attacker)) return;
        if (!hasFiery(attacker)) return;

        target.setSecondsOnFire(LHConfig.COMMON.fieryTime.get());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurtLowest(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            if (isNPC(attacker) && hasFiery(attacker)) {
                attacker.setRemainingFireTicks(0);
            }
        }
    }

    @SubscribeEvent
    public static void onNPCAttacked(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!isNPC(entity)) return;
        if (!hasFiery(entity)) return;

        if (event.getSource().getDirectEntity() instanceof LivingEntity) {
            ((LivingEntity) event.getSource().getDirectEntity()).setSecondsOnFire(LHConfig.COMMON.fieryTime.get());
        }
        if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            event.setCanceled(true);
        }
        entity.setRemainingFireTicks(0);
    }
}
