package bin.cnpchostility.handler;

import bin.cnpchostility.CNPCHostility;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CNPCHostility.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CNPCLordHandler {

    private static final String LORD_TAG = "cnpchostility:lord_summoned";
    private static final String MINION_TAG = "cnpchostility:lord_minion";

    private static final ArmorMaterial[] ARMOR_TIERS = {
            ArmorMaterials.LEATHER,
            ArmorMaterials.GOLD,
            ArmorMaterials.CHAIN,
            ArmorMaterials.IRON,
            ArmorMaterials.DIAMOND,
            ArmorMaterials.NETHERITE
    };

    private static final Tiers[] WEAPON_TIERS = {
            Tiers.WOOD,
            Tiers.GOLD,
            Tiers.STONE,
            Tiers.IRON,
            Tiers.DIAMOND,
            Tiers.NETHERITE
    };

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

    private static boolean hasMaster(LivingEntity entity) {
        var opt = entity.getCapability(MobTraitCap.CAPABILITY).resolve();
        if (opt.isEmpty()) return false;
        for (var entry : opt.get().traits.entrySet()) {
            if ("l2hostility:master".equals(entry.getKey().getID())) return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (event.getAmount() <= 0) return;
        if (!isNPC(entity)) return;
        if (!hasMaster(entity)) return;
        if (entity.getTags().contains(LORD_TAG)) return;

        entity.addTag(LORD_TAG);

        Level level = entity.level();
        RandomSource random = entity.getRandom();

        for (int i = 0; i < 5; i++) {
            Zombie zombie = new Zombie(level);
            zombie.setPos(
                    entity.getX() + random.nextInt(7) - 3,
                    entity.getY(),
                    entity.getZ() + random.nextInt(7) - 3
            );

            equipWithRandomGear(zombie, random);
            zombie.addTag(MINION_TAG);

            if (entity.getLastAttacker() instanceof LivingEntity) {
                zombie.setTarget((LivingEntity) entity.getLastAttacker());
            }

            level.addFreshEntity(zombie);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (attacker == null) return;
        if (attacker.getTags().contains(MINION_TAG) && event.getEntity().getTags().contains(LORD_TAG)) {
            event.setCanceled(true);
        }
    }

    private static void equipWithRandomGear(Zombie zombie, RandomSource random) {
        zombie.setItemSlot(EquipmentSlot.HEAD, makeArmor(ARMOR_TIERS[random.nextInt(ARMOR_TIERS.length)], ArmorItem.Type.HELMET));
        zombie.setItemSlot(EquipmentSlot.CHEST, makeArmor(ARMOR_TIERS[random.nextInt(ARMOR_TIERS.length)], ArmorItem.Type.CHESTPLATE));
        zombie.setItemSlot(EquipmentSlot.LEGS, makeArmor(ARMOR_TIERS[random.nextInt(ARMOR_TIERS.length)], ArmorItem.Type.LEGGINGS));
        zombie.setItemSlot(EquipmentSlot.FEET, makeArmor(ARMOR_TIERS[random.nextInt(ARMOR_TIERS.length)], ArmorItem.Type.BOOTS));

        zombie.setItemSlot(EquipmentSlot.MAINHAND, makeWeapon(WEAPON_TIERS[random.nextInt(WEAPON_TIERS.length)], random));

        if (random.nextBoolean()) {
            Tiers offTier = WEAPON_TIERS[random.nextInt(WEAPON_TIERS.length)];
            if (random.nextBoolean()) {
                zombie.setItemSlot(EquipmentSlot.OFFHAND, makeWeapon(offTier, random));
            } else {
                zombie.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
            }
        }

        zombie.setDropChance(EquipmentSlot.HEAD, 0.0F);
        zombie.setDropChance(EquipmentSlot.CHEST, 0.0F);
        zombie.setDropChance(EquipmentSlot.LEGS, 0.0F);
        zombie.setDropChance(EquipmentSlot.FEET, 0.0F);
        zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        zombie.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    private static ItemStack makeArmor(ArmorMaterial material, ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> switch ((ArmorMaterials) material) {
                case LEATHER -> new ItemStack(Items.LEATHER_HELMET);
                case GOLD -> new ItemStack(Items.GOLDEN_HELMET);
                case CHAIN -> new ItemStack(Items.CHAINMAIL_HELMET);
                case IRON -> new ItemStack(Items.IRON_HELMET);
                case DIAMOND -> new ItemStack(Items.DIAMOND_HELMET);
                case NETHERITE -> new ItemStack(Items.NETHERITE_HELMET);
                default -> new ItemStack(Items.LEATHER_HELMET);
            };
            case CHESTPLATE -> switch ((ArmorMaterials) material) {
                case LEATHER -> new ItemStack(Items.LEATHER_CHESTPLATE);
                case GOLD -> new ItemStack(Items.GOLDEN_CHESTPLATE);
                case CHAIN -> new ItemStack(Items.CHAINMAIL_CHESTPLATE);
                case IRON -> new ItemStack(Items.IRON_CHESTPLATE);
                case DIAMOND -> new ItemStack(Items.DIAMOND_CHESTPLATE);
                case NETHERITE -> new ItemStack(Items.NETHERITE_CHESTPLATE);
                default -> new ItemStack(Items.LEATHER_CHESTPLATE);
            };
            case LEGGINGS -> switch ((ArmorMaterials) material) {
                case LEATHER -> new ItemStack(Items.LEATHER_LEGGINGS);
                case GOLD -> new ItemStack(Items.GOLDEN_LEGGINGS);
                case CHAIN -> new ItemStack(Items.CHAINMAIL_LEGGINGS);
                case IRON -> new ItemStack(Items.IRON_LEGGINGS);
                case DIAMOND -> new ItemStack(Items.DIAMOND_LEGGINGS);
                case NETHERITE -> new ItemStack(Items.NETHERITE_LEGGINGS);
                default -> new ItemStack(Items.LEATHER_LEGGINGS);
            };
            case BOOTS -> switch ((ArmorMaterials) material) {
                case LEATHER -> new ItemStack(Items.LEATHER_BOOTS);
                case GOLD -> new ItemStack(Items.GOLDEN_BOOTS);
                case CHAIN -> new ItemStack(Items.CHAINMAIL_BOOTS);
                case IRON -> new ItemStack(Items.IRON_BOOTS);
                case DIAMOND -> new ItemStack(Items.DIAMOND_BOOTS);
                case NETHERITE -> new ItemStack(Items.NETHERITE_BOOTS);
                default -> new ItemStack(Items.LEATHER_BOOTS);
            };
        };
    }

    private static ItemStack makeWeapon(Tiers tier, RandomSource random) {
        if (random.nextBoolean()) {
            return switch ((Tiers) tier) {
                case WOOD -> new ItemStack(Items.WOODEN_SWORD);
                case GOLD -> new ItemStack(Items.GOLDEN_SWORD);
                case STONE -> new ItemStack(Items.STONE_SWORD);
                case IRON -> new ItemStack(Items.IRON_SWORD);
                case DIAMOND -> new ItemStack(Items.DIAMOND_SWORD);
                case NETHERITE -> new ItemStack(Items.NETHERITE_SWORD);
                default -> new ItemStack(Items.IRON_SWORD);
            };
        } else {
            return switch ((Tiers) tier) {
                case WOOD -> new ItemStack(Items.WOODEN_AXE);
                case GOLD -> new ItemStack(Items.GOLDEN_AXE);
                case STONE -> new ItemStack(Items.STONE_AXE);
                case IRON -> new ItemStack(Items.IRON_AXE);
                case DIAMOND -> new ItemStack(Items.DIAMOND_AXE);
                case NETHERITE -> new ItemStack(Items.NETHERITE_AXE);
                default -> new ItemStack(Items.IRON_AXE);
            };
        }
    }
}
