package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.Blocks;
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
		com.google.gson.JsonObject recipeObject = new com.google.gson.JsonObject();
		String foodID = "";
		String role = "";
		String unlockedKey = "";
		String ingredientID = "";
		ItemStack displayStack = ItemStack.EMPTY;
		double tier = 0;
		double restaurantIndex = 0;
		double index = 0;
		double slotIndex = 0;
		com.google.gson.JsonArray unlockedArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray recipes = new com.google.gson.JsonArray();
		com.google.gson.JsonArray ingredientSlots = new com.google.gson.JsonArray();
		com.google.gson.JsonArray alternatives = new com.google.gson.JsonArray();
		boolean isUnlocked = false;
		if (!world.isClientSide()) {
			foodID = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiarySelectedItem;
			databaseObject = ReadFoodDatabaseProcedure.execute(world);
			foodObject = GetFoodDataFromIDProcedure.execute(databaseObject, foodID);
			if (foodObject.has("id")) {
				displayStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((foodID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
				tier = foodObject.get("tier").getAsDouble();
				role = foodObject.get("role").getAsString();
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack5 = displayStack.copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString(0), _displayStack5, true);
				}
				if (entity instanceof Player _guiToolsLabelPlayer7 && _guiToolsLabelPlayer7.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu7) {
					_guiToolsLabelMenu7.sendMenuStateUpdate(_guiToolsLabelPlayer7, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label", ""), java.util.Objects.toString((displayStack.getDisplayName().getString()), ""),
							true);
				}
				if (entity instanceof Player _guiToolsLabelPlayer9 && _guiToolsLabelPlayer9.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu9) {
					_guiToolsLabelMenu9.sendMenuStateUpdate(_guiToolsLabelPlayer9, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy", ""), java.util.Objects.toString(("Tier " + (int) tier + " - " + role), ""),
							true);
				}
				restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
				if ((role).equals("INGREDIENT")) {
					unlockedKey = "unlocked_ingredients";
				} else {
					unlockedKey = "unlocked_foods";
				}
				if (restaurantIndex >= 0) {
					unlockedArray = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", unlockedKey, OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
							OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
					for (int _i1 = 0; _i1 < (int) unlockedArray.size(); _i1++) {
						if ((unlockedArray.get((int) index).getAsString()).equals(foodID)) {
							isUnlocked = true;
						}
						index = index + 1;
					}
					if (isUnlocked) {
						if (entity instanceof Player _guiToolsLabelPlayer12 && _guiToolsLabelPlayer12.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu12) {
							_guiToolsLabelMenu12.sendMenuStateUpdate(_guiToolsLabelPlayer12, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy_copy", ""), java.util.Objects.toString("\u00A72Discovered", ""), true);
						}
					} else {
						if (entity instanceof Player _guiToolsLabelPlayer13 && _guiToolsLabelPlayer13.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu13) {
							_guiToolsLabelMenu13.sendMenuStateUpdate(_guiToolsLabelPlayer13, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy_copy", ""), java.util.Objects.toString("\u00A74Not discovered", ""),
									true);
						}
					}
				}
				if (foodObject.get("servable").getAsBoolean()) {
					if (entity instanceof Player _guiToolsLabelPlayer15 && _guiToolsLabelPlayer15.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu15) {
						_guiToolsLabelMenu15.sendMenuStateUpdate(_guiToolsLabelPlayer15, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy_copy_copy_copy", ""), java.util.Objects.toString("Can be served", ""),
								true);
					}
				} else {
					if (entity instanceof Player _guiToolsLabelPlayer16 && _guiToolsLabelPlayer16.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu16) {
						_guiToolsLabelMenu16.sendMenuStateUpdate(_guiToolsLabelPlayer16, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy_copy_copy_copy", ""),
								java.util.Objects.toString("Cooking ingredient only", ""), true);
					}
				}
				recipes = foodObject.get("recipes").getAsJsonArray();
				slotIndex = 1;
				for (int _i1 = 0; _i1 < 9; _i1++) {
					if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
						ItemStack _displayStack18 = new ItemStack(Blocks.AIR).copy();
						_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) slotIndex), _displayStack18, true);
					}
					slotIndex = slotIndex + 1;
				}
				if (recipes.size() > 0) {
					recipeObject = GetFoodRecipeFromDataProcedure.execute(foodObject, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryRecipeIndex);
					ingredientSlots = GetRecipeIngredientSlotsProcedure.execute(recipeObject);
					slotIndex = 1;
					for (int _i1 = 0; _i1 < (int) Math.min(ingredientSlots.size(), 9); _i1++) {
						alternatives = GetIngredientsAlternativesFromSlotProcedure.execute(ingredientSlots, slotIndex);
						if (alternatives.size() > 0) {
							ingredientID = alternatives.get((int) 0).getAsString();
							if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
								ItemStack _displayStack24 = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((ingredientID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
								_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) slotIndex), _displayStack24, true);
							}
						}
						slotIndex = slotIndex + 1;
					}
					if (entity instanceof Player _guiToolsLabelPlayer28 && _guiToolsLabelPlayer28.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu28) {
						_guiToolsLabelMenu28.sendMenuStateUpdate(_guiToolsLabelPlayer28, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy_2", ""),
								java.util.Objects.toString(("Recipe: " + (int) (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryRecipeIndex + 1) + " / " + (int) recipes.size()), ""), true);
					}
				} else {
					if (entity instanceof Player _guiToolsLabelPlayer29 && _guiToolsLabelPlayer29.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu29) {
						_guiToolsLabelMenu29.sendMenuStateUpdate(_guiToolsLabelPlayer29, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy_2", ""), java.util.Objects.toString("No recipe required", ""), true);
					}
				}
			}
		}
	}
}