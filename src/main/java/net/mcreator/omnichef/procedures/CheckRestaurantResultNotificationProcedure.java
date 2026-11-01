package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class CheckRestaurantResultNotificationProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		com.google.gson.JsonObject restaurantObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject result = new com.google.gson.JsonObject();
		com.google.gson.JsonObject lastShift = new com.google.gson.JsonObject();
		double restaurantID = 0;
		String resultKey = "";
		boolean unread = false;
		if (world.isClientSide()) {
			return true;
		}
		if (!OmnichefModVariables.MapVariables.get(world).ModDataReady) {
			return true;
		}
		if (!(world.dayTime() % 100 == 0)) {
			return true;
		}
		restaurantID = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID;
		if (restaurantID <= 0) {
			if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).RestaurantResultUnread) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.RestaurantResultUnread = false;
					_vars.markSyncDirty();
				}
			}
			return true;
		}
		restaurantObject = FindRestaurantInfoByIndexViaIDProcedure.execute(world, restaurantID);
		if (!restaurantObject.has("last_shift_result")) {
			if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).RestaurantResultUnread) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.RestaurantResultUnread = false;
					_vars.markSyncDirty();
				}
			}
			return true;
		}
		lastShift = restaurantObject.get("last_shift_result").getAsJsonObject();
		result.add("last_shift_result", lastShift);
		resultKey = new java.text.DecimalFormat("0").format(restaurantID) + ":" + new java.text.DecimalFormat("0").format(result.get("shift_id").getAsDouble());
		unread = !(resultKey).equals(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).LastViewedRestaurantResult);
		if (!(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).RestaurantResultUnread == unread)) {
			{
				OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
				_vars.RestaurantResultUnread = unread;
				_vars.markSyncDirty();
			}
		}
		if (unread) {
			if (!(resultKey).equals(entity.getData(OmnichefModVariables.PLAYER_VARIABLES).LastNotifiedRestaurantResult)) {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("Your shift results are ready. Open your inventory to view them."), false);
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.LastNotifiedRestaurantResult = resultKey;
					_vars.markSyncDirty();
				}
			}
		}
		return true;
	}
}