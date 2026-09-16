package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

public class DeleteRestaurantProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonArray tablesArray = new com.google.gson.JsonArray();
		String receptionString = "";
		String tableString = "";
		double restaurantID = 0;
		double tableIndex = 0;
		double tableX = 0;
		double tableY = 0;
		double tableZ = 0;
		double restaurantIndex = 0;
		double receX = 0;
		double receY = 0;
		double receZ = 0;
		Direction receptionDirection = Direction.NORTH;
		restaurantID = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID;
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (CanDeleteRestaurantProcedure.execute(world, entity, restaurantID)) {
			tablesArray = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "tables", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
			tableIndex = 0;
			for (int _i1 = 0; _i1 < (int) tablesArray.size(); _i1++) {
				tableString = tablesArray.get((int) tableIndex).getAsString();
				tableX = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(0, tableString));
				tableY = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(1, tableString));
				tableZ = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(2, tableString));
				if ((world.getBlockState(BlockPos.containing(tableX, tableY, tableZ))).getBlock() == MasterchefRestaurantModBlocks.SERVICE_TABLE.get()
						&& getBlockNBTNumber(world, BlockPos.containing(tableX, tableY, tableZ), "RestaurantID") == restaurantID) {
					SetNumberNBTProcedure.execute(world, tableX, tableY, tableZ, 0, "RestaurantID");
					for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
						if ((world.getBlockState(BlockPos.containing(tableX + directioniterator.getStepX(), tableY, tableZ + directioniterator.getStepZ()))).getBlock() == MasterchefRestaurantModBlocks.CHAIR.get()
								&& getBlockNBTNumber(world, BlockPos.containing(tableX + directioniterator.getStepX(), tableY, tableZ + directioniterator.getStepZ()), "RestaurantID") == restaurantID) {
							SetNumberNBTProcedure.execute(world, tableX + directioniterator.getStepX(), tableY, tableZ + directioniterator.getStepZ(), 0, "RestaurantID");
						}
					}
				}
				tableIndex = tableIndex + 1;
			}
			receptionString = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
			receX = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(0, receptionString));
			receY = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(1, receptionString));
			receZ = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(2, receptionString));
			if ((world.getBlockState(BlockPos.containing(receX, receY, receZ))).getBlock() == MasterchefRestaurantModBlocks.RECEPTION.get() && getBlockNBTNumber(world, BlockPos.containing(receX, receY, receZ), "RestaurantID") == restaurantID) {
				receptionDirection = getDirectionFromBlockState((world.getBlockState(BlockPos.containing(receX, receY, receZ))));
				if ((world.getBlockState(BlockPos.containing(receX + receptionDirection.getStepX(), receY, receZ + receptionDirection.getStepZ()))).getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get()
						&& getBlockNBTNumber(world, BlockPos.containing(receX + receptionDirection.getStepX(), receY, receZ + receptionDirection.getStepZ()), "RestaurantID") == restaurantID) {
					DetachQueueRugsProcedure.execute(world, receX + receptionDirection.getStepX(), receY, receZ + receptionDirection.getStepZ());
				}
				SetNumberNBTProcedure.execute(world, receX, receY, receZ, 0, "RestaurantID");
			}
			RemoveRestaurantFromFileProcedure.execute(world, restaurantID);
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.Restaurant_ID = -1;
				_vars.markSyncDirty();
			}
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
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