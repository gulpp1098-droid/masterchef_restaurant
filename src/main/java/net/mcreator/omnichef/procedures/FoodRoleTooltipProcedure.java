package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
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
		double tier = 0;
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
			tooltip.add(2, Component.literal("\u00A7aFood"));
		} else if ((role).equals("INGREDIENT")) {
			tooltip.add(2, Component.literal("\u00A77Ingredient"));
		}
	}
}