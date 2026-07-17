package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class ReceptionIsDestroyedWithQueueRugsProcedure {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getPlayer());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (MasterchefRestaurantModBlocks.RECEPTION.get() == (world.getBlockState(BlockPos.containing(x, y, z))).getBlock()) {
			if (MasterchefRestaurantModBlocks.RUG_QUEUE.get() == (world.getBlockState(
					BlockPos.containing((getDirectionFromBlockState((world.getBlockState(BlockPos.containing(x, y, z))))).getStepX() + x, y, (getDirectionFromBlockState((world.getBlockState(BlockPos.containing(x, y, z))))).getStepZ() + z)))
					.getBlock()) {
				RugQueueIsDestroyedWithReceptionProcedure.execute(world, (getDirectionFromBlockState((world.getBlockState(BlockPos.containing(x, y, z))))).getStepX() + x, y,
						(getDirectionFromBlockState((world.getBlockState(BlockPos.containing(x, y, z))))).getStepZ() + z, entity);
				{
					BlockPos _pos = BlockPos.containing((getDirectionFromBlockState((world.getBlockState(BlockPos.containing(x, y, z))))).getStepX() + x, y,
							(getDirectionFromBlockState((world.getBlockState(BlockPos.containing(x, y, z))))).getStepZ() + z);
					Block.dropResources(world.getBlockState(_pos), world,
							BlockPos.containing((getDirectionFromBlockState((world.getBlockState(BlockPos.containing(x, y, z))))).getStepX() + x, y, (getDirectionFromBlockState((world.getBlockState(BlockPos.containing(x, y, z))))).getStepZ() + z),
							null);
					world.destroyBlock(_pos, false);
				}
				ReceptionBlockDestroyedProcedure.execute(world, entity);
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

	private static Property<?> getPropertyByName(BlockState state, String name) {
		for (Property<?> property : state.getProperties()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}
		return null;
	}
}