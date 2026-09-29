package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;

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
		Entity ownerentity = null;
		restaurantID = IDrestaurant;
		RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		restaurantLevel = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
		reputation = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reputation");
		if (restaurantLevel < 100) {
			requiredReputation = GetRequiredReputationForLevelProcedure.execute(world, restaurantLevel + 1);
			if (reputation >= requiredReputation && (restaurantLevel + 1) % 10 != 0) {
				ModifyRestaurantNumberParameterProcedure.execute(restaurantLevel + 1, RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
				AdvanceRecipeDiscoveryStageProcedure.execute(world, restaurantID);
				owner = GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
				ownerentity = world instanceof ServerLevel _level0 ? getEntityFromUUID(_level0, owner) : null;
				if (ownerentity != null) {
					if (ownerentity instanceof Player _player4 && !_player4.level().isClientSide())
						_player4.displayClientMessage(Component.literal(("Your restaurant reached Level " + (int) (restaurantLevel + 1) + "!")).withStyle(ChatFormatting.GREEN), false);
				}
				return restaurantLevel + 1;
			}
		}
		if (restaurantLevel > 0 && !(restaurantLevel % 10 == 0)) {
			requiredReputation = GetRequiredReputationForLevelProcedure.execute(world, restaurantLevel);
			requiredDown = requiredReputation * 0.8;
			if (reputation < requiredDown) {
				ModifyRestaurantNumberParameterProcedure.execute(restaurantLevel - 1, RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
				owner = GetRestaurantStringParameterProcedure.execute(RestaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
				ownerentity = world instanceof ServerLevel _level5 ? getEntityFromUUID(_level5, owner) : null;
				if (ownerentity != null) {
					if (ownerentity instanceof Player _player9 && !_player9.level().isClientSide())
						_player9.displayClientMessage(Component.literal(("Your restaurant dropped to Level " + (int) (restaurantLevel - 1) + ".")).withStyle(ChatFormatting.RED), false);
				}
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