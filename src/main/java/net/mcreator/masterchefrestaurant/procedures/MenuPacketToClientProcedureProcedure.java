package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModMenus;

import java.util.regex.Pattern;

public class MenuPacketToClientProcedureProcedure {
	public static void execute(Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		double foodIndex = 0;
		String foodList = "";
		String foodOne = "";
		String foodFive = "";
		String foodFour = "";
		String foodTwo = "";
		String foodThree = "";
		String[] _array21 = (inboundString.substring((int) inboundString.indexOf("[") + "[".length(), (int) inboundString.lastIndexOf("]"))).split(Pattern.quote(","));
		if (_array21.length != 0) {
			for (String stringiterator : _array21) {
				if (foodIndex == 0) {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack4 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack4.setCount(1);
						_menu.getSlots().get(1).set(_setstack4);
						_player.containerMenu.broadcastChanges();
					}
				} else if (foodIndex == 1) {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack8 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack8.setCount(1);
						_menu.getSlots().get(2).set(_setstack8);
						_player.containerMenu.broadcastChanges();
					}
				} else if (foodIndex == 2) {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack12 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack12.setCount(1);
						_menu.getSlots().get(3).set(_setstack12);
						_player.containerMenu.broadcastChanges();
					}
				} else if (foodIndex == 3) {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack16 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack16.setCount(1);
						_menu.getSlots().get(4).set(_setstack16);
						_player.containerMenu.broadcastChanges();
					}
				} else {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack20 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack20.setCount(1);
						_menu.getSlots().get(5).set(_setstack20);
						_player.containerMenu.broadcastChanges();
					}
				}
				foodIndex = foodIndex + 1;
			}
		} else {
			String stringiterator = (inboundString.substring((int) inboundString.indexOf("[") + "[".length(), (int) inboundString.lastIndexOf("]")));
			for (int _yourmother = 0; _yourmother < 1; _yourmother++) {
				if (foodIndex == 0) {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack4 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack4.setCount(1);
						_menu.getSlots().get(1).set(_setstack4);
						_player.containerMenu.broadcastChanges();
					}
				} else if (foodIndex == 1) {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack8 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack8.setCount(1);
						_menu.getSlots().get(2).set(_setstack8);
						_player.containerMenu.broadcastChanges();
					}
				} else if (foodIndex == 2) {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack12 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack12.setCount(1);
						_menu.getSlots().get(3).set(_setstack12);
						_player.containerMenu.broadcastChanges();
					}
				} else if (foodIndex == 3) {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack16 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack16.setCount(1);
						_menu.getSlots().get(4).set(_setstack16);
						_player.containerMenu.broadcastChanges();
					}
				} else {
					if (entity instanceof Player _player && _player.containerMenu instanceof MasterchefRestaurantModMenus.MenuAccessor _menu) {
						ItemStack _setstack20 = new ItemStack(
								BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH))))
								.copy();
						_setstack20.setCount(1);
						_menu.getSlots().get(5).set(_setstack20);
						_player.containerMenu.broadcastChanges();
					}
				}
				foodIndex = foodIndex + 1;
			}
		}
	}
}