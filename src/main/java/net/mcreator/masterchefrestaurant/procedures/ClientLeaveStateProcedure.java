package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class ClientLeaveStateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double restaurantID = 0;
		if (world.dayTime() % 24000 >= 18000 || world.dayTime() % 24000 >= 0 && world.dayTime() % 24000 <= 30
				|| new Vec3(x, y, z).distanceTo(new Vec3((entity.getPersistentData().getDouble("DestX")), (entity.getPersistentData().getDouble("DestY")), (entity.getPersistentData().getDouble("DestZ")))) >= 60
				|| world.dayTime() >= entity.getPersistentData().getDouble("despawn_time")) {
			if (entity.getPersistentData().getBoolean("leader")) {
				restaurantID = entity.getPersistentData().getDouble("RestaurantID");
				ModifyRestaurantNumberParameterProcedure.execute(
						GetRestaurantNumberParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "active_groups") - 1,
						RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "active_groups");
			}
			if (!entity.level().isClientSide())
				entity.discard();
		} else {
			if (entity instanceof Mob _entity)
				_entity.getNavigation().moveTo((x + 100), y, (z + 100), 1);
		}
	}
}