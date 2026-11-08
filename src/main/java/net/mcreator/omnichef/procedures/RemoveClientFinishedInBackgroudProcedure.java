package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.entity.CriticEntity;

public class RemoveClientFinishedInBackgroudProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		com.google.gson.JsonObject clientData = new com.google.gson.JsonObject();
		com.google.gson.JsonObject group = new com.google.gson.JsonObject();
		com.google.gson.JsonArray groups = new com.google.gson.JsonArray();
		double groupIndex = 0;
		double satisfaction = 0;
		double tableX = 0;
		double tableY = 0;
		double tableZ = 0;
		boolean oldShift = false;
		Direction chairDirection = Direction.NORTH;
		if (world.isClientSide()) {
			return false;
		}
		if (RestaurantIndexSearchByIDProcedure.execute(world, entity.getPersistentData().getDouble("RestaurantID")) < 0) {
			return false;
		}
		oldShift = Math.floor(entity.getPersistentData().getDouble("shift_id") / 24000) < OmnichefModVariables.MapVariables.get(world).LastClientsDatabaseResetDay;
		if (!oldShift) {
			if (entity.getPersistentData().getDouble("client_expire_time") <= 0) {
				return false;
			}
			if (world.dayTime() < entity.getPersistentData().getDouble("client_expire_time")) {
				return false;
			}
			clientData = FindClientsInfoByIndexViaIDProcedure.execute(world, entity.getPersistentData().getDouble("RestaurantID"));
			if (!clientData.has("shift_id")) {
				return false;
			}
			if (!(clientData.get("shift_id").getAsDouble() == entity.getPersistentData().getDouble("shift_id"))) {
				return false;
			}
			groups = clientData.get("groups").getAsJsonArray();
			groupIndex = entity.getPersistentData().getDouble("group");
			if (groupIndex < 0 || groupIndex >= groups.size()) {
				return false;
			}
			group = groups.get((int) groupIndex).getAsJsonObject();
			if (!group.has("finished")) {
				return false;
			}
			if (!group.get("finished").getAsBoolean()) {
				return false;
			}
		}
		entity.getPersistentData().putBoolean("active_group_finished", true);
		if (entity instanceof Mob _entity)
			_entity.getNavigation().stop();
		if ((entity.getPersistentData().getString("state")).equals("leave")) {
			entity.getPersistentData().putBoolean("leaving_started", true);
		}
		if (entity instanceof CriticEntity) {
			CriticGroupWaitStateProcedure.execute(world, entity);
		} else {
			if (entity.getPersistentData().getBoolean("order_served") && !entity.getPersistentData().getBoolean("coins_paid")) {
				if (!world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(entity.getPersistentData().getDouble("DestX")), SectionPos.blockToSectionCoord(entity.getPersistentData().getDouble("DestZ")))) {
					return true;
				}
				chairDirection = getDirectionFromBlockState((world.getBlockState(BlockPos.containing(entity.getPersistentData().getDouble("DestX"), entity.getPersistentData().getDouble("DestY"), entity.getPersistentData().getDouble("DestZ")))));
				tableX = entity.getPersistentData().getDouble("DestX") + chairDirection.getStepX();
				tableY = entity.getPersistentData().getDouble("DestY");
				tableZ = entity.getPersistentData().getDouble("DestZ") + chairDirection.getStepZ();
				if (!world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(tableX), SectionPos.blockToSectionCoord(tableZ))) {
					return true;
				}
			}
			if (!entity.getPersistentData().getBoolean("service_result_counted")) {
				if (entity.getPersistentData().getBoolean("order_served")) {
					satisfaction = GetClientOrderSatisfactionProcedure.execute(entity);
					if (satisfaction >= 100) {
						AddRecipeDiscoveryExpProcedure.execute(world, entity.getPersistentData().getDouble("RestaurantID"));
						AddRecipeDiscoveryExpProcedure.execute(world, entity.getPersistentData().getDouble("RestaurantID"));
					} else if (satisfaction >= 75) {
						AddRecipeDiscoveryExpProcedure.execute(world, entity.getPersistentData().getDouble("RestaurantID"));
					}
				}
				entity.getPersistentData().putBoolean("service_result_counted", true);
			}
			ClientCoinPayProcedure.execute(world, entity);
			ClientExpPayProcedure.execute(entity);
			if (entity.getPersistentData().getBoolean("order_served") && !entity.getPersistentData().getBoolean("coins_paid")) {
				return true;
			}
		}
		ClientBeginLeavingProcedure.execute(world, entity);
		if (!entity.getPersistentData().getBoolean("leaving_started")) {
			return true;
		}
		entity.stopRiding();
		if (!entity.level().isClientSide())
			entity.discard();
		return true;
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}

	private static Property<?> getPropertyByName(BlockState state, String name) {
		for (Property<?> property : state.getProperties()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}
		return null;
	}
}