package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

import java.util.UUID;

public class ClientTableGoCopyNBTStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		Entity leader = null;
		client = entity;
		if ((world instanceof ServerLevel _level1 ? getEntityFromUUID(_level1, (client.getPersistentData().getString("leaderUUID"))) : null) != null) {
			leader = world instanceof ServerLevel _level3 ? getEntityFromUUID(_level3, (client.getPersistentData().getString("leaderUUID"))) : null;
			client.getPersistentData().putDouble("TableNumber", (leader.getPersistentData().getDouble("TableNumber")));
			for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
				if ((world.getBlockState(
						BlockPos.containing(client.getPersistentData().getDouble("DestX") + directioniterator.getStepX(), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ") + directioniterator.getStepZ())))
						.getBlock() == MasterchefRestaurantModBlocks.CHAIR.get()
						&& (directioniterator.getOpposite()) == (getDirectionFromBlockState((world.getBlockState(BlockPos.containing(client.getPersistentData().getDouble("DestX") + directioniterator.getStepX(),
								client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ") + directioniterator.getStepZ())))))
						&& client.getPersistentData().getDouble("member") == getBlockNBTNumber(world, BlockPos.containing(client.getPersistentData().getDouble("DestX") + directioniterator.getStepX(), client.getPersistentData().getDouble("DestY"),
								client.getPersistentData().getDouble("DestZ") + directioniterator.getStepZ()), "ChairNumber")) {
					client.getPersistentData().putDouble("DestX", (client.getPersistentData().getDouble("DestX") + 0.5 + directioniterator.getStepX()));
					client.getPersistentData().putDouble("DestY", (client.getPersistentData().getDouble("DestY")));
					client.getPersistentData().putDouble("DestZ", (client.getPersistentData().getDouble("DestZ") + 0.5 + directioniterator.getStepZ()));
					client.getPersistentData().putDouble("ChairNumber",
							(getBlockNBTNumber(world, BlockPos.containing(client.getPersistentData().getDouble("DestX"), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ")), "ChairNumber")));
					client.getPersistentData().putDouble("stuckCounter", 10);
					client.getPersistentData().putBoolean("alertSent", false);
					if (client instanceof LivingEntity _entity)
						_entity.removeEffect(MobEffects.GLOWING);
					client.getPersistentData().putString("state", "find_chair");
					break;
				}
			}
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
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