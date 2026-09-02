package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class PatiancePacketToClientProcedureProcedure {
	public static void execute(Entity entity, String inboundString) {
		if (entity == null || inboundString == null)
			return;
		String string = "";
		string = inboundString;
		if (new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(string) >= 75) {
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.ClientPatiance = 0;
				_vars.markSyncDirty();
			}
		} else if (new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(string) >= 50) {
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.ClientPatiance = 1;
				_vars.markSyncDirty();
			}
		} else if (new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(string) >= 25) {
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.ClientPatiance = 2;
				_vars.markSyncDirty();
			}
		} else {
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.ClientPatiance = 3;
				_vars.markSyncDirty();
			}
		}
	}
}