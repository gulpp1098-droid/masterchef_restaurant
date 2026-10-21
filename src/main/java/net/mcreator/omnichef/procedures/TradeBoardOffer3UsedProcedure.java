package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;

import net.mcreator.omnichef.init.OmnichefModMenus;

public class TradeBoardOffer3UsedProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		ItemStack displayStack = ItemStack.EMPTY;
		displayStack = (entity instanceof Player _guiToolsDisplayPlayer0 && _guiToolsDisplayPlayer0.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu0
				? _guiToolsDisplayMenu0.getMenuState(3, Integer.toString(4), ItemStack.EMPTY)
				: ItemStack.EMPTY).copy();
		return !displayStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("trade_board_used");
	}
}