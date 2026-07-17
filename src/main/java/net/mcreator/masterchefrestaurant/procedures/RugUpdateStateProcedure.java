package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class RugUpdateStateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		boolean North = false;
		boolean South = false;
		boolean East = false;
		boolean West = false;
		boolean NE = false;
		boolean SE = false;
		boolean NW = false;
		boolean SW = false;
		double amountRugs = 0;
		double amountRugsDiag = 0;
		amountRugs = 0;
		for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
			if (directioniterator == Direction.NORTH && MasterchefRestaurantModBlocks.RUG.get() == (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock()) {
				amountRugs = amountRugs + 1;
				North = true;
			} else if (directioniterator == Direction.SOUTH && MasterchefRestaurantModBlocks.RUG.get() == (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock()) {
				amountRugs = amountRugs + 1;
				South = true;
			} else if (directioniterator == Direction.WEST && MasterchefRestaurantModBlocks.RUG.get() == (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock()) {
				amountRugs = amountRugs + 1;
				West = true;
			} else if (directioniterator == Direction.EAST && MasterchefRestaurantModBlocks.RUG.get() == (world.getBlockState(BlockPos.containing(x + directioniterator.getStepX(), y, z + directioniterator.getStepZ()))).getBlock()) {
				amountRugs = amountRugs + 1;
				East = true;
			}
		}
		amountRugsDiag = 0;
		if (MasterchefRestaurantModBlocks.RUG.get() == (world.getBlockState(BlockPos.containing(x + 1, y, z + 1))).getBlock()) {
			SE = true;
			amountRugsDiag = amountRugsDiag + 1;
		}
		if (MasterchefRestaurantModBlocks.RUG.get() == (world.getBlockState(BlockPos.containing(x + 1, y, z - 1))).getBlock()) {
			NE = true;
			amountRugsDiag = amountRugsDiag + 1;
		}
		if (MasterchefRestaurantModBlocks.RUG.get() == (world.getBlockState(BlockPos.containing(x - 1, y, z + 1))).getBlock()) {
			SW = true;
			amountRugsDiag = amountRugsDiag + 1;
		}
		if (MasterchefRestaurantModBlocks.RUG.get() == (world.getBlockState(BlockPos.containing(x - 1, y, z - 1))).getBlock()) {
			NW = true;
			amountRugsDiag = amountRugsDiag + 1;
		}
		if (amountRugs == 4) {
			if (amountRugsDiag == 4) {
				setIntegerBlockState(world, x, y, z, "state", 11);
			} else if (amountRugsDiag == 3) {
				setIntegerBlockState(world, x, y, z, "state", 10);
				if (!NE) {
					setDirectionBlockState(world, x, y, z, Direction.SOUTH);
				} else if (!SE) {
					setDirectionBlockState(world, x, y, z, Direction.WEST);
				} else if (!NW) {
					setDirectionBlockState(world, x, y, z, Direction.EAST);
				} else {
					setDirectionBlockState(world, x, y, z, Direction.NORTH);
				}
			} else if (amountRugsDiag == 2) {
				if (NE && SW) {
					setIntegerBlockState(world, x, y, z, "state", 9);
					setDirectionBlockState(world, x, y, z, Direction.EAST);
				} else if (SE && NW) {
					setIntegerBlockState(world, x, y, z, "state", 9);
					setDirectionBlockState(world, x, y, z, Direction.NORTH);
				} else {
					if (NE && NW) {
						setIntegerBlockState(world, x, y, z, "state", 8);
						setDirectionBlockState(world, x, y, z, Direction.NORTH);
					} else if (SE && SW) {
						setIntegerBlockState(world, x, y, z, "state", 8);
						setDirectionBlockState(world, x, y, z, Direction.SOUTH);
					} else if (NE && SE) {
						setIntegerBlockState(world, x, y, z, "state", 8);
						setDirectionBlockState(world, x, y, z, Direction.EAST);
					} else {
						setIntegerBlockState(world, x, y, z, "state", 8);
						setDirectionBlockState(world, x, y, z, Direction.WEST);
					}
				}
			} else if (amountRugsDiag == 1) {
				if (NE) {
					setIntegerBlockState(world, x, y, z, "state", 7);
					setDirectionBlockState(world, x, y, z, Direction.EAST);
				} else if (NW) {
					setIntegerBlockState(world, x, y, z, "state", 7);
					setDirectionBlockState(world, x, y, z, Direction.NORTH);
				} else if (SE) {
					setIntegerBlockState(world, x, y, z, "state", 7);
					setDirectionBlockState(world, x, y, z, Direction.SOUTH);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 7);
					setDirectionBlockState(world, x, y, z, Direction.WEST);
				}
			} else {
				setIntegerBlockState(world, x, y, z, "state", 4);
			}
		} else if (amountRugs == 3) {
			if (!East) {
				setDirectionBlockState(world, x, y, z, Direction.NORTH);
				if (NW && SW) {
					setIntegerBlockState(world, x, y, z, "state", 14);
				} else if (SW) {
					setIntegerBlockState(world, x, y, z, "state", 12);
				} else if (NW) {
					setIntegerBlockState(world, x, y, z, "state", 13);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 3);
				}
			} else if (!West) {
				setDirectionBlockState(world, x, y, z, Direction.SOUTH);
				if (NE && SE) {
					setIntegerBlockState(world, x, y, z, "state", 14);
				} else if (SE) {
					setIntegerBlockState(world, x, y, z, "state", 13);
				} else if (NE) {
					setIntegerBlockState(world, x, y, z, "state", 12);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 3);
				}
			} else if (!South) {
				setDirectionBlockState(world, x, y, z, Direction.EAST);
				if (NW && NE) {
					setIntegerBlockState(world, x, y, z, "state", 14);
				} else if (NW) {
					setIntegerBlockState(world, x, y, z, "state", 12);
				} else if (NE) {
					setIntegerBlockState(world, x, y, z, "state", 13);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 3);
				}
			} else {
				setDirectionBlockState(world, x, y, z, Direction.WEST);
				if (SW && SE) {
					setIntegerBlockState(world, x, y, z, "state", 14);
				} else if (SW) {
					setIntegerBlockState(world, x, y, z, "state", 13);
				} else if (SE) {
					setIntegerBlockState(world, x, y, z, "state", 12);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 3);
				}
			}
		} else if (amountRugs == 1) {
			if (East) {
				setDirectionBlockState(world, x, y, z, Direction.NORTH);
			} else if (West) {
				setDirectionBlockState(world, x, y, z, Direction.SOUTH);
			} else if (South) {
				setDirectionBlockState(world, x, y, z, Direction.EAST);
			} else {
				setDirectionBlockState(world, x, y, z, Direction.WEST);
			}
			setIntegerBlockState(world, x, y, z, "state", 1);
		} else if (amountRugs == 2 && (North && South || East && West)) {
			if (North) {
				setDirectionBlockState(world, x, y, z, Direction.WEST);
			} else if (West) {
				setDirectionBlockState(world, x, y, z, Direction.NORTH);
			}
			setIntegerBlockState(world, x, y, z, "state", 0);
		} else if (amountRugs == 2) {
			if (North && East) {
				setDirectionBlockState(world, x, y, z, Direction.SOUTH);
				if (NE) {
					setIntegerBlockState(world, x, y, z, "state", 6);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 2);
				}
			} else if (North && West) {
				setDirectionBlockState(world, x, y, z, Direction.EAST);
				if (NW) {
					setIntegerBlockState(world, x, y, z, "state", 6);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 2);
				}
			} else if (West && South) {
				setDirectionBlockState(world, x, y, z, Direction.NORTH);
				if (SW) {
					setIntegerBlockState(world, x, y, z, "state", 6);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 2);
				}
			} else {
				if (SE) {
					setIntegerBlockState(world, x, y, z, "state", 6);
				} else {
					setIntegerBlockState(world, x, y, z, "state", 2);
				}
				setDirectionBlockState(world, x, y, z, Direction.WEST);
			}
		} else {
			setIntegerBlockState(world, x, y, z, "state", 5);
		}
	}

	private static void setIntegerBlockState(LevelAccessor world, double x, double y, double z, String property, int value) {
		BlockPos pos = BlockPos.containing(x, y, z);
		BlockState state = world.getBlockState(pos);
		if (state.getBlock().getStateDefinition().getProperty(property) instanceof IntegerProperty integerProperty && integerProperty.getPossibleValues().contains(value)) {
			world.setBlock(pos, state.setValue(integerProperty, value), 3);
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