package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

public class FillSlotsMenuProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonArray Menu = new com.google.gson.JsonArray();
		String Food1 = "";
		String Food2 = "";
		String Food3 = "";
		Entity player = null;
		double restaurantID = 0;
		double page = 0;
		double index1 = 0;
		double index2 = 0;
		double index3 = 0;
		player = entity;
		restaurantID = player.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
			_menu.getSlots().get(0).set(ItemStack.EMPTY);
			_menu.getSlots().get(1).set(ItemStack.EMPTY);
			_menu.getSlots().get(2).set(ItemStack.EMPTY);
			_player.containerMenu.broadcastChanges();
		}
		if (restaurantID > 0) {
			if (GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
				Menu = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
			} else {
				Menu = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "next_menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
			}
			page = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("page");
			index1 = page * 3;
			index2 = page * 3 + 1;
			index3 = page * 3 + 2;
			if (Menu.size() > index1) {
				Food1 = Menu.get((int) index1).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _setstack8 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food1).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_setstack8.setCount(1);
					_menu.getSlots().get(0).set(_setstack8);
					_player.containerMenu.broadcastChanges();
				}
			}
			if (Menu.size() > index2) {
				Food2 = Menu.get((int) index2).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _setstack12 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food2).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_setstack12.setCount(1);
					_menu.getSlots().get(1).set(_setstack12);
					_player.containerMenu.broadcastChanges();
				}
			}
			if (Menu.size() > index3) {
				Food3 = Menu.get((int) index3).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _setstack16 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food3).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_setstack16.setCount(1);
					_menu.getSlots().get(2).set(_setstack16);
					_player.containerMenu.broadcastChanges();
				}
			}
		}
	}
}