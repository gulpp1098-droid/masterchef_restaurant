package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

import java.util.List;

@EventBusSubscriber(value = {Dist.CLIENT})
public class FoodRoleTooltipProcedure {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		execute(event, event.getToolTip());
	}

	public static void execute(List<Component> tooltip) {
		execute(null, tooltip);
	}

	private static void execute(@Nullable Event event, List<Component> tooltip) {
		if (tooltip == null)
			return;
		String role = "";
		role = net.mcreator.omnichef.FoodRoleClientCache.getRole(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(itemstack.getItem()).toString());
		if ((role).equals("FOOD")) {
			tooltip.add(1, Component.literal("\u00A7aFood"));
		} else if ((role).equals("INGREDIENT")) {
			tooltip.add(1, Component.literal("\u00A77Ingredient"));
		}
	}
}