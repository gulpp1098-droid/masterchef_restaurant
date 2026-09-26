package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class OrdersCheckboxesCheckSlot3Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered).length() == 5) {
			if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(4)).equals("0")) {
				return false;
			}
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered).length() > 5) {
			if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(4, 5)).equals("0")) {
				return false;
			}
		}
		return true;
	}
}