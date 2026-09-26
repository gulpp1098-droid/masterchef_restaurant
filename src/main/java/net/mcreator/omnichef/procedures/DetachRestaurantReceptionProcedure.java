package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModBlocks;

public class DetachRestaurantReceptionProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double indexRestaurant = 0;
		double recX = 0;
		double recY = 0;
		double recZ = 0;
		Entity owner = null;
		String reception = "";
		Direction direction = Direction.NORTH;
		owner = entity;
		indexRestaurant = RestaurantIndexSearchByIDProcedure.execute(world, owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
		reception = GetRestaurantStringParameterProcedure.execute(indexRestaurant, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
		if (!(reception).equals("")) {
			recX = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(0, reception));
			recY = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(1, reception));
			recZ = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(2, reception));
			if ((world.getBlockState(BlockPos.containing(recX, recY, recZ))).getBlock() == OmnichefModBlocks.RECEPTION.get()
					&& getBlockNBTNumber(world, BlockPos.containing(recX, recY, recZ), "RestaurantID") == entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID) {
				direction = getDirectionFromBlockState((world.getBlockState(BlockPos.containing(recX, recY, recZ))));
				DetachQueueRugsProcedure.execute(world, recX + direction.getStepX(), recY, recZ + direction.getStepZ());
				{
					BlockPos _pos = BlockPos.containing(recX, recY, recZ);
					Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(recX, recY, recZ), null);
					world.destroyBlock(_pos, false);
				}
				ModifyRestaurantStringParameterProcedure.execute(indexRestaurant, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception", "");
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