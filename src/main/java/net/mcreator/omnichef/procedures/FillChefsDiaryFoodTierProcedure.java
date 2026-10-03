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

public class FillChefsDiaryFoodTierProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		double index = 0;
		double tier = 0;
		double page = 0;
		double startIndex = 0;
		double restaurantIndex = 0;
		double slotIndex = 0;
		double foodIndex = 0;
		double unlockedIndex = 0;
		double coins = 0;
		double dishExp = 0;
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray unlockedArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodDataArray = new com.google.gson.JsonArray();
		String foodID = "";
		String rewardText = "";
		ItemStack displayStack = ItemStack.EMPTY;
		boolean isUnlocked = false;
		com.google.gson.JsonObject foodObject = new com.google.gson.JsonObject();
		if (!world.isClientSide()) {
			index = 1;
			for (int _i1 = 0; _i1 < 36; _i1++) {
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack1 = new ItemStack(Blocks.AIR).copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) index), _displayStack1, true);
				}
				index = index + 1;
			}
			index = 101;
			for (int _i1 = 0; _i1 < 36; _i1++) {
				if (entity instanceof Player _guiToolsLabelPlayer3 && _guiToolsLabelPlayer3.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu3) {
					_guiToolsLabelMenu3.sendMenuStateUpdate(_guiToolsLabelPlayer3, 0, "gui_tools:multiline:" + java.util.Objects.toString(("t_" + (int) index), ""), java.util.Objects.toString("", ""), true);
				}
				index = index + 1;
			}
			tier = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodTier;
			page = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryFoodPage;
			startIndex = page * 18;
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
			foodDataArray = GetFoodDataFromTierProcedure.execute(world, tier);
			if (restaurantIndex >= 0) {
				unlockedArray = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "unlocked", OmnichefModVariables.MapVariables.get(world).RestaurantFood_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
			}
			for (int _i1 = 0; _i1 < 18; _i1++) {
				foodIndex = startIndex + slotIndex;
				if (foodIndex < foodDataArray.size()) {
					foodObject = foodDataArray.get((int) foodIndex).getAsJsonObject();
					foodID = foodObject.get("id").getAsString();
					displayStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((foodID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
					coins = 2 * (tier + 1);
					dishExp = foodObject.get("base_exp").getAsDouble();
					rewardText = (int) coins + "\n" + (int) dishExp;
					isUnlocked = false;
					unlockedIndex = 0;
					for (int _i2 = 0; _i2 < (int) unlockedArray.size(); _i2++) {
						if ((foodID).equals(unlockedArray.get((int) unlockedIndex).getAsString())) {
							isUnlocked = true;
						}
						unlockedIndex = unlockedIndex + 1;
					}
					if (isUnlocked) {
						if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
							ItemStack _displayStack13 = displayStack.copy();
							_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (slotIndex + 19)), _displayStack13, true);
						}
						if (entity instanceof Player _guiToolsLabelPlayer15 && _guiToolsLabelPlayer15.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu15) {
							_guiToolsLabelMenu15.sendMenuStateUpdate(_guiToolsLabelPlayer15, 0, "gui_tools:multiline:" + java.util.Objects.toString(("t_" + (int) (slotIndex + 101)), ""), java.util.Objects.toString(rewardText, ""), true);
						}
					} else {
						if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
							ItemStack _displayStack16 = displayStack.copy();
							_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (slotIndex + 1)), _displayStack16, true);
						}
						if (entity instanceof Player _guiToolsLabelPlayer18 && _guiToolsLabelPlayer18.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu18) {
							_guiToolsLabelMenu18.sendMenuStateUpdate(_guiToolsLabelPlayer18, 0, "gui_tools:multiline:" + java.util.Objects.toString(("t_" + (int) (slotIndex + 101)), ""), java.util.Objects.toString(("-" + "\n" + "-"), ""), true);
						}
					}
				}
				slotIndex = slotIndex + 1;
			}
		}
	}
}