package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModMenus;

public class FillSlotsMenuProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonArray Menu = new com.google.gson.JsonArray();
		com.google.gson.JsonArray checkMenu = new com.google.gson.JsonArray();
		String Food1 = "";
		String Food2 = "";
		String Food3 = "";
		Entity player = null;
		double restaurantID = 0;
		double page = 0;
		double index1 = 0;
		double index2 = 0;
		double index3 = 0;
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		player = entity;
		restaurantID = player.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
			ItemStack _displayStack0 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack0, true);
			ItemStack _displayStack1 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack1, true);
			ItemStack _displayStack2 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(2), _displayStack2, true);
		}
		if (restaurantID > 0) {
			database = ReadFoodDatabaseProcedure.execute(world);
			if (GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
				Menu = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "menu", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			} else {
				checkMenu = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "menu", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				if (GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "menu_advance_ready") || checkMenu.size() == 0) {
					Menu = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "next_menu", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
							OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				} else {
					Menu = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "menu", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
							OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
				}
			}
			page = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("page");
			index1 = page * 3;
			index2 = page * 3 + 1;
			index3 = page * 3 + 2;
			if (Menu.size() > index1) {
				Food1 = Menu.get((int) index1).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack8 = CreateMenuFoodDisplayStackProcedure.execute(database, Food1).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack8, true);
				}
			}
			if (Menu.size() > index2) {
				Food2 = Menu.get((int) index2).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack11 = CreateMenuFoodDisplayStackProcedure.execute(database, Food2).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack11, true);
				}
			}
			if (Menu.size() > index3) {
				Food3 = Menu.get((int) index3).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack14 = CreateMenuFoodDisplayStackProcedure.execute(database, Food3).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(2), _displayStack14, true);
				}
			}
		}
	}
}