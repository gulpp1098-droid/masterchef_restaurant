package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.world.inventory.RestaurantResultGUIMenu;
import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModMenus;

import io.netty.buffer.Unpooled;

public class OpenRestaurantResultGUIProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return false;
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject result = new com.google.gson.JsonObject();
		double restaurantID = 0;
		double day = 0;
		double totalCustomers = 0;
		double reputationChange = 0;
		String reputationText = "";
		if (world.isClientSide()) {
			return false;
		}
		restaurantID = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (RestaurantIndexSearchByIDProcedure.execute(world, restaurantID) < 0) {
			return false;
		}
		restaurantObject = FindRestaurantInfoByIndexViaIDProcedure.execute(world, restaurantID);
		if (!restaurantObject.has("last_shift_result")) {
			return false;
		}
		result = restaurantObject.get("last_shift_result").getAsJsonObject();
		day = Math.floor(result.get("shift_id").getAsDouble() / 24000) + 1;
		totalCustomers = result.get("customers_served_fully").getAsDouble() + result.get("customers_served").getAsDouble() + result.get("customers_lost").getAsDouble();
		reputationChange = result.get("reputation_change").getAsDouble();
		reputationText = new java.text.DecimalFormat("0").format(reputationChange);
		if (reputationChange > 0) {
			reputationText = "+" + reputationText;
		}
		if (entity instanceof ServerPlayer _ent) {
			BlockPos _bpos = BlockPos.containing(x, y, z);
			_ent.openMenu(new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("RestaurantResultGUI");
				}

				@Override
				public boolean shouldTriggerClientSideContainerClosingOnOpen() {
					return false;
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					return new RestaurantResultGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
				}
			}, _bpos);
		}
		if (entity instanceof Player _guiToolsLabelPlayer9 && _guiToolsLabelPlayer9.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu9) {
			_guiToolsLabelMenu9.sendMenuStateUpdate(_guiToolsLabelPlayer9, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_day_18_results", ""),
					java.util.Objects.toString(("DAY " + new java.text.DecimalFormat("0").format(day) + " RESULTS"), ""), true);
		}
		if (entity instanceof Player _guiToolsLabelPlayer10 && _guiToolsLabelPlayer10.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu10) {
			_guiToolsLabelMenu10.sendMenuStateUpdate(_guiToolsLabelPlayer10, 0, "gui_tools:sized:" + java.util.Objects.toString("sized_text_label_customers_20", ""),
					java.util.Objects.toString(("CUSTOMERS: " + new java.text.DecimalFormat("0").format(totalCustomers)), ""), true);
		}
		if (entity instanceof Player _guiToolsLabelPlayer14 && _guiToolsLabelPlayer14.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu14) {
			_guiToolsLabelMenu14.sendMenuStateUpdate(_guiToolsLabelPlayer14, 0, "gui_tools:multiline:" + java.util.Objects.toString("multiline_label_1062", ""),
					java.util.Objects.toString((new java.text.DecimalFormat("0").format(result.get("customers_served_fully").getAsDouble()) + "\n" + "\n" + new java.text.DecimalFormat("0").format(result.get("customers_served").getAsDouble()) + "\n"
							+ "\n" + new java.text.DecimalFormat("0").format(result.get("customers_lost").getAsDouble())), ""),
					true);
		}
		if (entity instanceof Player _guiToolsLabelPlayer17 && _guiToolsLabelPlayer17.containerMenu instanceof OmnichefModMenus.MenuAccessor _guiToolsLabelMenu17) {
			_guiToolsLabelMenu17.sendMenuStateUpdate(_guiToolsLabelPlayer17, 0, "gui_tools:multiline:" + java.util.Objects.toString("multiline_label_1002010", ""), java.util.Objects.toString(
					(new java.text.DecimalFormat("0.#").format(result.get("satisfaction").getAsDouble()) + "%" + "\n" + "\n" + "+" + new java.text.DecimalFormat("0").format(result.get("coins_earned").getAsDouble()) + "\n" + "\n" + reputationText),
					""), true);
		}
		{
			OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
			_vars.LastViewedRestaurantResult = new java.text.DecimalFormat("0").format(restaurantID) + ":" + new java.text.DecimalFormat("0").format(result.get("shift_id").getAsDouble());
			_vars.RestaurantResultUnread = false;
			_vars.markSyncDirty();
		}
		return true;
	}
}