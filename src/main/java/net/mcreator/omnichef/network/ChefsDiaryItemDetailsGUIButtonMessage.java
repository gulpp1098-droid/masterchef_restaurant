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
public record ChefsDiaryItemDetailsGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<ChefsDiaryItemDetailsGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "chefs_diary_item_details_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChefsDiaryItemDetailsGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ChefsDiaryItemDetailsGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new ChefsDiaryItemDetailsGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<ChefsDiaryItemDetailsGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final ChefsDiaryItemDetailsGUIButtonMessage message, final IPayloadContext context) {
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
			if (buttonID == 14) {
				net.mcreator.omnichef.procedures.ChefsDiaryGUIOpenProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 15) {
				net.mcreator.omnichef.procedures.ChefsDiaryFoodProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 16) {
				net.mcreator.omnichef.procedures.ChefsDiaryClientsProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 17) {
				net.mcreator.omnichef.procedures.ChefsDiaryApplienceProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 18) {
				net.mcreator.omnichef.procedures.ChefsDiaryStatsProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 19) {
				net.mcreator.omnichef.procedures.ChefsDiaryGUIOpenProcedure.execute(world, x, y, z, entity);
			}
		}
		guiTools$advancedButtons : {
			if (buttonID == 20) {
				net.mcreator.omnichef.procedures.ChefsDiaryChangeRecipeProcedure.execute(world, entity, 1.0);
			}
			if (buttonID == 21) {
				net.mcreator.omnichef.procedures.ChefsDiaryChangeRecipeProcedure.execute(world, entity, -1.0);
			}
			if (buttonID == 26) {
				net.mcreator.omnichef.procedures.ChefsDiaryChangeDependentPageProcedure.execute(world, entity, 1.0);
			}
			if (buttonID == 27) {
				net.mcreator.omnichef.procedures.ChefsDiaryChangeDependentPageProcedure.execute(world, entity, -1.0);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(ChefsDiaryItemDetailsGUIButtonMessage.TYPE, ChefsDiaryItemDetailsGUIButtonMessage.STREAM_CODEC, ChefsDiaryItemDetailsGUIButtonMessage::handleData);
	}
}