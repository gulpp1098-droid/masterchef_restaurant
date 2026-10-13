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

public class FillSlotsNextMenuProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonArray Menu = new com.google.gson.JsonArray();
		com.google.gson.JsonArray checkMenu = new com.google.gson.JsonArray();
		String Food1 = "";
		String Food2 = "";
		String Food3 = "";
		String Food4 = "";
		String Food5 = "";
		String Food6 = "";
		Entity player = null;
		double restaurantID = 0;
		double page = 0;
		double index1 = 0;
		double index2 = 0;
		double index3 = 0;
		double index4 = 0;
		double index5 = 0;
		double index6 = 0;
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
			ItemStack _displayStack3 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(3), _displayStack3, true);
			ItemStack _displayStack4 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(4), _displayStack4, true);
			ItemStack _displayStack5 = new ItemStack(Blocks.AIR).copy();
			_menu.sendMenuStateUpdate(_player, 3, Integer.toString(5), _displayStack5, true);
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
			index1 = page * 6;
			index2 = page * 6 + 1;
			index3 = page * 6 + 2;
			index4 = page * 6 + 3;
			index5 = page * 6 + 4;
			index6 = page * 6 + 5;
			if (Menu.size() > index1) {
				Food1 = Menu.get((int) index1).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack11 = CreateMenuFoodDisplayStackProcedure.execute(database, Food1).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack11, true);
				}
			}
			if (Menu.size() > index2) {
				Food2 = Menu.get((int) index2).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack14 = CreateMenuFoodDisplayStackProcedure.execute(database, Food2).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack14, true);
				}
			}
			if (Menu.size() > index3) {
				Food3 = Menu.get((int) index3).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack17 = CreateMenuFoodDisplayStackProcedure.execute(database, Food3).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(2), _displayStack17, true);
				}
			}
			if (Menu.size() > index4) {
				Food4 = Menu.get((int) index4).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack20 = CreateMenuFoodDisplayStackProcedure.execute(database, Food4).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(3), _displayStack20, true);
				}
			}
			if (Menu.size() > index5) {
				Food5 = Menu.get((int) index5).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack23 = CreateMenuFoodDisplayStackProcedure.execute(database, Food5).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(4), _displayStack23, true);
				}
			}
			if (Menu.size() > index6) {
				Food6 = Menu.get((int) index6).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack26 = CreateMenuFoodDisplayStackProcedure.execute(database, Food6).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(5), _displayStack26, true);
				}
			}
		}
	}
}