package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class CheckRestaurantBoundaryDistanceProcedure {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonArray locations = new com.google.gson.JsonArray();
		double tileX = 0;
		double tileZ = 0;
		if (!world.isClientSide()) {
			if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).ShowRestaurantBoundaries) {
				if (world.dayTime() % 20 == 0) {
					locations = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants", "locations",
							OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
					if (locations.size() > 0) {
						tileX = new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(GetPartFromStringProcedure.execute(0, locations.get((int) 0).getAsString()));
						tileZ = new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(GetPartFromStringProcedure.execute(1, locations.get((int) 0).getAsString()));
						if (new Vec3((tileX * 5 + 2.5), 0, (tileZ * 5 + 2.5)).distanceTo(new Vec3((entity.getX()), 0, (entity.getZ()))) >= 50) {
							{
								OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
								_vars.ShowRestaurantBoundaries = false;
								_vars.markSyncDirty();
							}
						}
					}
				}
			}
		}
	}
}