package bin.cnpchostility.mixin;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 注入 MobTraitCap.tick()，为 CNPC 实体提供特殊处理：
 * - 跳过自动初始化（不生成随机词条）
 * - 阻止 OwnableEntity 检查清除词条
 * - 阻止 banned 检查移除词条
 */
@Mixin(value = MobTraitCap.class, remap = false)
public class MixinMobTraitCap {

    @Shadow(remap = false)
    private MobTraitCap.Stage stage;

    /**
     * ThreadLocal 标记当前 tick() 调用是否针对 NPC 实体。
     * 配合下方 Redirect 使用。
     */
    @Unique
    private static final ThreadLocal<Boolean> IS_NPC_TICK = ThreadLocal.withInitial(() -> false);

    @Unique
    private static boolean cnpchostility$isNPC(LivingEntity entity) {
        Class<?> clazz = entity.getClass();
        do {
            if (clazz.getName().equals("noppes.npcs.entity.EntityNPCInterface")) {
                return true;
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return false;
    }

    /**
     * 在 tick() 头部注入：对 NPC 实体设置 stage=POST_INIT，
     * 跳过自动初始化，并标记 ThreadLocal。
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void cnpchostility$beforeTick(LivingEntity mob, CallbackInfo ci) {
        if (cnpchostility$isNPC(mob)) {
            IS_NPC_TICK.set(true);
            // 强制设置 stage 为 POST_INIT，使其跳过 init() 和 post init 流程
            if (stage != MobTraitCap.Stage.POST_INIT) {
                stage = MobTraitCap.Stage.POST_INIT;
            }
        } else {
            IS_NPC_TICK.set(false);
        }
    }

    /**
     * 在 tick() 末尾清理 ThreadLocal 标记。
     */
    @Inject(method = "tick", at = @At("RETURN"))
    private void cnpchostility$afterTick(LivingEntity mob, CallbackInfo ci) {
        IS_NPC_TICK.remove();
    }

    /**
     * 重定向 MobTrait.isBanned()：对 NPC 实体永远返回 false，
     * 防止词条因 banned 检查被移除。
     */
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Ldev/xkmc/l2hostility/content/traits/base/MobTrait;isBanned()Z")
    )
    private boolean cnpchostility$redirectIsBanned(MobTrait instance) {
        if (IS_NPC_TICK.get()) {
            return false;
        }
        return instance.isBanned();
    }

    /**
     * 重定向 traits.clear()：阻止 OwnableEntity 检查清除 NPC 的词条。
     * tick() 方法中仅有一处 clear() 调用（在 OwnableEntity 检查块中）。
     */
    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Ljava/util/LinkedHashMap;clear()V")
    )
    private void cnpchostility$redirectClear(java.util.LinkedHashMap<?, ?> map) {
        if (!IS_NPC_TICK.get()) {
            map.clear();
        }
    }
}
