package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.SectionPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModItems;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

import javax.annotation.Nullable;

@EventBusSubscriber(Dist.CLIENT)
public class SetSpatulaStateToNormalProcedure {
	@SubscribeEvent
	public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
		PacketDistributor.sendToServer(new SetSpatulaStateToNormalMessage());
		execute(event.getEntity());
	}

	@EventBusSubscriber
	public record SetSpatulaStateToNormalMessage() implements CustomPacketPayload {
		public static final Type<SetSpatulaStateToNormalMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MasterchefRestaurantMod.MODID, "procedure_set_spatula_state_to_normal"));
		public static final StreamCodec<RegistryFriendlyByteBuf, SetSpatulaStateToNormalMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, SetSpatulaStateToNormalMessage message) -> {
		}, (RegistryFriendlyByteBuf buffer) -> new SetSpatulaStateToNormalMessage());

		@Override
		public Type<SetSpatulaStateToNormalMessage> type() {
			return TYPE;
		}

		public static void handleData(final SetSpatulaStateToNormalMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.SERVERBOUND) {
				context.enqueueWork(() -> {
					if (!context.player().level().getChunkSource().hasChunk(SectionPos.blockToSectionCoord(context.player().getX()), SectionPos.blockToSectionCoord(context.player().getZ())))
						return;
					execute(context.player());
				}).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}

		@SubscribeEvent
		public static void registerMessage(FMLCommonSetupEvent event) {
			MasterchefRestaurantMod.addNetworkMessage(SetSpatulaStateToNormalMessage.TYPE, SetSpatulaStateToNormalMessage.STREAM_CODEC, SetSpatulaStateToNormalMessage::handleData);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == MasterchefRestaurantModItems.SPATULA_GOLDEN.get()
				&& (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("SelectingRestaurantLocation")) {
			{
				final String _tagName = "SelectingRestaurantLocation";
				final boolean _tagValue = false;
				CustomData.update(DataComponents.CUSTOM_DATA, (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
			}
			{
				final String _tagName = "ShowRestaurantArea";
				final boolean _tagValue = false;
				CustomData.update(DataComponents.CUSTOM_DATA, (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
			}
		}
	}
}