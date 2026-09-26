package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.omnichef.init.OmnichefModMenus;

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
		String _toSplit21 = (inboundString.substring((int) inboundString.indexOf("[") + "[".length(), (int) inboundString.lastIndexOf("]")));
		String[] _array21 = _toSplit21.split(Pattern.quote(","));
		for (int _iter21 = 0; _iter21 < Math.max(1, _array21.length); _iter21++) {
			String stringiterator = _array21.length == 0 ? _toSplit21 : _array21[_iter21];
			if (foodIndex == 0) {
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack4 = new ItemStack(
							BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(1), _displayStack4, true);
				}
			} else if (foodIndex == 1) {
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack8 = new ItemStack(
							BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(2), _displayStack8, true);
				}
			} else if (foodIndex == 2) {
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack12 = new ItemStack(
							BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(3), _displayStack12, true);
				}
			} else if (foodIndex == 3) {
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack16 = new ItemStack(
							BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(4), _displayStack16, true);
				}
			} else {
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack20 = new ItemStack(
							BuiltInRegistries.ITEM.get(ResourceLocation.parse(((stringiterator.substring((int) stringiterator.indexOf("\"") + "\"".length(), (int) stringiterator.lastIndexOf("\"")))).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(5), _displayStack20, true);
				}
			}
			foodIndex = foodIndex + 1;
		}
	}
}