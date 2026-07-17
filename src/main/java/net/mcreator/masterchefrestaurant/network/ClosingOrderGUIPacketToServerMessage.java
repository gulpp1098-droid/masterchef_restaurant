package net.mcreator.masterchefrestaurant.network;

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

import net.mcreator.masterchefrestaurant.procedures.ClosingOrderGUIPacketToServerProcedureProcedure;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

@EventBusSubscriber
public record ClosingOrderGUIPacketToServerMessage(String extradata) implements CustomPacketPayload {
	public static final Type<ClosingOrderGUIPacketToServerMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MasterchefRestaurantMod.MODID, "closing_order_gui_packet_to_server"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClosingOrderGUIPacketToServerMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ClosingOrderGUIPacketToServerMessage message) -> {
		buffer.writeUtf(message.extradata);
	}, (RegistryFriendlyByteBuf buffer) -> new ClosingOrderGUIPacketToServerMessage(buffer.readUtf()));

	@Override
	public Type<ClosingOrderGUIPacketToServerMessage> type() {
		return TYPE;
	}

	public static void handleData(final ClosingOrderGUIPacketToServerMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				Player entity = context.player();
				Level world = entity.level();
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();
				String inboundString = message.extradata;
				if (!world.hasChunkAt(entity.blockPosition()))
					return;

				ClosingOrderGUIPacketToServerProcedureProcedure.execute(entity, inboundString);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MasterchefRestaurantMod.addNetworkMessage(ClosingOrderGUIPacketToServerMessage.TYPE, ClosingOrderGUIPacketToServerMessage.STREAM_CODEC, ClosingOrderGUIPacketToServerMessage::handleData);
	}
}