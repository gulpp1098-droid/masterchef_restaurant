package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.entity.LocationEdgeRightEntity;
import net.mcreator.masterchefrestaurant.entity.LocationEdgeLeftEntity;
import net.mcreator.masterchefrestaurant.entity.LocationEdgeEntity;
import net.mcreator.masterchefrestaurant.entity.LocationEdgeDownEntity;

import java.util.Comparator;

import java.io.File;

public class SetRestaurantLocalizationWithSpatulaProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity Owner = null;
		File ListOfRestaurants = new File("");
		com.google.gson.JsonArray RestaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject Restaurants = new com.google.gson.JsonObject();
		com.google.gson.JsonObject RestaurantObject = new com.google.gson.JsonObject();
		boolean AddingArray = false;
		double IndexNumber = 0;
		double arrayIndex = 0;
		double PosX = 0;
		double PosZ = 0;
		double X = 0;
		double Z = 0;
		double restaurantLevel = 0;
		String restaurantsString = "";
		if ((entity.level().dimension().location().toString()).equals("minecraft:overworld")) {
			if (!world.isClientSide()) {
				X = (int) Math.floor(x / 5);
				Z = (int) Math.floor(z / 5);
				Owner = entity;
				AddingArray = false;
				if ((Owner instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("SelectingRestaurantLocation")) {
					if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
						IndexNumber = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
						restaurantLevel = GetRestaurantNumberParameterProcedure.execute(IndexNumber, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
						RestaurantsArray = GetRestaurantArrayParameterProcedure.execute(IndexNumber, "restaurants", "locations", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
						if (RestaurantsArray.size() < 1) {
							AddingArray = true;
							if (Owner instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal(("Base location was set: [" + (X + ":" + Z + "]"))), false);
						} else if (Math.min(30, 4 + Math.floor(restaurantLevel * (26d / 95))) > RestaurantsArray.size()) {
							arrayIndex = 0;
							for (int _i1 = 0; _i1 < (int) RestaurantsArray.size(); _i1++) {
								restaurantsString = RestaurantsArray.get((int) arrayIndex).getAsString();
								PosX = new Object() {
									double convert(String s) {
										try {
											return Double.parseDouble(s.trim());
										} catch (Exception e) {
										}
										return 0;
									}
								}.convert(GetPartFromStringProcedure.execute(0, restaurantsString));
								PosZ = new Object() {
									double convert(String s) {
										try {
											return Double.parseDouble(s.trim());
										} catch (Exception e) {
										}
										return 0;
									}
								}.convert(GetPartFromStringProcedure.execute(1, restaurantsString));
								if (Math.abs(X - PosX) + Math.abs(PosZ - Z) == 1) {
									AddingArray = true;
									if (Owner instanceof Player _player && !_player.level().isClientSide())
										_player.displayClientMessage(Component.literal(("New location was added: [" + (X + ":" + Z + "]"))), false);
									break;
								}
								arrayIndex = arrayIndex + 1;
							}
						}
					}
				}
			}
			if (AddingArray) {
				ModifyRestaurantArrayParameterProcedure.execute(IndexNumber, "restaurants", "locations", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, X + ":" + Z);
				{
					final Vec3 _center = new Vec3(x, y, z);
					for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(100 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
						if ((entityiterator instanceof LocationEdgeEntity || entityiterator instanceof LocationEdgeDownEntity || entityiterator instanceof LocationEdgeLeftEntity || entityiterator instanceof LocationEdgeRightEntity)
								&& entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID == entityiterator.getPersistentData().getDouble("RestaurantID")) {
							if (!entityiterator.level().isClientSide())
								entityiterator.discard();
						}
					}
				}
				{
					final String _tagName = "SpawnArea";
					final boolean _tagValue = true;
					CustomData.update(DataComponents.CUSTOM_DATA, (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY), tag -> tag.putBoolean(_tagName, _tagValue));
				}
			} else {
				if (Owner instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("Location was not added!"), false);
			}
		} else {
			if (Owner instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("Location has to be set in overworld!"), false);
		}
	}
}