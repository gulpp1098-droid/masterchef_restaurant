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

import net.mcreator.omnichef.procedures.CheckboxPacketToServerProcedureProcedure;
import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public record CheckboxPacketToServerMessage(String extradata) implements CustomPacketPayload {
	public static final Type<CheckboxPacketToServerMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(OmnichefMod.MODID, "checkbox_packet_to_server"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CheckboxPacketToServerMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, CheckboxPacketToServerMessage message) -> {
		buffer.writeUtf(message.extradata);
	}, (RegistryFriendlyByteBuf buffer) -> new CheckboxPacketToServerMessage(buffer.readUtf()));

	@Override
	public Type<CheckboxPacketToServerMessage> type() {
		return TYPE;
	}

	public static void handleData(final CheckboxPacketToServerMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				Player entity = context.player();
				Level world = entity.level();
				double x = entity.getX();
				double y = entity.getY();
				double z = entity.getZ();
				String inboundString = message.extradata;
				if (!world.hasChunkAt(entity.blockPosition()))
					return;

				CheckboxPacketToServerProcedureProcedure.execute(world, entity, inboundString);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		OmnichefMod.addNetworkMessage(CheckboxPacketToServerMessage.TYPE, CheckboxPacketToServerMessage.STREAM_CODEC, CheckboxPacketToServerMessage::handleData);
	}
}