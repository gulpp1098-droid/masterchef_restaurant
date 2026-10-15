package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ClientFinishActiveGroupProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double restaurantID = 0;
		double restaurantIndex = 0;
		if (!world.isClientSide() && entity.getPersistentData().getBoolean("leader") && !entity.getPersistentData().getBoolean("active_group_finished")) {
			restaurantID = entity.getPersistentData().getDouble("RestaurantID");
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
			if (restaurantIndex >= 0) {
				ModifyRestaurantNumberParameterProcedure
						.execute(
								Math.max(0,
										GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
												OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "active_groups") - 1),
								restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "active_groups");
				entity.getPersistentData().putBoolean("active_group_finished", true);
			}
		}
	}
}