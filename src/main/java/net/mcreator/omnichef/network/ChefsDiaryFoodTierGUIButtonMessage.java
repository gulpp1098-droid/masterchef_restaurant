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

import net.mcreator.omnichef.procedures.ChefsDiaryFoodPreviousPageProcedure;
import net.mcreator.omnichef.procedures.ChefsDiaryFoodNextPageProcedure;
import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public record ChefsDiaryFoodTierGUIButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<ChefsDiaryFoodTierGUIButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "chefs_diary_food_tier_gui_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChefsDiaryFoodTierGUIButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ChefsDiaryFoodTierGUIButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new ChefsDiaryFoodTierGUIButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<ChefsDiaryFoodTierGUIButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final ChefsDiaryFoodTierGUIButtonMessage message, final IPayloadContext context) {
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

			ChefsDiaryFoodPreviousPageProcedure.execute(world, x, y, z, entity);
		}
		if (buttonID == 1) {

			ChefsDiaryFoodNextPageProcedure.execute(world, x, y, z, entity);
		}

		guiTools$enhancedImageButton : {
			if (buttonID == 2) {
				net.mcreator.omnichef.procedures.ChefsDiaryGUIOpenProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 3) {
				net.mcreator.omnichef.procedures.ChefsDiaryFoodProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 4) {
				net.mcreator.omnichef.procedures.ChefsDiaryClientsProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 5) {
				net.mcreator.omnichef.procedures.ChefsDiaryApplienceProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 6) {
				net.mcreator.omnichef.procedures.ChefsDiaryStatsProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 7) {
				net.mcreator.omnichef.procedures.ChefsDiaryShowIngredientsProcedure.execute(world, x, y, z, entity);
			}
			if (buttonID == 8) {
				net.mcreator.omnichef.procedures.ChefsDiaryShowFoodsProcedure.execute(world, x, y, z, entity);
			}
		}
		guiTools$advancedButtons : {
			if (buttonID == 9) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 0.0);
			}
			if (buttonID == 10) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 1.0);
			}
			if (buttonID == 11) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 2.0);
			}
			if (buttonID == 12) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 3.0);
			}
			if (buttonID == 13) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 4.0);
			}
			if (buttonID == 14) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 5.0);
			}
			if (buttonID == 15) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 6.0);
			}
			if (buttonID == 16) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 7.0);
			}
			if (buttonID == 17) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 8.0);
			}
			if (buttonID == 18) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 9.0);
			}
			if (buttonID == 19) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 10.0);
			}
			if (buttonID == 20) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 11.0);
			}
			if (buttonID == 21) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 12.0);
			}
			if (buttonID == 22) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 13.0);
			}
			if (buttonID == 23) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 14.0);
			}
			if (buttonID == 24) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 15.0);
			}
			if (buttonID == 25) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 16.0);
			}
			if (buttonID == 26) {
				net.mcreator.omnichef.procedures.ChefsDiarySelectItemProcedure.execute(world, x, y, z, entity, 17.0);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(ChefsDiaryFoodTierGUIButtonMessage.TYPE, ChefsDiaryFoodTierGUIButtonMessage.STREAM_CODEC, ChefsDiaryFoodTierGUIButtonMessage::handleData);
	}
}