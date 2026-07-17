package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class CheckboxPacketToClientProcedureProcedure {
	public static void execute(Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		{
			MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
			_vars.CurrentClientFoodDelivered = inboundString;
			_vars.markSyncDirty();
		}
	}
}