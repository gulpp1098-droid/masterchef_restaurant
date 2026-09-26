package net.mcreator.omnichef.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class OrdersCheckboxesCheckSlot1Procedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(0, 1)).equals("0") || !(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).CurrentClientFoodDelivered.substring(0, 1)).equals("1")) {
			return false;
		}
		return true;
	}
}