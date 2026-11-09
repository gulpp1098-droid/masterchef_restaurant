package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;

import net.mcreator.omnichef.init.OmnichefModMenus;

public class Card3TextureReturnProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		ItemStack card = ItemStack.EMPTY;
		double textureIndex = 0;
		card = (entity instanceof Player _guiToolsDisplayPlayer0 && _guiToolsDisplayPlayer0.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsDisplayMenu0
				? _guiToolsDisplayMenu0.getMenuState(3, Integer.toString(2), ItemStack.EMPTY)
				: ItemStack.EMPTY).copy();
		textureIndex = card.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble("RecipeCardTextureIndex");
		if (textureIndex == 1) {
			return "card-big-tier1.png";
		} else if (textureIndex == 2) {
			return "card-big-tier2.png";
		} else if (textureIndex == 3) {
			return "card-big-tier3.png";
		} else if (textureIndex == 4) {
			return "card-big-tier4.png";
		} else if (textureIndex == 5) {
			return "card-big-tier5.png";
		} else if (textureIndex == 6) {
			return "card-big-tier6.png";
		} else if (textureIndex == 7) {
			return "card-big-tier7.png";
		} else if (textureIndex == 8) {
			return "card-big-tier8.png";
		} else if (textureIndex == 9) {
			return "card-big-tier9.png";
		}
		return "card-big-tier0.png";
	}
}