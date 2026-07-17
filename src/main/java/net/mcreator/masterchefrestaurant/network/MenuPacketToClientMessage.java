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

import net.mcreator.masterchefrestaurant.procedures.MenuPacketToClientProcedureProcedure;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

@EventBusSubscriber
public record MenuPacketToClientMessage(String extradata) implements CustomPacketPayload {
	public static final Type<MenuPacketToClientMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MasterchefRestaurantMod.MODID, "menu_packet_to_client"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MenuPacketToClientMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, MenuPacketToClientMessage message) -> {
		buffer.writeUtf(message.extradata);
	}, (RegistryFriendlyByteBuf buffer) -> new MenuPacketToClientMessage(buffer.readUtf()));

	@Override
	public Type<MenuPacketToClientMessage> type() {
		return TYPE;
	}

	public static void handleData(final MenuPacketToClientMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND) {
			context.enqueueWork(() -> {
				Player entity = context.player();
				Level world = entity.level();
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();
				String inboundString = message.extradata;

				MenuPacketToClientProcedureProcedure.execute(entity, inboundString);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MasterchefRestaurantMod.addNetworkMessage(MenuPacketToClientMessage.TYPE, MenuPacketToClientMessage.STREAM_CODEC, MenuPacketToClientMessage::handleData);
	}
}