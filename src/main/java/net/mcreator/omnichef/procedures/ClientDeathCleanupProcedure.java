package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.entity.CriticEntity;
import net.mcreator.omnichef.entity.ClientEntity;

import javax.annotation.Nullable;

@EventBusSubscriber
public class ClientDeathCleanupProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity());
		}
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double restaurantIndex = 0;
		if (!world.isClientSide()) {
			if (entity instanceof ClientEntity || entity instanceof CriticEntity) {
				restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getPersistentData().getDouble("RestaurantID"));
				if (restaurantIndex >= 0) {
					if (!(entity.getPersistentData().getString("state")).equals("leave")) {
						ClientBeginLeavingProcedure.execute(world, entity);
					}
					if (entity instanceof ClientEntity) {
						ClientSettleLeavingProcedure.execute(world, entity);
					}
					if (entity instanceof CriticEntity && !entity.getPersistentData().getBoolean("service_result_counted")) {
						ModifyRestaurantObjectParameterProcedure.execute(restaurantIndex, 1, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
								"daily_stats", "customers_lost");
						entity.getPersistentData().putBoolean("service_result_counted", true);
					}
					ClientFinishActiveGroupProcedure.execute(world, entity);
				}
			}
		}
	}
}