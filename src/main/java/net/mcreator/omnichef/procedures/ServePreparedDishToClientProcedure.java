package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;

public class ServePreparedDishToClientProcedure {
	public static boolean execute(LevelAccessor world, Entity clientEntity, ItemStack preparedDish) {
		if (clientEntity == null)
			return false;
		Entity client = null;
		ItemStack dishStack = ItemStack.EMPTY;
		com.google.gson.JsonArray orderFoodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray orderAmountArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonObject comparisonResult = new com.google.gson.JsonObject();
		client = clientEntity;
		dishStack = preparedDish.copy();
		if (client.getPersistentData().getBoolean("order_served")) {
			return false;
		}
		if (!IsPreparedDishDataValidProcedure.execute(dishStack)) {
			return false;
		}
		orderFoodArray = GetClientOrderFoodArrayProcedure.execute(client);
		orderAmountArray = GetClientOrderAmountArrayProcedure.execute(client);
		database = ReadFoodDatabaseProcedure.execute(world);
		comparisonResult = ComparePreparedDishWithOrderProcedure.execute(dishStack, orderAmountArray, orderFoodArray, database);
		client.getPersistentData().putString("oder_result", ("" + comparisonResult));
		client.getPersistentData().putBoolean("order_served", true);
		return true;
	}
}