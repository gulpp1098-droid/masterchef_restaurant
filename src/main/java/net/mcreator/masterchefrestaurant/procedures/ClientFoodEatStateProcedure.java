package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;

public class ClientFoodEatStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		String foodDelivered = "";
		String orderedFood = "";
		ArrayList<Object> array = new ArrayList<>();
		double ordered = 0;
		double delivered = 0;
		double totalMultiplayer = 0;
		double EXPsum = 0;
		double index = 0;
		double CurrentReputation = 0;
		double EXPTotal = 0;
		client = entity;
		if (client.getPersistentData().getDouble("food_eat_time") <= world.dayTime()) {
			ClientCoinPayProcedure.execute(world, entity);
			ClientExpPayProcedure.execute(world, entity);
			client.getPersistentData().putString("state", "group_wait");
		}
	}
}