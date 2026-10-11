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

import net.mcreator.omnichef.init.OmnichefModItems;

import javax.annotation.Nullable;

import java.util.List;

@EventBusSubscriber(value = {Dist.CLIENT})
public class PreparedDishTooltipProcedure {
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
		ItemStack dishStack = ItemStack.EMPTY;
		ItemStack foodStack = ItemStack.EMPTY;
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray amountArray = new com.google.gson.JsonArray();
		double loopSize = 0;
		double index = 0;
		double amount = 0;
		String foodID = "";
		dishStack = itemstack.copy();
		if (dishStack.getItem() == OmnichefModItems.PREPARED_DISH.get()) {
			foodArray = GetPreparedDishFoodArrayProcedure.execute(dishStack);
			amountArray = GetPreparedDishAmountArrayProcedure.execute(dishStack);
			loopSize = Math.min(foodArray.size(), amountArray.size());
			tooltip.add(Component.literal("Contents:"));
			for (int _i1 = 0; _i1 < (int) loopSize; _i1++) {
				foodID = foodArray.get((int) index).getAsString();
				amount = amountArray.get((int) index).getAsDouble();
				foodStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((foodID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
				tooltip.add(Component.literal((foodStack.getDisplayName().getString() + " x" + new java.text.DecimalFormat("0").format(amount))));
				index = index + 1;
			}
		}
	}
}