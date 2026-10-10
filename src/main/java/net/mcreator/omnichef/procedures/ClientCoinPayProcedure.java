package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ClientCoinPayProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		Direction chairDirection = Direction.NORTH;
		double index = 0;
		double CoinSum = 0;
		double CoinTotal = 0;
		double restaurantID = 0;
		double tier = 0;
		double loopSize = 0;
		double satisfaction = 0;
		double amount = 0;
		com.google.gson.JsonObject database = new com.google.gson.JsonObject();
		com.google.gson.JsonArray foodArray = new com.google.gson.JsonArray();
		com.google.gson.JsonArray foodAmountArray = new com.google.gson.JsonArray();
		String foodID = "";
		client = entity;
		if (client.getPersistentData().getBoolean("order_served")) {
			database = ReadFoodDatabaseProcedure.execute(world);
			foodArray = GetClientOrderFoodArrayProcedure.execute(client);
			foodAmountArray = GetClientOrderAmountArrayProcedure.execute(client);
			satisfaction = GetClientOrderSatisfactionProcedure.execute(client);
			loopSize = Math.min(foodArray.size(), foodAmountArray.size());
			for (int _i1 = 0; _i1 < (int) loopSize; _i1++) {
				foodID = foodArray.get((int) index).getAsString();
				amount = foodAmountArray.get((int) index).getAsDouble();
				tier = GetFoodTierByIDProcedure.execute(database, foodID);
				CoinSum = CoinSum + (tier + 1) * amount;
				index = index + 1;
			}
			CoinTotal = Math.round(CoinSum * (satisfaction / 100));
		}
		restaurantID = client.getPersistentData().getDouble("RestaurantID");
		ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), CoinTotal, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
				OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "coins_earned");
		chairDirection = getDirectionFromBlockState((world.getBlockState(BlockPos.containing(client.getPersistentData().getDouble("DestX"), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ")))));
		SetNumberNBTProcedure.execute(world, client.getPersistentData().getDouble("DestX") + chairDirection.getStepX(), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ") + chairDirection.getStepZ(),
				getBlockNBTNumber(world,
						BlockPos.containing(client.getPersistentData().getDouble("DestX") + chairDirection.getStepX(), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ") + chairDirection.getStepZ()), "coins")
						+ CoinTotal,
				"coins");
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}

	private static Property<?> getPropertyByName(BlockState state, String name) {
		for (Property<?> property : state.getProperties()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}
		return null;
	}
}