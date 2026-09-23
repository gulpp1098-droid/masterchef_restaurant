package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class RestaurantLevelUpCheckProcedure {
	public static double execute(LevelAccessor world, Entity entity, double IDrestaurant) {
		if (entity == null)
			return 0;
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
			if (reputation >= requiredReputation && (restaurantLevel + 1) % 10 != 0) {
				ModifyRestaurantNumberParameterProcedure.execute(restaurantLevel + 1, RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
				owner = GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
				if (entity instanceof Player _player3 && !_player3.level().isClientSide())
					_player3.displayClientMessage(Component.literal(("Your restaurant reached Level " + (int) (restaurantLevel + 1) + "!")).withStyle(ChatFormatting.GREEN), false);
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
				if (entity instanceof Player _player7 && !_player7.level().isClientSide())
					_player7.displayClientMessage(Component.literal(("Your restaurant dropped to Level " + (int) (restaurantLevel - 1) + ".")).withStyle(ChatFormatting.RED), false);
				return restaurantLevel - 1;
			}
		}
		return restaurantLevel;
	}
}