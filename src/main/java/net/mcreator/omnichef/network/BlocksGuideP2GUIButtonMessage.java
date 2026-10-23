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

import net.mcreator.omnichef.procedures.ChefsDiaryGuideBlocksProcedure;
import net.mcreator.omnichef.procedures.ChefsDiaryGuideBlocksP3Procedure;
import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public record BlocksGuideP2GUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<BlocksGuideP2GUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "blocks_guide_p_2_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksGuideP2GUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, BlocksGuideP2GUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new BlocksGuideP2GUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<BlocksGuideP2GUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final BlocksGuideP2GUIButtonMessage message, final IPayloadContext context) {
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

			ChefsDiaryGuideBlocksProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 1) {

			ChefsDiaryGuideBlocksP3Procedure.execute(world, x, y, z, entity);
		}

		guiTools$enhancedImageButton : {
			if (buttonID == 3) {
				net.mcreator.omnichef.procedures.ChefsDiaryGUIOpenProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 4) {
				net.mcreator.omnichef.procedures.ChefsDiaryFoodProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 5) {
				net.mcreator.omnichef.procedures.ChefsDiaryClientsProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 6) {
				net.mcreator.omnichef.procedures.ChefsDiaryApplienceProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 7) {
				net.mcreator.omnichef.procedures.ChefsDiaryStatsProcedure.execute(world, x, y, z, entity);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(BlocksGuideP2GUIButtonMessage.TYPE, BlocksGuideP2GUIButtonMessage.STREAM_CODEC, BlocksGuideP2GUIButtonMessage::handleData);
	}
}