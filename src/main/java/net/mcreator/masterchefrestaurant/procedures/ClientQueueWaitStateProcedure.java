package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class ClientQueueWaitStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		com.google.gson.JsonArray TablesArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray PossibleTablesArray = new com.google.gson.JsonArray();
		boolean Found = false;
		boolean FoundTable = false;
		com.google.gson.JsonObject TablePositionsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject PickedTableObject = new com.google.gson.JsonObject();
		String tableString = "";
		String receptionString = "";
		double RugNumber = 0;
		double X = 0;
		double Y = 0;
		double Z = 0;
		double DestX = 0;
		double DestY = 0;
		double DestZ = 0;
		double stringIndex = 0;
		double TabZ = 0;
		double TabY = 0;
		double TabX = 0;
		double groupSize = 0;
		double restaurantID = 0;
		double RandomTable = 0;
		double RecX = 0;
		double RecY = 0;
		double RecZ = 0;
		client = entity;
		X = client.getPersistentData().getDouble("DestX");
		Y = client.getPersistentData().getDouble("DestY");
		Z = client.getPersistentData().getDouble("DestZ");
		RugNumber = client.getPersistentData().getDouble("current_position") - 1;
		if (client.getPersistentData().getDouble("current_position") > 1 && getBlockNBTNumber(world, BlockPos.containing(X, Y, Z), "queue") > 0) {
			for (Direction directioniterator : Direction.Plane.HORIZONTAL) {
				if ((world.getBlockState(BlockPos.containing(X + directioniterator.getStepX(), Y, Z + directioniterator.getStepZ()))).getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get()) {
					if (getBlockNBTNumber(world, BlockPos.containing(X + directioniterator.getStepX(), Y, Z + directioniterator.getStepZ()), "queue") == RugNumber - 1) {
						if (directioniterator == Direction.SOUTH) {
							{
								Entity _ent = client;
								_ent.setYRot(0);
								_ent.setXRot(0);
								_ent.setYBodyRot(_ent.getYRot());
								_ent.setYHeadRot(_ent.getYRot());
								_ent.yRotO = _ent.getYRot();
								_ent.xRotO = _ent.getXRot();
								if (_ent instanceof LivingEntity _entity) {
									_entity.yBodyRotO = _entity.getYRot();
									_entity.yHeadRotO = _entity.getYRot();
								}
							}
						} else if (directioniterator == Direction.WEST) {
							{
								Entity _ent = client;
								_ent.setYRot(90);
								_ent.setXRot(0);
								_ent.setYBodyRot(_ent.getYRot());
								_ent.setYHeadRot(_ent.getYRot());
								_ent.yRotO = _ent.getYRot();
								_ent.xRotO = _ent.getXRot();
								if (_ent instanceof LivingEntity _entity) {
									_entity.yBodyRotO = _entity.getYRot();
									_entity.yHeadRotO = _entity.getYRot();
								}
							}
						} else if (directioniterator == Direction.NORTH) {
							{
								Entity _ent = client;
								_ent.setYRot(180);
								_ent.setXRot(0);
								_ent.setYBodyRot(_ent.getYRot());
								_ent.setYHeadRot(_ent.getYRot());
								_ent.yRotO = _ent.getYRot();
								_ent.xRotO = _ent.getXRot();
								if (_ent instanceof LivingEntity _entity) {
									_entity.yBodyRotO = _entity.getYRot();
									_entity.yHeadRotO = _entity.getYRot();
								}
							}
						} else {
							{
								Entity _ent = client;
								_ent.setYRot(-90);
								_ent.setXRot(0);
								_ent.setYBodyRot(_ent.getYRot());
								_ent.setYHeadRot(_ent.getYRot());
								_ent.yRotO = _ent.getYRot();
								_ent.xRotO = _ent.getXRot();
								if (_ent instanceof LivingEntity _entity) {
									_entity.yBodyRotO = _entity.getYRot();
									_entity.yHeadRotO = _entity.getYRot();
								}
							}
						}
						if (!getBlockNBTLogic(world, BlockPos.containing(X + directioniterator.getStepX(), Y, Z + directioniterator.getStepZ()), "occupied")) {
							DestX = X + directioniterator.getStepX();
							DestY = Y;
							DestZ = Z + directioniterator.getStepZ();
							Found = true;
							break;
						}
					}
				}
			}
			if (Found) {
				SetLogicNBTProcedure.execute(world, DestX, DestY, DestZ, true, "occupied");
				SetLogicNBTProcedure.execute(world, client.getPersistentData().getDouble("DestX"), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ"), false, "occupied");
				client.getPersistentData().putDouble("DestX", DestX);
				client.getPersistentData().putDouble("DestY", DestY);
				client.getPersistentData().putDouble("DestZ", DestZ);
				client.getPersistentData().putString("state", "queue_move");
				if (client instanceof Mob _mob44)
					_mob44.setNoAi(false);
			}
		} else {
			FoundTable = false;
			restaurantID = client.getPersistentData().getDouble("RestaurantID");
			groupSize = client.getPersistentData().getDouble("group_size");
			TablesArray = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "tables", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
			stringIndex = 0;
			for (int _i1 = 0; _i1 < (int) TablesArray.size(); _i1++) {
				tableString = TablesArray.get((int) stringIndex).getAsString();
				TablePositionsObject = new Object() {
					public com.google.gson.JsonObject parse(String rawJson) {
						try {
							return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
						} catch (Exception e) {
							MasterchefRestaurantMod.LOGGER.error(e);
							return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
						}
					}
				}.parse("{}");
				TabX = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(0, tableString));
				TabY = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(1, tableString));
				TabZ = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(2, tableString));
				stringIndex = stringIndex + 1;
				if (!getBlockNBTLogic(world, BlockPos.containing(TabX, TabY, TabZ), "occupied") && getBlockNBTNumber(world, BlockPos.containing(TabX, TabY, TabZ), "ChairAmount") >= groupSize
						&& IsInsideRestaurantProcedure.execute(world, TabX, TabZ, restaurantID) && getBlockNBTLogic(world, BlockPos.containing(TabX, TabY, TabZ), "active")
						&& getBlockNBTNumber(world, BlockPos.containing(TabX, TabY, TabZ), "coins") == 0) {
					TablePositionsObject.addProperty("TabX", TabX);
					TablePositionsObject.addProperty("TabY", TabY);
					TablePositionsObject.addProperty("TabZ", TabZ);
					PossibleTablesArray.add(TablePositionsObject);
					FoundTable = true;
				}
			}
			if (FoundTable) {
				RandomTable = Mth.nextInt(RandomSource.create(), 0, (int) (PossibleTablesArray.size() - 1));
				client.getPersistentData().putDouble("stuckCounter", 10);
				client.getPersistentData().putBoolean("alertSent", false);
				if (client instanceof LivingEntity _entity)
					_entity.removeEffect(MobEffects.GLOWING);
				receptionString = GetRestaurantStringParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
				RecX = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(0, receptionString));
				RecY = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(1, receptionString));
				RecZ = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(2, receptionString));
				SetNumberNBTProcedure.execute(world, RecX, RecY, RecZ, getBlockNBTNumber(world, BlockPos.containing(RecX, RecY, RecZ), "queue_length") - 1, "queue_length");
				client.getPersistentData().putString("state", "table_go");
				if (client instanceof Mob _mob65)
					_mob65.setNoAi(false);
				SetLogicNBTProcedure.execute(world, client.getPersistentData().getDouble("DestX"), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ"), false, "occupied");
				PickedTableObject = PossibleTablesArray.get((int) RandomTable).getAsJsonObject();
				client.getPersistentData().putDouble("DestX", PickedTableObject.get("TabX").getAsDouble());
				client.getPersistentData().putDouble("DestY", PickedTableObject.get("TabY").getAsDouble());
				client.getPersistentData().putDouble("DestZ", PickedTableObject.get("TabZ").getAsDouble());
				client.getPersistentData().putDouble("TableNumber",
						(getBlockNBTNumber(world, BlockPos.containing(PickedTableObject.get("TabX").getAsDouble(), PickedTableObject.get("TabY").getAsDouble(), PickedTableObject.get("TabZ").getAsDouble()), "TableNumber")));
				SetLogicNBTProcedure.execute(world, PickedTableObject.get("TabX").getAsDouble(), PickedTableObject.get("TabY").getAsDouble(), PickedTableObject.get("TabZ").getAsDouble(), true, "occupied");
			}
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}

	private static boolean getBlockNBTLogic(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getBoolean(tag);
		return false;
	}
}