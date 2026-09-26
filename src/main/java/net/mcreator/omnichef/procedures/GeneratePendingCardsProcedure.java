package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class GeneratePendingCardsProcedure {
	public static void execute(LevelAccessor world, double restaurantID) {
		double restaurantIndex = 0;
		double randomnumber1 = 0;
		double randomnumber2 = 0;
		double randomnumber3 = 0;
		com.google.gson.JsonArray options = new com.google.gson.JsonArray();
		com.google.gson.JsonArray pendingCards = new com.google.gson.JsonArray();
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		options = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlock_options", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
				OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
		randomnumber1 = -1;
		randomnumber2 = -1;
		randomnumber3 = -1;
		if (options.size() >= 3) {
			randomnumber1 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
			randomnumber2 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
			randomnumber3 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
			while (randomnumber1 == randomnumber2) {
				randomnumber2 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
			}
			while (randomnumber3 == randomnumber2 || randomnumber3 == randomnumber1) {
				randomnumber3 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
			}
		} else if (options.size() == 2) {
			randomnumber1 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
			randomnumber2 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
			while (randomnumber1 == randomnumber2) {
				randomnumber2 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
			}
		} else if (options.size() == 1) {
			randomnumber1 = Mth.nextInt(RandomSource.create(), 0, (int) (options.size() - 1));
		}
		if (randomnumber1 >= 0) {
			pendingCards.add(options.get((int) randomnumber1).getAsString());
		}
		if (randomnumber2 >= 0) {
			pendingCards.add(options.get((int) randomnumber2).getAsString());
		}
		if (randomnumber3 >= 0) {
			pendingCards.add(options.get((int) randomnumber3).getAsString());
		}
		ModifyRestaurantWholeArrayParameterProcedure.execute(pendingCards, restaurantIndex, "restaurants", "pending_cards", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
				OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
	}
}