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
		String foodLabel = "";
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
			foodLabel = new java.text.DecimalFormat("0").format(amount) + "x "
					+ (displayStack.getDisplayName().getString()).substring((int) (displayStack.getDisplayName().getString()).indexOf("[") + "[".length(), (int) (displayStack.getDisplayName().getString()).indexOf("]"));
			displayStack.setCount((int) amount);
			if (entity instanceof Player _guiToolsLabelPlayer11 && _guiToolsLabelPlayer11.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu11) {
				_guiToolsLabelMenu11.sendMenuStateUpdate(_guiToolsLabelPlayer11, 0, "gui_tools:sized:" + java.util.Objects.toString(("sized_text_label_food_" + new java.text.DecimalFormat("0").format(index + 1) + "_name"), ""),
						java.util.Objects.toString(foodLabel, ""), true);
			}
			if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack12 = displayStack.copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (index + 1)), _displayStack12, true);
			}
			index = index + 1;
		}
	}
}