package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModEntities;

import java.util.ArrayList;

import ca.weblite.objc.Client;

public class SpawnClientsProcedure {
	public static void execute(LevelAccessor world) {
		ArrayList<Object> OpenRestaurantsArray = new ArrayList<>();
		Entity Client = null;
		com.google.gson.JsonObject RestaurantsClientsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject singleGroup = new com.google.gson.JsonObject();
		com.google.gson.JsonObject clientObject = new com.google.gson.JsonObject();
		String stringReception = "";
		String food = "";
		String LeaderUUID = "";
		String stringDelivered = "";
		com.google.gson.JsonArray ClientsData = new com.google.gson.JsonArray();
		com.google.gson.JsonArray groupArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray membersArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodDeliveredArray = new com.google.gson.JsonArray();
		boolean newSpawnTimeSet = false;
		boolean condition = false;
		double nextSpawnGroupTime = 0;
		double nextSpawnGroupIndex = 0;
		double restaurantID = 0;
		double RecX = 0;
		double RecY = 0;
		double RecZ = 0;
		double indexMembers = 0;
		double patience = 0;
		double newSpawnGroupIndex = 0;
		double SpawnTimeModify = 0;
		double SpawnIndexModify = 0;
		double clientDatabaseIndex = 0;
		double restaurantIndex = 0;
		double SpawnX = 0;
		double SpawnY = 0;
		double SpawnZ = 0;
		double OffsetX = 0;
		double OffsetZ = 0;
		if (!world.isClientSide() && (((Level) world).dimension().location().toString()).equals("minecraft:overworld")) {
			OpenRestaurantsArray = MasterchefRestaurantModVariables.MapVariables.get(world).RestaurantsOpen;
			if (!OpenRestaurantsArray.isEmpty()) {
				if (world.dayTime() % 100 == 0) {
					ClientsData = GetClientDatabaseListArrayProcedure.execute(world);
					for (Object arraylistiterator : OpenRestaurantsArray) {
						restaurantID = arraylistiterator instanceof Double _doub5 ? _doub5 : 0.0D;
						clientDatabaseIndex = ClientDatabaseIndexSearchByIDProcedure.execute(world, restaurantID);
						restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
						RestaurantsClientsObject = ClientsData.get((int) clientDatabaseIndex).getAsJsonObject();
						nextSpawnGroupTime = RestaurantsClientsObject.get("next_spawn_group_time").getAsDouble();
						if (world.dayTime() % 24000 >= nextSpawnGroupTime) {
							nextSpawnGroupIndex = RestaurantsClientsObject.get("next_spawn_group_index").getAsDouble();
							groupArray = RestaurantsClientsObject.get("groups").getAsJsonArray();
							singleGroup = groupArray.get((int) nextSpawnGroupIndex).getAsJsonObject();
							if (!singleGroup.get("spawned").getAsBoolean()) {
								stringReception = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
										MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
								RecX = new Object() {
									double convert(String s) {
										try {
											return Double.parseDouble(s.trim());
										} catch (Exception e) {
										}
										return 0;
									}
								}.convert(GetPartFromStringProcedure.execute(0, stringReception));
								RecY = new Object() {
									double convert(String s) {
										try {
											return Double.parseDouble(s.trim());
										} catch (Exception e) {
										}
										return 0;
									}
								}.convert(GetPartFromStringProcedure.execute(1, stringReception));
								RecZ = new Object() {
									double convert(String s) {
										try {
											return Double.parseDouble(s.trim());
										} catch (Exception e) {
										}
										return 0;
									}
								}.convert(GetPartFromStringProcedure.execute(2, stringReception));
								condition = false;
								for (int _i1 = 0; _i1 < 30; _i1++) {
									OffsetX = 0;
									OffsetZ = 0;
									while (Math.max(Math.abs(OffsetX), Math.abs(OffsetZ)) < 12) {
										OffsetX = Mth.nextDouble(RandomSource.create(), -20, 20);
										OffsetZ = Mth.nextDouble(RandomSource.create(), -20, 20);
									}
									SpawnX = RecX + OffsetX;
									SpawnZ = RecZ + OffsetZ;
									if (!IsInsideRestaurantProcedure.execute(world, SpawnX, SpawnZ, restaurantID)) {
										SpawnY = RecY;
										if (world.isEmptyBlock(BlockPos.containing(SpawnX, SpawnY, SpawnZ)) && world.isEmptyBlock(BlockPos.containing(SpawnX, SpawnY + 1, SpawnZ))) {
											while (world.isEmptyBlock(BlockPos.containing(SpawnX, SpawnY - 1, SpawnZ)) && RecY - 25 <= SpawnY) {
												SpawnY = SpawnY - 1;
											}
											if (!world.isEmptyBlock(BlockPos.containing(SpawnX, SpawnY - 1, SpawnZ))) {
												condition = true;
												break;
											} else {
												continue;
											}
										} else {
											continue;
										}
									} else {
										continue;
									}
								}
								if (condition) {
									membersArray = singleGroup.get("members").getAsJsonArray();
									indexMembers = 0;
									for (int _i1 = 0; _i1 < (int) membersArray.size(); _i1++) {
										clientObject = membersArray.get((int) indexMembers).getAsJsonObject();
										foodDeliveredArray = clientObject.get("food").getAsJsonArray();
										food = "" + foodDeliveredArray;
										Client = world instanceof ServerLevel _level23 ? MasterchefRestaurantModEntities.CLIENT.get().spawn(_level23, BlockPos.containing(SpawnX, SpawnY, SpawnZ), MobSpawnType.MOB_SUMMONED) : null;
										patience = clientObject.get("patience").getAsDouble();
										if (indexMembers == 0) {
											Client.getPersistentData().putBoolean("leader", true);
											Client.getPersistentData().putDouble("group_size", membersArray.size());
											LeaderUUID = Client.getStringUUID();
											ModifyRestaurantNumberParameterProcedure.execute(
													GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
															MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "active_groups") + 1,
													restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path,
													"active_groups");
										} else {
											Client.getPersistentData().putString("leaderUUID", LeaderUUID);
										}
										stringDelivered = "";
										for (int _i2 = 0; _i2 < (int) foodDeliveredArray.size(); _i2++) {
											stringDelivered = stringDelivered + ",0";
										}
										Client.getPersistentData().putString("food", food);
										Client.getPersistentData().putString("food_delivered", (stringDelivered.substring(1)));
										Client.getPersistentData().putString("food_tiers", clientObject.get("foodTiers").getAsString());
										Client.getPersistentData().putString("state", "restaurant_go");
										Client.getPersistentData().putString("last_rug", RestaurantsClientsObject.get("queue_start").getAsString());
										Client.getPersistentData().putDouble("group", singleGroup.get("group_id").getAsDouble());
										Client.getPersistentData().putDouble("patience", patience);
										Client.getPersistentData().putDouble("member", indexMembers);
										Client.getPersistentData().putDouble("RestaurantID", restaurantID);
										Client.getPersistentData().putDouble("stuckCounter", 10);
										indexMembers = indexMembers + 1;
									}
									ModifyClientGroupLogicParameterProcedure.execute(true, nextSpawnGroupIndex, clientDatabaseIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).ClientsDatabase_File_Name,
											MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "groups", "spawned");
									singleGroup.addProperty("spawned", true);
									newSpawnGroupIndex = 0;
									SpawnTimeModify = 0;
									SpawnIndexModify = 0;
									newSpawnTimeSet = false;
									for (int _i1 = 0; _i1 < (int) groupArray.size(); _i1++) {
										singleGroup = groupArray.get((int) newSpawnGroupIndex).getAsJsonObject();
										if (!singleGroup.get("spawned").getAsBoolean() && (singleGroup.get("spawn_time").getAsDouble() < SpawnTimeModify || !newSpawnTimeSet)) {
											newSpawnTimeSet = true;
											SpawnTimeModify = singleGroup.get("spawn_time").getAsDouble();
											SpawnIndexModify = newSpawnGroupIndex;
										}
										newSpawnGroupIndex = newSpawnGroupIndex + 1;
									}
									if (newSpawnTimeSet) {
										ModifyRestaurantNumberParameterProcedure.execute(SpawnTimeModify, clientDatabaseIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).ClientsDatabase_File_Name,
												MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "next_spawn_group_time");
										ModifyRestaurantNumberParameterProcedure.execute(SpawnIndexModify, clientDatabaseIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).ClientsDatabase_File_Name,
												MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "next_spawn_group_index");
									}
								} else {
									if (world instanceof ServerLevel _level) {
										_level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("Failed to spawn client group for restaurant!"), false);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}