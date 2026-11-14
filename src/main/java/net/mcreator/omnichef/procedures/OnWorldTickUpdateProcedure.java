package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import net.mcreator.omnichef.network.OmnichefModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class OnWorldTickUpdateProcedure {
	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent.Post event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		boolean shiftsSettled = false;
		boolean groupsUpdated = false;
		double ingredientIndex = 0;
		double ingredientRestaurantID = 0;
		if ((((Level) world).dimension().location().toString()).equals("minecraft:overworld")) {
			if (!world.isClientSide()) {
				if (OmnichefModVariables.MapVariables.get(world).ModDataReady) {
					ClosingRestaurantsProcedure.execute(world);
					OmnichefModVariables.IngredientExpTickCounter = OmnichefModVariables.IngredientExpTickCounter + 1;
					if (OmnichefModVariables.IngredientExpTickCounter >= 100) {
						OmnichefModVariables.IngredientExpTickCounter = 0;
						for (int _i1 = 0; _i1 < (int) OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.size(); _i1++) {
							ingredientRestaurantID = OmnichefModVariables.MapVariables.get(world).RestaurantsOpen.get((int) ingredientIndex) instanceof Double _doub3 ? _doub3 : 0.0D;
							AddIngredientExpProcedure.execute(world, 1, ingredientRestaurantID);
							ingredientIndex = ingredientIndex + 1;
						}
					}
					groupsUpdated = true;
					if (world.dayTime() % 100 == 0) {
						groupsUpdated = FinishExpiredUnspawnedGroupsProcedure.execute(world);
					}
					if (groupsUpdated) {
						if (world.dayTime() % 100 == 0) {
							shiftsSettled = SettleAllRestaurantShiftsProcedure.execute(world);
						}
						DeleteClientsDatabaseFileProcedure.execute(world);
						SpawnClientsProcedure.execute(world);
					}
				}
			}
		}
	}
}