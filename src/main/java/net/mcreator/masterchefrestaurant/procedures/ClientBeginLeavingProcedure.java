package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

public class ClientBeginLeavingProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		double RecX = 0;
		double RecY = 0;
		double RecZ = 0;
		String ReceptionPosition = "";
		client = entity;
		if (!client.getPersistentData().getBoolean("leaving_started")) {
			client.getPersistentData().putBoolean("leaving_started", true);
			if (client.getPersistentData().getBoolean("leader")) {
				if (client.getPersistentData().getBoolean("queue_registered")) {
					SetLogicNBTProcedure.execute(world, client.getPersistentData().getDouble("DestX"), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ"), false, "occupied");
					ReceptionPosition = GetRestaurantStringParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID")), "restaurants",
							MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
					RecX = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(0, ReceptionPosition));
					RecY = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(1, ReceptionPosition));
					RecZ = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(2, ReceptionPosition));
					SetNumberNBTProcedure.execute(world, RecX, RecY, RecZ, getBlockNBTNumber(world, BlockPos.containing(RecX, RecY, RecZ), "queue_length") - 1, "queue_length");
					client.getPersistentData().putBoolean("queue_registered", false);
				}
				if (client.getPersistentData().getBoolean("table_reserved")) {
					SetLogicNBTProcedure.execute(world, client.getPersistentData().getDouble("reserved_table_x"), client.getPersistentData().getDouble("reserved_table_y"), client.getPersistentData().getDouble("reserved_table_z"), false, "occupied");
					client.getPersistentData().putBoolean("table_reserved", false);
				}
			}
			client.getPersistentData().putString("state", "leave");
			client.getPersistentData().putDouble("stuckCounter", (-1));
			client.stopRiding();
			client.getPersistentData().putDouble("despawn_time", (world.dayTime() + 250));
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}