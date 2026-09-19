package bin.cnpchostility.util;

import net.minecraft.world.entity.LivingEntity;

public class NPCUtil {

    public static boolean isNPC(LivingEntity entity) {
        Class<?> clazz = entity.getClass();
        do {
            if (clazz.getName().equals("noppes.npcs.entity.EntityNPCInterface")) {
                return true;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return false;
    }
}
