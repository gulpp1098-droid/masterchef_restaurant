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

import net.mcreator.omnichef.procedures.ChefsDiaryGuideBlocksP5Procedure;
import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public record BlocksGuideP6GUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<BlocksGuideP6GUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "blocks_guide_p_6_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksGuideP6GUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, BlocksGuideP6GUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new BlocksGuideP6GUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<BlocksGuideP6GUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final BlocksGuideP6GUIButtonMessage message, final IPayloadContext context) {
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

			ChefsDiaryGuideBlocksP5Procedure.execute(world, x, y, z, entity);
		}

		guiTools$enhancedImageButton : {
			if (buttonID == 1) {
				net.mcreator.omnichef.procedures.ChefsDiaryGUIOpenProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 2) {
				net.mcreator.omnichef.procedures.ChefsDiaryFoodProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 3) {
				net.mcreator.omnichef.procedures.ChefsDiaryClientsProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 4) {
				net.mcreator.omnichef.procedures.ChefsDiaryApplienceProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 5) {
				net.mcreator.omnichef.procedures.ChefsDiaryStatsProcedure.execute(world, x, y, z, entity);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(BlocksGuideP6GUIButtonMessage.TYPE, BlocksGuideP6GUIButtonMessage.STREAM_CODEC, BlocksGuideP6GUIButtonMessage::handleData);
	}
}