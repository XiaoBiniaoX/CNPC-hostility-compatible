package bin.cnpchostility.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "net.kayn.fallen_traits.content.traits.basic.CloneTrait", remap = false)
public class MixinFallenCloneTrait {

    @Redirect(
            method = "spawnClone",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;m_20615_(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;",
                    remap = false
            )
    )
    private Entity cnpchostility$copyNpcData(
            EntityType<?> type,
            Level level,
            Mob original,
            ServerLevel serverLevel) {
        Entity clone = type.create(level);
        if (original instanceof EntityNPCInterface && clone instanceof EntityNPCInterface cloneNpc) {
            CompoundTag tag = original.saveWithoutId(new CompoundTag());
            cloneNpc.readAdditionalSaveData(tag);
        }
        return clone;
    }
}
