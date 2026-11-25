package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModMenus;

public class FillChefsDiaryDependentsProcedure {
	public static void execute(Entity entity, com.google.gson.JsonObject databaseObject) {
		if (entity == null || databaseObject == null)
			return;
		com.google.gson.JsonArray dependentItems = new com.google.gson.JsonArray();
		String candidateID = "";
		ItemStack displayStack = ItemStack.EMPTY;
		double rowIndex = 0;
		double itemIndex = 0;
		double pageCount = 0;
		dependentItems = GetDiaryDependentItemsProcedure.execute(databaseObject, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiarySelectedItem);
		pageCount = Math.max(1, Math.ceil(dependentItems.size() / (int) 5));
		{
			OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
			_vars.DiaryDependentPage = Math.max(0, Math.min(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryDependentPage, pageCount - 1));
			_vars.markSyncDirty();
		}
		for (int _i1 = 0; _i1 < 5; _i1++) {
			if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack2 = new ItemStack(Blocks.AIR).copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (rowIndex + 10)), _displayStack2, true);
			}
			if (entity instanceof Player _guiToolsLabelPlayer3 && _guiToolsLabelPlayer3.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu3) {
				_guiToolsLabelMenu3.sendMenuStateUpdate(_guiToolsLabelPlayer3, 0, "gui_tools:sized:" + java.util.Objects.toString(("dependent_row_" + new java.text.DecimalFormat("0").format(rowIndex)), ""), java.util.Objects.toString("", ""), true);
			}
			itemIndex = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryDependentPage * 5 + rowIndex;
			if (itemIndex < dependentItems.size()) {
				candidateID = dependentItems.get((int) itemIndex).getAsString();
				displayStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((candidateID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack7 = displayStack.copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (rowIndex + 10)), _displayStack7, true);
				}
				if (entity instanceof Player _guiToolsLabelPlayer10 && _guiToolsLabelPlayer10.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu10) {
					_guiToolsLabelMenu10.sendMenuStateUpdate(_guiToolsLabelPlayer10, 0, "gui_tools:sized:" + java.util.Objects.toString(("dependent_row_" + new java.text.DecimalFormat("0").format(rowIndex)), ""), java.util.Objects
							.toString(((displayStack.getDisplayName().getString()).substring((int) (displayStack.getDisplayName().getString()).indexOf("[") + "[".length(), (int) (displayStack.getDisplayName().getString()).indexOf("]"))), ""), true);
				}
			}
			rowIndex = rowIndex + 1;
		}
		if (entity instanceof Player _guiToolsLabelPlayer12 && _guiToolsLabelPlayer12.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu12) {
			_guiToolsLabelMenu12.sendMenuStateUpdate(_guiToolsLabelPlayer12, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy_copy_copy_copy_copy", ""),
					java.util.Objects.toString(("Related recipes: " + new java.text.DecimalFormat("0").format(dependentItems.size())), ""), true);
		}
		if (entity instanceof Player _guiToolsLabelPlayer13 && _guiToolsLabelPlayer13.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu13) {
			_guiToolsLabelMenu13.sendMenuStateUpdate(_guiToolsLabelPlayer13, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_page_1_1", ""),
					java.util.Objects.toString(("Page " + new java.text.DecimalFormat("0").format(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryDependentPage + 1) + " / " + new java.text.DecimalFormat("0").format(pageCount)), ""), true);
		}
	}
}