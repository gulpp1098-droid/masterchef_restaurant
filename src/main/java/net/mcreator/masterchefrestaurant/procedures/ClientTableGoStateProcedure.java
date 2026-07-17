package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class ClientTableGoStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		com.google.gson.JsonArray TablesArray = new com.google.gson.JsonArray();
		String tableString = "";
		double groupSize = 0;
		double stringIndex = 0;
		double positionIndex = 0;
		double TabX = 0;
		double TabY = 0;
		double TabZ = 0;
		client = entity;
		if (!(new Vec3((client.getX()), (client.getY()), (client.getZ()))
				.distanceTo(new Vec3((client.getPersistentData().getDouble("DestX") + 0.5), (client.getPersistentData().getDouble("DestY")), (client.getPersistentData().getDouble("DestZ") + 0.5))) <= 3.5)) {
			if (client instanceof Mob _entity)
				_entity.getNavigation().moveTo((client.getPersistentData().getDouble("DestX") + 0.5), (client.getPersistentData().getDouble("DestY") - 1), (client.getPersistentData().getDouble("DestZ") + 0.5), 1);
		} else {
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
					client.getPersistentData().putString("state", "find_chair");
					if (client instanceof LivingEntity _entity)
						_entity.removeEffect(MobEffects.GLOWING);
					break;
				}
			}
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