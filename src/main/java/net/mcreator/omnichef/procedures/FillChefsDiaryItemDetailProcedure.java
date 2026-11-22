package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModMenus;

public class FillChefsDiaryItemDetailProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonObject databaseObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		String foodID = "";
		String role = "";
		ItemStack displayStack = ItemStack.EMPTY;
		double tier = 0;
		if (!world.isClientSide()) {
			foodID = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiarySelectedItem;
			databaseObject = ReadFoodDatabaseProcedure.execute(world);
			foodObject = GetFoodDataFromIDProcedure.execute(foodObject, foodID);
			if (foodObject.has("id")) {
				displayStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((foodID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
				tier = foodObject.get("tier").getAsDouble();
				role = foodObject.get("role").getAsString();
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack5 = displayStack.copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack5, true);
				}
				if (entity instanceof Player _guiToolsLabelPlayer7 && _guiToolsLabelPlayer7.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu7) {
					_guiToolsLabelMenu7.sendMenuStateUpdate(_guiToolsLabelPlayer7, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy", ""), java.util.Objects.toString(("Tier " + (int) tier + " - " + role), ""),
							true);
				}
			}
		}
	}
}