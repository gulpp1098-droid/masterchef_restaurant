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
import net.minecraft.core.SectionPos;

import net.mcreator.masterchefrestaurant.procedures.SetSpatulaLocationModeTrueProcedure;
import net.mcreator.masterchefrestaurant.procedures.OpenRestaurantButtonProcedure;
import net.mcreator.masterchefrestaurant.procedures.OpenCreateRestaurantGUIProcedure;
import net.mcreator.masterchefrestaurant.procedures.MenuPreviousPageProcedure;
import net.mcreator.masterchefrestaurant.procedures.MenuNextPageProcedure;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

@EventBusSubscriber
public record RestaurantManagementGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<RestaurantManagementGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MasterchefRestaurantMod.MODID, "restaurant_management_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RestaurantManagementGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, RestaurantManagementGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new RestaurantManagementGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<RestaurantManagementGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final RestaurantManagementGUIButtonMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> handleButtonAction(context.player(), message.buttonID, message.x, message.y, message.z)).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		// security measure to prevent arbitrary chunk generation
		if (!world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z)))
			return;
		if (buttonID == 0) {

			OpenCreateRestaurantGUIProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 1) {

			SetSpatulaLocationModeTrueProcedure.execute(world, entity);
		}
		if (buttonID == 2) {

			OpenRestaurantButtonProcedure.execute(world, entity);
		}
		if (buttonID == 3) {

			MenuNextPageProcedure.execute(world, entity);
		}
		if (buttonID == 4) {

			MenuPreviousPageProcedure.execute(world, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MasterchefRestaurantMod.addNetworkMessage(RestaurantManagementGUIButtonMessage.TYPE, RestaurantManagementGUIButtonMessage.STREAM_CODEC, RestaurantManagementGUIButtonMessage::handleData);
	}
}