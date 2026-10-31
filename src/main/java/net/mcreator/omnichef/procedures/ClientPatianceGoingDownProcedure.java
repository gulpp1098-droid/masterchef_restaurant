package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.entity.CriticEntity;
import net.mcreator.omnichef.entity.ClientEntity;

public class ClientPatianceGoingDownProcedure {
	public static boolean execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return false;
		Entity client = null;
		double nbtPatience = 0;
		double restaurantID = 0;
		client = entity;
		if (world.dayTime() >= client.getPersistentData().getDouble("patience_end_time") && !client.getPersistentData().getBoolean("leaving_started") && client.getPersistentData().getBoolean("patience_needed")
				&& !client.getPersistentData().getBoolean("order_served")) {
			if (client instanceof ClientEntity) {
				if (!RecordClientShiftResultProcedure.execute(world, client)) {
					return true;
				}
			}
			client.getPersistentData().putBoolean("patience_needed", false);
			restaurantID = client.getPersistentData().getDouble("RestaurantID");
			ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), 1, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
					OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "customers_lost");
			client.getPersistentData().putBoolean("service_result_counted", true);
			client.getPersistentData().putString("state", "group_wait");
		}
		if (client.getPersistentData().getBoolean("patience_needed") && !client.getPersistentData().getBoolean("leaving_started") && !client.getPersistentData().getBoolean("order_served")) {
			if (client instanceof ClientEntity _datEntSetI)
				_datEntSetI.getEntityData().set(ClientEntity.DATA_PatienceBar,
						(int) (1000 * Math.max(0, Math.min(1, (client.getPersistentData().getDouble("patience_end_time") - world.dayTime()) / Math.max(1, client.getPersistentData().getDouble("patience_duration"))))));
			if (client instanceof CriticEntity _datEntSetI)
				_datEntSetI.getEntityData().set(CriticEntity.DATA_PatienceBar,
						(int) (1000 * Math.max(0, Math.min(1, (client.getPersistentData().getDouble("patience_end_time") - world.dayTime()) / Math.max(1, client.getPersistentData().getDouble("patience_duration"))))));
		} else {
			if (client instanceof ClientEntity _datEntSetI)
				_datEntSetI.getEntityData().set(ClientEntity.DATA_PatienceBar, -1);
			if (client instanceof CriticEntity _datEntSetI)
				_datEntSetI.getEntityData().set(CriticEntity.DATA_PatienceBar, -1);
		}
		return true;
	}
}