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
		pageCount = Math.max(1, Math.ceil(dependentItems.size() / 5));
		{
			OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
			_vars.DiaryDependentPage = Math.max(0, Math.min(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryDependentPage, pageCount - 1));
			_vars.markSyncDirty();
		}
		for (int _i1 = 0; _i1 < 5; _i1++) {
			if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
				ItemStack _displayStack1 = new ItemStack(Blocks.AIR).copy();
				_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (rowIndex + 10)), _displayStack1, true);
			}
			if (entity instanceof Player _guiToolsLabelPlayer2 && _guiToolsLabelPlayer2.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu2) {
				_guiToolsLabelMenu2.sendMenuStateUpdate(_guiToolsLabelPlayer2, 0, "gui_tools:sized:" + java.util.Objects.toString(("dependent_row_" + new java.text.DecimalFormat("0").format(rowIndex)), ""), java.util.Objects.toString("", ""), true);
			}
			itemIndex = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryDependentPage * 5 + rowIndex;
			if (itemIndex == dependentItems.size()) {
				candidateID = dependentItems.get((int) itemIndex).getAsString();
				displayStack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse((candidateID).toLowerCase(java.util.Locale.ENGLISH)))).copy();
				if (entity instanceof Player _player && _player.containerMenu instanceof OmnichefModMenus.MenuAccessor _menu) {
					ItemStack _displayStack6 = displayStack.copy();
					_menu.sendMenuStateUpdate(_player, 3, Integer.toString((int) (rowIndex + 10)), _displayStack6, true);
				}
				if (entity instanceof Player _guiToolsLabelPlayer9 && _guiToolsLabelPlayer9.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu9) {
					_guiToolsLabelMenu9.sendMenuStateUpdate(_guiToolsLabelPlayer9, 0, "gui_tools:sized:" + java.util.Objects.toString(("dependent_row_" + new java.text.DecimalFormat("0").format(rowIndex)), ""), java.util.Objects
							.toString(((displayStack.getDisplayName().getString()).substring((int) (displayStack.getDisplayName().getString()).indexOf("[") + "[".length(), (int) (displayStack.getDisplayName().getString()).indexOf("]"))), ""), true);
				}
			}
			rowIndex = rowIndex + 1;
		}
		if (entity instanceof Player _guiToolsLabelPlayer11 && _guiToolsLabelPlayer11.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu11) {
			_guiToolsLabelMenu11.sendMenuStateUpdate(_guiToolsLabelPlayer11, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_text_label_copy_copy_copy_copy_copy", ""),
					java.util.Objects.toString(("Related recipes: " + new java.text.DecimalFormat("0").format(dependentItems.size())), ""), true);
		}
		if (entity instanceof Player _guiToolsLabelPlayer12 && _guiToolsLabelPlayer12.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu12) {
			_guiToolsLabelMenu12.sendMenuStateUpdate(_guiToolsLabelPlayer12, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_page_1_", ""),
					java.util.Objects.toString(("Page " + new java.text.DecimalFormat("0").format(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).DiaryDependentPage + 1) + " / " + new java.text.DecimalFormat("0").format(pageCount)), ""), true);
		}
	}
}