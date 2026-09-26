package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.init.OmnichefModBlocks;

public class ChairBlockIsPlacedByProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		BlockState ServiceTable = Blocks.AIR.defaultBlockState();
		boolean chairFound = false;
		for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
			ServiceTable = (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ())));
			if (ServiceTable.getBlock() == OmnichefModBlocks.SERVICE_TABLE.get()) {
				setDirectionBlockState(world, x, y, z, directioniterator);
				chairFound = true;
				break;
			}
		}
		if (!chairFound) {
			if (entity instanceof Player _player11 && !_player11.level().isClientSide())
				_player11.displayClientMessage(Component.literal("This Chair is not connected to a Service Table.").withStyle(ChatFormatting.YELLOW), true);
		}
	}

	private static void setDirectionBlockState(LevelAccessor world, double x, double y, double z, Direction value) {
		BlockPos pos = BlockPos.containing(x, y, z);
		BlockState state = world.getBlockState(pos);
		Property<?> property = state.getBlock().getStateDefinition().getProperty("facing");
		if (property instanceof DirectionProperty directionProperty && directionProperty.getPossibleValues().contains(value)) {
			world.setBlock(pos, state.setValue(directionProperty, value), 3);
		} else {
			property = state.getBlock().getStateDefinition().getProperty("axis");
			if (property instanceof EnumProperty enumProperty && enumProperty.getPossibleValues().contains(value.getAxis())) {
				world.setBlock(pos, state.setValue(enumProperty, value.getAxis()), 3);
			}
		}
	}
}