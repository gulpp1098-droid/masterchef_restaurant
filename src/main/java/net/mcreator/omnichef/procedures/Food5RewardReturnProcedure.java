package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.init.OmnichefModMenus;

public class Food5RewardReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		double tier = 0;
		tier = GetFoodTierByItemProcedure.execute(entity instanceof Player _guiToolsDisplayPlayer0 && _guiToolsDisplayPlayer0.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu0
				? _guiToolsDisplayMenu0.getMenuState(3, Integer.toString(4), ItemStack.EMPTY)
				: ItemStack.EMPTY);
		if (tier == -1) {
			return "-";
		}
		return "" + (int) (tier + 1);
	}
}