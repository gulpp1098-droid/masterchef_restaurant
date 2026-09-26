package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.init.OmnichefModMenus;

public class Food2NameReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		if ((((entity instanceof Player _guiToolsDisplayPlayer0 && _guiToolsDisplayPlayer0.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu0
				? _guiToolsDisplayMenu0.getMenuState(3, Integer.toString(1), ItemStack.EMPTY)
				: ItemStack.EMPTY).getDisplayName().getString())
				.substring(
						(int) ((entity instanceof Player _guiToolsDisplayPlayer0 && _guiToolsDisplayPlayer0.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu0
								? _guiToolsDisplayMenu0.getMenuState(3, Integer.toString(1), ItemStack.EMPTY)
								: ItemStack.EMPTY).getDisplayName().getString()).indexOf("[") + "[".length(),
						(int) ((entity instanceof Player _guiToolsDisplayPlayer0 && _guiToolsDisplayPlayer0.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu0
								? _guiToolsDisplayMenu0.getMenuState(3, Integer.toString(1), ItemStack.EMPTY)
								: ItemStack.EMPTY).getDisplayName().getString()).lastIndexOf("]")))
				.equals("Air")) {
			return "";
		}
		return ((entity instanceof Player _guiToolsDisplayPlayer3 && _guiToolsDisplayPlayer3.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu3
				? _guiToolsDisplayMenu3.getMenuState(3, Integer.toString(1), ItemStack.EMPTY)
				: ItemStack.EMPTY).getDisplayName().getString())
				.substring(
						(int) ((entity instanceof Player _guiToolsDisplayPlayer3 && _guiToolsDisplayPlayer3.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu3
								? _guiToolsDisplayMenu3.getMenuState(3, Integer.toString(1), ItemStack.EMPTY)
								: ItemStack.EMPTY).getDisplayName().getString()).indexOf("[") + "[".length(),
						(int) ((entity instanceof Player _guiToolsDisplayPlayer3 && _guiToolsDisplayPlayer3.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu3
								? _guiToolsDisplayMenu3.getMenuState(3, Integer.toString(1), ItemStack.EMPTY)
								: ItemStack.EMPTY).getDisplayName().getString()).indexOf("]"));
	}
}