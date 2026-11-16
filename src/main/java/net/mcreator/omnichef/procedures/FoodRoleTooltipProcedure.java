package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;

import java.util.List;

@EventBusSubscriber(value = {Dist.CLIENT})
public class FoodRoleTooltipProcedure {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		execute(event, event.getItemStack(), event.getToolTip());
	}

	public static void execute(ItemStack itemstack, List<Component> tooltip) {
		execute(null, itemstack, tooltip);
	}

	private static void execute(@Nullable Event event, ItemStack itemstack, List<Component> tooltip) {
		if (tooltip == null)
			return;
		String role = "";
		String foodID = "";
		String variantID = "";
		String variantMod = "";
		String variantName = "";
		double tier = 0;
		double variantCount = 0;
		double variantIndex = 0;
		boolean canServe = false;
		boolean shiftHeld = false;
		ItemStack variantStack = ItemStack.EMPTY;
		foodID = BuiltInRegistries.ITEM.getKey(itemstack.getItem()).toString();
		role = net.mcreator.omnichef.FoodRoleClientCache.getRole(foodID);
		tier = net.mcreator.omnichef.FoodRoleClientCache.getTier(foodID);
		if (tier >= 0 && ((role).equals("INGREDIENT") || (role).equals("FOOD"))) {
			if (tier == 0) {
				tooltip.add(1, Component.literal(("\u00A7fTier: " + (int) tier)));
			} else {
				tooltip.add(1, Component.literal(("\u00A7" + (int) tier + "Tier: " + (int) tier)));
			}
		}
		if ((role).equals("FOOD")) {
			tooltip.add(2, Component.literal("\u00A75Food"));
		} else if ((role).equals("INGREDIENT")) {
			tooltip.add(2, Component.literal("\u00A7aIngredient"));
		}
		variantCount = net.mcreator.omnichef.FoodRoleClientCache.getVariantCount(foodID);
		canServe = net.mcreator.omnichef.FoodRoleClientCache.canServe(foodID);
		shiftHeld = net.minecraft.client.gui.screens.Screen.hasShiftDown();
		if (variantCount > 1) {
			if (canServe) {
				tooltip.add(Component.literal(("\u00A7bRestaurant substitutes: " + new java.text.DecimalFormat("0").format(variantCount) + " variants")));
			} else {
				tooltip.add(Component.literal(("\u00A7bShared discovery: " + new java.text.DecimalFormat("0").format(variantCount) + " variants")));
			}
			if (!shiftHeld) {
				tooltip.add(Component.literal("\u00A78Hold Shift to preview variants"));
			} else {
				variantIndex = 0;
				for (int _i1 = 0; _i1 < (int) Math.min(variantCount, 5); _i1++) {
					variantID = net.mcreator.omnichef.FoodRoleClientCache.getVariants(foodID).get((int) variantIndex);
					variantMod = variantID.substring(0, variantID.indexOf(':'));
					variantStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((variantID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					variantName = variantStack.getDisplayName().getString() + "[" + variantMod + "]";
					if ((variantName).length() > 60) {
						variantName = variantName.substring(0, 56) + "...";
					}
					tooltip.add(Component.literal(("\u00A77" + variantName)));
					variantIndex = variantIndex + 1;
				}
				if (variantCount > 5) {
					tooltip.add(Component.literal(("\u00A78And " + new java.text.DecimalFormat("0").format(variantCount - 5) + " more")));
				}
				if (canServe) {
					tooltip.add(Component.literal("\u00A78Restaurant use only; recipes may differ"));
				} else {
					tooltip.add(Component.literal("\u00A78Shared unlock; recipe requirements may differ"));
				}
			}
		}
	}
}