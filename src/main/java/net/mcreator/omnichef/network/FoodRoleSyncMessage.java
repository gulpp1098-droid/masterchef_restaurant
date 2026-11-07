package net.mcreator.omnichef.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;

import net.mcreator.omnichef.procedures.ReceiveFoodRolesProcedure;
import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public record FoodRoleSyncMessage(String extradata) implements CustomPacketPayload {
	public static final Type<FoodRoleSyncMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "food_role_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, FoodRoleSyncMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, FoodRoleSyncMessage message) -> {
		buffer.writeUtf(message.extradata);
	}, (RegistryFriendlyByteBuf buffer) -> new FoodRoleSyncMessage(buffer.readUtf()));

	@Override
	public Type<FoodRoleSyncMessage> type() {
		return TYPE;
	}

	public static void handleData(final FoodRoleSyncMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND) {
			context.enqueueWork(() -> {
				Player entity = context.player();
				Level world = entity.level();
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();
				String inboundString = message.extradata;

				ReceiveFoodRolesProcedure.execute(inboundString);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(FoodRoleSyncMessage.TYPE, FoodRoleSyncMessage.STREAM_CODEC, FoodRoleSyncMessage::handleData);
	}
}