package bin.cnpchostility;

import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.WeakHashMap;

public final class NpcDeathContext {

    private static final Map<LivingEntity, Snapshot> SNAPSHOTS = new WeakHashMap<>();

    private NpcDeathContext() {
    }

    public static void capture(LivingEntity entity, boolean curseAtDeath) {
        SNAPSHOTS.put(entity, new Snapshot(entity.tickCount, curseAtDeath));
    }

    public static boolean consumeCurseAtDeath(LivingEntity entity) {
        Snapshot snapshot = SNAPSHOTS.remove(entity);
        return snapshot != null && snapshot.tick() == entity.tickCount && snapshot.curseAtDeath();
    }

    public static void clear(LivingEntity entity) {
        SNAPSHOTS.remove(entity);
    }

    private record Snapshot(int tick, boolean curseAtDeath) {
    }
}
