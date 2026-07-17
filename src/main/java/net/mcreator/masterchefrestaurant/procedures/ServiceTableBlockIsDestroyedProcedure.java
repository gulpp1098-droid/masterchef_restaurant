package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;
import net.mcreator.masterchefrestaurant.block.ChairBlock;

import javax.annotation.Nullable;

@EventBusSubscriber
public class ServiceTableBlockIsDestroyedProcedure {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
	}

	public static void execute(LevelAccessor world, double x, double y, double z) {
		execute(null, world, x, y, z);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z) {
		BlockState ChairBlock = Blocks.AIR.defaultBlockState();
		double ChairAmount = 0;
		double chairIndex = 0;
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == MasterchefRestaurantModBlocks.SERVICE_TABLE.get()) {
			for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
				ChairBlock = (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())));
				if (ChairBlock.getBlock() == MasterchefRestaurantModBlocks.CHAIR.get() && (getDirectionFromBlockState(ChairBlock)) == (directioniterator.getOpposite())) {
					setBlockNBTNumber(world, (x + directioniterator.getStepX()), y, (z + directioniterator.getStepZ()), "TableNumber", (-1));
					setBlockNBTNumber(world, (x + directioniterator.getStepX()), y, (z + directioniterator.getStepZ()), "ChairNumber", (-1));
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

	private static void setBlockNBTNumber(LevelAccessor world, double x, double y, double z, String tag, double value) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			if (blockEntity != null) {
				blockEntity.getPersistentData().putDouble(tag, value);
			}
			if (world instanceof Level level) {
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
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