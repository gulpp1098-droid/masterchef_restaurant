package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModBlocks;

public class IsRestaurantReceptionValidProcedure {
	public static boolean execute(LevelAccessor world, double RestaurantID) {
		double restaurantIndex = 0;
		double receptionX = 0;
		double receptionY = 0;
		double receptionZ = 0;
		String receptionPosition = "";
		boolean ValidReception = false;
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, RestaurantID);
		if (restaurantIndex >= 0) {
			receptionPosition = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
					"reception");
			receptionX = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(0, receptionPosition));
			receptionY = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(1, receptionPosition));
			receptionZ = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(2, receptionPosition));
			if ((world.getBlockState(BlockPos.containing(receptionX, receptionY, receptionZ))).getBlock() == OmnichefModBlocks.RECEPTION.get()
					&& getBlockNBTNumber(world, BlockPos.containing(receptionX, receptionY, receptionZ), "RestaurantID") == RestaurantID && IsInsideRestaurantProcedure.execute(world, receptionX, receptionZ, RestaurantID)) {
				ValidReception = true;
			}
		}
		return ValidReception;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}