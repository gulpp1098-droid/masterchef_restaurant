package net.mcreator.masterchefrestaurant.procedures;

import org.checkerframework.checker.units.qual.s;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

import com.google.common.collect.Table;

public class ScanningRestaurantAreasProcedure {
	public static double execute(LevelAccessor world, Entity entity, String valueDependency) {
		if (entity == null || valueDependency == null)
			return 0;
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray locations = new com.google.gson.JsonArray();
		com.google.gson.JsonArray tables = new com.google.gson.JsonArray();
		BlockState Table = Blocks.AIR.defaultBlockState();
		boolean Remove = false;
		double indexLocations = 0;
		double indexTables = 0;
		double SectorX = 0;
		double SectorZ = 0;
		double PosX = 0;
		double PosY = 0;
		double PosZ = 0;
		double TableFound = 0;
		double ChairFound = 0;
		double ChairsMax = 0;
		double level = 0;
		double TablesActive = 0;
		double RestaurantIndex = 0;
		String tablesString = "";
		String locationsString = "";
		restaurantObject = FindRestaurantInfoByIndexViaIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
		locations = restaurantObject.get("locations").getAsJsonArray();
		tables = restaurantObject.get("tables").getAsJsonArray();
		RestaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
		level = GetRestaurantNumberParameterProcedure.execute(RestaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
		ChairsMax = 0;
		indexTables = 0;
		TableFound = 0;
		ChairFound = 0;
		TablesActive = 0;
		for (int index0 = 0; index0 < (int) tables.size(); index0++) {
			Remove = false;
			tablesString = tables.get((int) indexTables).getAsString();
			PosX = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(0, tablesString));
			PosY = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(1, tablesString));
			PosZ = new Object() {
				double convert(String s) {
					try {
						return Double.parseDouble(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert(GetPartFromStringProcedure.execute(2, tablesString));
			Table = (world.getBlockState(BlockPos.containing(PosX, PosY, PosZ)));
			if (Table.getBlock() == MasterchefRestaurantModBlocks.SERVICE_TABLE.get()) {
				if (!(getBlockNBTNumber(world, BlockPos.containing(PosX, PosY, PosZ), "RestaurantID") == entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
					Remove = true;
				} else {
					setBlockNBTLogic(world, PosX, PosY, PosZ, "active", false);
					indexLocations = 0;
					if (TablesActive < Math.floor(level / 10) + 1 && TablesActive < 10) {
						for (int index1 = 0; index1 < (int) locations.size(); index1++) {
							locationsString = locations.get((int) indexLocations).getAsString();
							SectorX = new Object() {
								double convert(String s) {
									try {
										return Double.parseDouble(s.trim());
									} catch (Exception e) {
									}
									return 0;
								}
							}.convert(GetPartFromStringProcedure.execute(0, locationsString));
							SectorZ = new Object() {
								double convert(String s) {
									try {
										return Double.parseDouble(s.trim());
									} catch (Exception e) {
									}
									return 0;
								}
							}.convert(GetPartFromStringProcedure.execute(1, locationsString));
							if (SectorX == Math.floor(PosX / 5) && SectorZ == Math.floor(PosZ / 5)) {
								TableFound = TableFound + 1;
								ChairFound = ChairFound + getBlockNBTNumber(world, BlockPos.containing(PosX, PosY, PosZ), "ChairAmount");
								TablesActive = TablesActive + 1;
								setBlockNBTLogic(world, PosX, PosY, PosZ, "active", true);
								if (getBlockNBTNumber(world, BlockPos.containing(PosX, PosY, PosZ), "ChairAmount") > ChairsMax) {
									ChairsMax = getBlockNBTNumber(world, BlockPos.containing(PosX, PosY, PosZ), "ChairAmount");
								}
								break;
							}
							indexLocations = indexLocations + 1;
						}
					}
				}
			} else {
				Remove = true;
			}
			if (Remove) {
				RemoveRestaurantArrayParameterIndexProcedure.execute(RestaurantIndex, "restaurants", "tables", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, PosX + ":" + PosY + ":" + PosZ);
			}
			indexTables = indexTables + 1;
		}
		if ((valueDependency).equals("Tables")) {
			return TableFound;
		} else if ((valueDependency).equals("ChairsMax")) {
			return ChairsMax;
		}
		return ChairFound;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}

	private static void setBlockNBTLogic(LevelAccessor world, double x, double y, double z, String tag, boolean value) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			if (blockEntity != null) {
				blockEntity.getPersistentData().putBoolean(tag, value);
			}
			if (world instanceof Level level) {
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	}
}