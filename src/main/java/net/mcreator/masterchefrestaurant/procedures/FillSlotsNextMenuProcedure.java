package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.Blocks;
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

public class FillSlotsNextMenuProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonArray Menu = new com.google.gson.JsonArray();
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
		player = entity;
		restaurantID = player.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
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
			Menu = GetRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", "next_menu", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
					MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path);
			page = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("page");
			index1 = page * 6;
			index2 = page * 6 + 1;
			index3 = page * 6 + 2;
			index4 = page * 6 + 3;
			index5 = page * 6 + 4;
			index6 = page * 6 + 5;
			if (Menu.size() > index1) {
				Food1 = Menu.get((int) index1).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _displayStack11 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food1).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack11, true);
				}
			}
			if (Menu.size() > index2) {
				Food2 = Menu.get((int) index2).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _displayStack15 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food2).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack15, true);
				}
			}
			if (Menu.size() > index3) {
				Food3 = Menu.get((int) index3).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _displayStack19 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food3).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(2), _displayStack19, true);
				}
			}
			if (Menu.size() > index4) {
				Food4 = Menu.get((int) index4).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _displayStack23 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food4).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(3), _displayStack23, true);
				}
			}
			if (Menu.size() > index5) {
				Food5 = Menu.get((int) index5).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _displayStack27 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food5).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(4), _displayStack27, true);
				}
			}
			if (Menu.size() > index6) {
				Food6 = Menu.get((int) index6).getAsString();
				if (player instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
					ItemStack _displayStack31 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((Food6).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(5), _displayStack31, true);
				}
			}
		}
	}
}