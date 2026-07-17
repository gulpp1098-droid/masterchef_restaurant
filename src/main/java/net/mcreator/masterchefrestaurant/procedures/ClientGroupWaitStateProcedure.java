package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.entity.ClientEntity;

import java.util.Comparator;

public class ClientGroupWaitStateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		boolean AllReady = false;
		Entity client = null;
		Direction chairDirection = Direction.NORTH;
		client = entity;
		AllReady = true;
		{
			final Vec3 _center = new Vec3(x, y, z);
			for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
				if (entityiterator instanceof ClientEntity) {
					if (client.getPersistentData().getDouble("group") == entityiterator.getPersistentData().getDouble("group")) {
						if (!(entityiterator.getPersistentData().getString("state")).equals("group_wait")) {
							AllReady = false;
						}
					}
				}
			}
		}
		if (AllReady) {
			{
				final Vec3 _center = new Vec3(x, y, z);
				for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(4 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
					if (entityiterator instanceof ClientEntity) {
						if (client.getPersistentData().getDouble("group") == entityiterator.getPersistentData().getDouble("group")) {
							chairDirection = getDirectionFromBlockState(
									(world.getBlockState(BlockPos.containing(client.getPersistentData().getDouble("DestX"), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ")))));
							SetLogicNBTProcedure.execute(world, client.getPersistentData().getDouble("DestX") + chairDirection.getStepX(), client.getPersistentData().getDouble("DestY"),
									client.getPersistentData().getDouble("DestZ") + chairDirection.getStepZ(), false, "occupied");
							entityiterator.getPersistentData().putString("state", "leave");
							entityiterator.getPersistentData().putDouble("despawn_time", (world.dayTime() + 250));
							entityiterator.stopRiding();
						}
					}
				}
			}
			client.getPersistentData().putString("state", "leave");
			client.getPersistentData().putDouble("despawn_time", (world.dayTime() + 250));
			client.stopRiding();
		}
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