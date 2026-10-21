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
public record TradeBoardGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<TradeBoardGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "trade_board_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, TradeBoardGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, TradeBoardGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new TradeBoardGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<TradeBoardGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final TradeBoardGUIButtonMessage message, final IPayloadContext context) {
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
			if (buttonID == 1) {
				net.mcreator.omnichef.procedures.SelectTradeBoardOffer1Procedure.execute(world, entity);
			}
			if (buttonID == 2) {
				net.mcreator.omnichef.procedures.SelectTradeBoardOffer2Procedure.execute(world, entity);
			}
			if (buttonID == 3) {
				net.mcreator.omnichef.procedures.SelectTradeBoardOffer3Procedure.execute(world, entity);
			}
			if (buttonID == 4) {
				net.mcreator.omnichef.procedures.SelectTradeBoardOffer4Procedure.execute(world, entity);
			}
			if (buttonID == 5) {
				net.mcreator.omnichef.procedures.SelectTradeBoardOffer5Procedure.execute(world, entity);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(TradeBoardGUIButtonMessage.TYPE, TradeBoardGUIButtonMessage.STREAM_CODEC, TradeBoardGUIButtonMessage::handleData);
	}
}