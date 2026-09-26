package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class OrdersCheckboxesCheckSlot5Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered).length() == 9) {
			if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(8)).equals("0")) {
				return false;
			}
		} else if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered).length() > 9) {
			if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(8, 9)).equals("0")) {
				return false;
			}
		}
		return true;
	}
}