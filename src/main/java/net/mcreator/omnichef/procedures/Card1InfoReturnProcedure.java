package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;

import net.mcreator.omnichef.init.OmnichefModMenus;

public class Card1InfoReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		ItemStack card = ItemStack.EMPTY;
		card = (entity instanceof Player _guiToolsDisplayPlayer0 && _guiToolsDisplayPlayer0.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu0
				? _guiToolsDisplayMenu0.getMenuState(3, Integer.toString(0), ItemStack.EMPTY)
				: ItemStack.EMPTY).copy();
		if ((Food1NameReturnProcedure.execute(entity)).equals("")) {
			return "";
		}
		return "Tier " + new java.text.DecimalFormat("0").format(card.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("RecipeTier")) + "\n" + Food1NameReturnProcedure.execute(entity);
	}
}