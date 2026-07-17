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

import net.mcreator.masterchefrestaurant.procedures.*;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

@EventBusSubscriber
public record BlocksGuideGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<BlocksGuideGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MasterchefRestaurantMod.MODID, "blocks_guide_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksGuideGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, BlocksGuideGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new BlocksGuideGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<BlocksGuideGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final BlocksGuideGUIButtonMessage message, final IPayloadContext context) {
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

			ChefsDiaryMenuProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 1) {

			ChefsDiaryFoodProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 2) {

			ChefsDiaryClientsProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 3) {

			ChefsDiaryApplienceProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 4) {

			ChefsDiaryStatsProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 5) {

			ChefsDiaryGuideBlocksP2Procedure.execute(world, x, y, z, entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		MasterchefRestaurantMod.addNetworkMessage(BlocksGuideGUIButtonMessage.TYPE, BlocksGuideGUIButtonMessage.STREAM_CODEC, BlocksGuideGUIButtonMessage::handleData);
	}
}