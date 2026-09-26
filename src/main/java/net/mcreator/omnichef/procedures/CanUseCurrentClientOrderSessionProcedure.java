package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.omnichef.world.inventory.ClientOrderGUIMenu;
import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.entity.CriticEntity;
import net.mcreator.omnichef.entity.ClientEntity;

import java.util.UUID;

public class CanUseCurrentClientOrderSessionProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		Entity client = null;
		if (!(entity instanceof Player _plr0 && _plr0.containerMenu instanceof ClientOrderGUIMenu)) {
			return false;
		}
		client = world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientUUID) : null;
		if (client == null) {
			return false;
		} else if (!(client instanceof ClientEntity || client instanceof CriticEntity)) {
			return false;
		} else if (!((client.getPersistentData().getString("state")).equals("food_wait") || (client.getPersistentData().getString("state")).equals("order_wait"))) {
			return false;
		} else if ((client != null ? entity.distanceTo(client) : -1) > 6) {
			return false;
		}
		return true;
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}