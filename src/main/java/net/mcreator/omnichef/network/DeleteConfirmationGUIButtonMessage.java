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
import net.minecraft.core.SectionPos;

import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public record DeleteConfirmationGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<DeleteConfirmationGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "delete_confirmation_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, DeleteConfirmationGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, DeleteConfirmationGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new DeleteConfirmationGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<DeleteConfirmationGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final DeleteConfirmationGUIButtonMessage message, final IPayloadContext context) {
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

		guiTools$enhancedImageButton : {
			if (buttonID == 0) {
				net.mcreator.omnichef.procedures.DeleteRestaurantProcedure.execute(world, entity);
			}
			if (buttonID == 1) {
				net.mcreator.omnichef.procedures.OpenCreateRestaurantGUIProcedure.execute(world, x, y, z, entity);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(DeleteConfirmationGUIButtonMessage.TYPE, DeleteConfirmationGUIButtonMessage.STREAM_CODEC, DeleteConfirmationGUIButtonMessage::handleData);
	}
}