package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.omnichef.network.PatiancePacketToClientMessage;
import net.mcreator.omnichef.network.OmnichefModVariables;

import java.util.UUID;

public class PatianceInOrderGUIProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		Entity player = null;
		if (!world.isClientSide()) {
			client = world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientUUID) : null;
			player = entity;
			if (client != null) {
				if (("food_wait").equals(client.getPersistentData().getString("state")) || ("order_wait").equals(client.getPersistentData().getString("state"))) {
					if (player instanceof ServerPlayer player6)
						PacketDistributor.sendToPlayer(player6,
								new PatiancePacketToClientMessage((new java.text.DecimalFormat("##").format(Math.max(Math.min((client.getPersistentData().getDouble("patience_end_time") - world.dayTime()) / 36, 100), 0)))));
				} else {
					if (player instanceof Player _player)
						_player.closeContainer();
				}
			} else {
				if (player instanceof Player _player)
					_player.closeContainer();
			}
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}