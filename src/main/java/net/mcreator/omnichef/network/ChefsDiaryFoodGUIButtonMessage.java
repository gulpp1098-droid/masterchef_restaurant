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

import net.mcreator.omnichef.procedures.ChefsDiaryStatsProcedure;
import net.mcreator.omnichef.procedures.ChefsDiaryMenuProcedure;
import net.mcreator.omnichef.procedures.ChefsDiaryFoodProcedure;
import net.mcreator.omnichef.procedures.ChefsDiaryClientsProcedure;
import net.mcreator.omnichef.procedures.ChefsDiaryApplienceProcedure;
import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public record ChefsDiaryFoodGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<ChefsDiaryFoodGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "chefs_diary_food_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChefsDiaryFoodGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ChefsDiaryFoodGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new ChefsDiaryFoodGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<ChefsDiaryFoodGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final ChefsDiaryFoodGUIButtonMessage message, final IPayloadContext context) {
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

		guiTools$enhancedImageButton : {
			if (buttonID == 5) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier0Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 6) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier1Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 7) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier2Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 8) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier3Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 9) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier4Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 10) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier5Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 11) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier6Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 12) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier7Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 13) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier8Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 14) {
				net.mcreator.omnichef.procedures.SelectChefsDiaryFoodTier9Procedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 16) {
				net.mcreator.omnichef.procedures.ChefsDiaryFoodTiersProcedure.execute(world, x, y, z, entity);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(ChefsDiaryFoodGUIButtonMessage.TYPE, ChefsDiaryFoodGUIButtonMessage.STREAM_CODEC, ChefsDiaryFoodGUIButtonMessage::handleData);
	}
}