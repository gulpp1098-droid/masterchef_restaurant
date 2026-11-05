package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModParticleTypes;

public class ClientOnEntityTickUpdateProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return false;
		Entity client = null;
		String satisfaction = "";
		client = entity;
		if (!world.isClientSide()) {
			if (OmnichefModVariables.MapVariables.get(world).ModDataReady) {
				if (Math.floor(client.getPersistentData().getDouble("shift_id") / 24000) < OmnichefModVariables.MapVariables.get(world).LastClientsDatabaseResetDay) {
					if (!client.level().isClientSide())
						client.discard();
					return true;
				}
				if (RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID")) >= 0) {
					if (client.getPersistentData().getBoolean("leader")) {
						if ((client.getPersistentData().getString("state")).equals("queue_move") || (client.getPersistentData().getString("state")).equals("queue_wait")) {
							ClientQueueMoveStateProcedure.execute(entity);
						}
					} else {
						if ((client.getPersistentData().getString("state")).equals("restaurant_go") || (client.getPersistentData().getString("state")).equals("queue_move") || (client.getPersistentData().getString("state")).equals("queue_wait")) {
							ClientFollowStateProcedure.execute(world, entity);
						}
					}
					ClientAnimationSetUpProcedure.execute(world, entity);
					if (world.dayTime() % 10 == 0) {
						if (client.getPersistentData().getBoolean("service_result_counted") && client.getPersistentData().getBoolean("order_served")) {
							satisfaction = GetClientSatisfactionLevelProcedure.execute(client);
							if ((satisfaction).equals("perfect")) {
								if (world instanceof ServerLevel _level)
									_level.sendParticles((SimpleParticleType) (OmnichefModParticleTypes.PERFECT_PARTICLE.get()), x, (y + 1.8), z, 2, 0.5, 0.25, 0.5, 0.015);
							} else if ((satisfaction).equals("satisfied")) {
								if (world instanceof ServerLevel _level)
									_level.sendParticles((SimpleParticleType) (OmnichefModParticleTypes.SATISFIED_PARTICLE.get()), x, (y + 1.8), z, 1, 0.5, 0.25, 0.5, 0.015);
							} else if ((satisfaction).equals("unsatisfied")) {
								if (world instanceof ServerLevel _level)
									_level.sendParticles((SimpleParticleType) (OmnichefModParticleTypes.UNSATISFIED_PARTICLE.get()), x, (y + 1.8), z, 1, 0.5, 0.25, 0.5, 0.015);
							}
						}
						if (!ClientMaximumLifetimeCheckProcedure.execute(world, entity)) {
							ClientPatianceGoingDownProcedure.execute(world, entity);
							if (client.getPersistentData().getBoolean("leader")) {
								if ((client.getPersistentData().getString("state")).equals("restaurant_go")) {
									if (!GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID")), "restaurants",
											OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
										ClientExpPayProcedure.execute(entity);
										ClientBeginLeavingProcedure.execute(world, entity);
									} else {
										ClientRestaurantGoStateProcedure.execute(world, entity);
									}
								} else if ((client.getPersistentData().getString("state")).equals("queue_wait")) {
									if (!GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID")), "restaurants",
											OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
										ClientExpPayProcedure.execute(entity);
										ClientBeginLeavingProcedure.execute(world, entity);
									} else {
										ClientQueueWaitStateProcedure.execute(world, entity);
									}
								} else if ((client.getPersistentData().getString("state")).equals("table_go")) {
									ClientTableGoStateProcedure.execute(world, entity);
								} else if ((client.getPersistentData().getString("state")).equals("group_wait")) {
									ClientGroupWaitStateProcedure.execute(world, x, y, z, entity);
								}
							} else {
								ClientCopyLeaderLeaveStateProcedure.execute(world, entity);
								if ((client.getPersistentData().getString("state")).equals("table_go")) {
									ClientTableGoCopyNBTStateProcedure.execute(world, entity);
								}
							}
							if ((client.getPersistentData().getString("state")).equals("find_chair")) {
								ClientFindChairStateProcedure.execute(entity);
							} else if ((client.getPersistentData().getString("state")).equals("order_pick")) {
								ClientOrderPickStateProcedure.execute(world, entity);
							} else if ((client.getPersistentData().getString("state")).equals("food_wait")) {
								ClientFoodWaitStateProcedure.execute(world, entity);
							} else if ((client.getPersistentData().getString("state")).equals("food_eat")) {
								ClientFoodEatStateProcedure.execute(world, entity);
							} else if ((client.getPersistentData().getString("state")).equals("leave")) {
								ClientLeaveStateProcedure.execute(world, x, y, z, entity);
							} else if ((client.getPersistentData().getString("state")).equals("order_wait")) {
							} else {
								if (!((client.getPersistentData().getString("state")).equals("restaurant_go") || (client.getPersistentData().getString("state")).equals("queue_move")
										|| (client.getPersistentData().getString("state")).equals("queue_wait"))) {
									ClientFollowStateProcedure.execute(world, entity);
								}
							}
							EntityStaysOnTableProcedure.execute(world, entity);
							OrderWaitingProcedure.execute(world, entity);
						}
						if (world.dayTime() % 100 == 0) {
							ClientStuckStateProcedure.execute(world, x, y, z, entity);
						}
					}
				} else {
					if (!client.level().isClientSide())
						client.discard();
				}
			}
		}
		return true;
	}
}