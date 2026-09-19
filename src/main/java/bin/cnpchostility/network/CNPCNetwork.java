package bin.cnpchostility.network;

import bin.cnpchostility.CNPCHostility;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CNPCNetwork {
    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CNPCHostility.MODID, "main"), () -> VERSION,
            VERSION::equals, VERSION::equals);

    private CNPCNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(SaveNpcTraits.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SaveNpcTraits::encode)
                .decoder(SaveNpcTraits::decode)
                .consumerMainThread(SaveNpcTraits::handle)
                .add();
    }

    public static void saveNpcTraits(int entityId, Map<ResourceLocation, Integer> traits) {
        CHANNEL.sendToServer(new SaveNpcTraits(entityId, new LinkedHashMap<>(traits)));
    }

    public record SaveNpcTraits(int entityId, Map<ResourceLocation, Integer> traits) {
        private static void encode(SaveNpcTraits message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.entityId);
            buffer.writeVarInt(message.traits.size());
            message.traits.forEach((id, level) -> {
                buffer.writeResourceLocation(id);
                buffer.writeVarInt(level);
            });
        }

        private static SaveNpcTraits decode(FriendlyByteBuf buffer) {
            int entityId = buffer.readVarInt();
            int size = Math.min(buffer.readVarInt(), 256);
            Map<ResourceLocation, Integer> traits = new LinkedHashMap<>();
            for (int i = 0; i < size; i++) {
                traits.put(buffer.readResourceLocation(), Math.max(1, Math.min(100, buffer.readVarInt())));
            }
            return new SaveNpcTraits(entityId, traits);
        }

        private static void handle(SaveNpcTraits message, Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context context = supplier.get();
            var sender = context.getSender();
            context.setPacketHandled(true);
            if (sender == null || !sender.hasPermissions(2)) return;
            context.enqueueWork(() -> {
                var entity = sender.level().getEntity(message.entityId);
                if (!(entity instanceof LivingEntity living) || sender.distanceToSqr(living) > 4096.0) return;
                Class<?> type = living.getClass();
                while (type != null && !"noppes.npcs.entity.EntityNPCInterface".equals(type.getName())) type = type.getSuperclass();
                if (type == null) return;
                var registry = dev.xkmc.l2hostility.init.registrate.LHTraits.TRAITS.get();
                living.getCapability(MobTraitCap.CAPABILITY).ifPresent(cap -> {
                    var invisible = registry.getValue(new ResourceLocation("l2hostility", "invisible"));
                    boolean wasInvisible = invisible != null && cap.hasTrait(invisible);
                    cap.traits.clear();
                    message.traits.forEach((id, level) -> {
                        var trait = registry.getValue(id);
                        if (trait != null && !"split".equals(id.getPath()) && !"growth".equals(id.getPath())) {
                            cap.traits.put(trait, level);
                        }
                    });
                    if (wasInvisible && (invisible == null || !cap.hasTrait(invisible))) {
                        living.setInvisible(false);
                    }
                    cap.syncToClient(living);
                });
            });
        }
    }
}
