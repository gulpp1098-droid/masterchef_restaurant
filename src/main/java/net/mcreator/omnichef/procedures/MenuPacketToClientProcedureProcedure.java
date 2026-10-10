package net.mcreator.omnichef.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.omnichef.init.OmnichefModMenus;
import net.mcreator.omnichef.OmnichefMod;

public class MenuPacketToClientProcedureProcedure {
	public static void execute(Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		double index = 0;
		double loopSize = 0;
		double amount = 0;
		String foodID = "";
		com.google.gson.JsonObject menuObject = new com.google.gson.JsonObject();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArray = new com.google.gson.JsonArray();
		ItemStack displayStack = ItemStack.EMPTY;
		menuObject = new Object() {
			public com.google.gson.JsonObject parse(String rawJson) {
				try {
					return new com.google.gson.Gson().fromJson(rawJson, com.google.gson.JsonObject.class);
				} catch (Exception e) {
					OmnichefMod.LOGGER.error(e);
					return new com.google.gson.Gson().fromJson("{}", com.google.gson.JsonObject.class);
				}
			}
		}.parse(inboundString);
		foodArray = menuObject.get("food").getAsJsonArray();
		foodAmountArray = menuObject.get("food_amount").getAsJsonArray();
		loopSize = Math.min(6, Math.min(foodArray.size(), foodAmountArray.size()));
		for (int _i1 = 0; _i1 < (int) loopSize; _i1++) {
			foodID = foodArray.get((int) index).getAsString();
			amount = foodAmountArray.get((int) index).getAsDouble();
			displayStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((foodID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
			displayStack.setCount((int) amount);
			if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack9 = displayStack.copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (index + 1)), _displayStack9, true);
			}
			index = index + 1;
		}
	}
}