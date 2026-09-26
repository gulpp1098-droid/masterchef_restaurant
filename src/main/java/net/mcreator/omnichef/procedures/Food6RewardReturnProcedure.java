package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.init.OmnichefModMenus;

public class Food6RewardReturnProcedure {
	public static String execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return "";
		double tier = 0;
		tier = GetFoodTierByItemProcedure.execute(world,
				entity instanceof Player _guiToolsDisplayPlayer0 && _guiToolsDisplayPlayer0.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu0
						? _guiToolsDisplayMenu0.getMenuState(3, Integer.toString(5), ItemStack.EMPTY)
						: ItemStack.EMPTY);
		if (tier == -1) {
			return "-";
		}
		return "" + (int) (tier * 2 + 2);
	}
}