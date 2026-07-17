package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.util.UUID;

public class RestaurantLevelUpCheckProcedure {
	public static double execute(LevelAccessor world, double IDrestaurant) {
		String owner = "";
		double restaurantID = 0;
		double restaurantLevel = 0;
		double reputation = 0;
		double requiredReputation = 0;
		double requiredDown = 0;
		double RestaurantIndex = 0;
		restaurantID = IDrestaurant;
		RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		restaurantLevel = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
		reputation = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
		if (restaurantLevel < 100) {
			requiredReputation = (restaurantLevel + 1) * 40 + Math.pow(restaurantLevel + 1, 2) * 6 + Math.pow(restaurantLevel + 1, 3) * 0.08;
			if (reputation >= requiredReputation) {
				ModifyRestaurantNumberParameterProcedure.execute(restaurantLevel + 1, RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
				owner = GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
				if ((world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, owner) : null) instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal(("Your restaurant has leveled up to: " + (int) (restaurantLevel + 1) + " level!")), false);
				return restaurantLevel + 1;
			}
		}
		if (restaurantLevel > 0 && !(restaurantLevel % 10 == 0)) {
			requiredReputation = restaurantLevel * 40 + Math.pow(restaurantLevel, 2) * 6 + Math.pow(restaurantLevel, 3) * 0.08;
			requiredDown = requiredReputation * 0.8;
			if (reputation < requiredDown) {
				ModifyRestaurantNumberParameterProcedure.execute(restaurantLevel - 1, RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
				owner = GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
				if ((world instanceof ServerLevel _level4 ? getEntityFromUUID(_level4, owner) : null) instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal(("Your restaurant has leveled down to: " + (int) (restaurantLevel - 1) + " level!")), false);
				return restaurantLevel - 1;
			}
		}
		return restaurantLevel;
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}