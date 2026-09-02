package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.ArrayList;

public class ClientCoinPayProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		ArrayList<Object> array = new ArrayList<>();
		Entity client = null;
		String foodDelivered = "";
		String orderedFood = "";
		Direction chairDirection = Direction.NORTH;
		double CurrentReputation = 0;
		double ordered = 0;
		double index = 0;
		double delivered = 0;
		double CoinSum = 0;
		double CoinTotal = 0;
		double restaurantID = 0;
		client = entity;
		foodDelivered = client.getPersistentData().getString("food_delivered");
		orderedFood = client.getPersistentData().getString("food_tiers");
		array = string2ArrayList(orderedFood, ",");
		index = 0;
		String _toSplit5 = foodDelivered;
		String[] _array5 = _toSplit5.split(Pattern.quote(","));
		for (int _iter5 = 0; _iter5 < Math.max(1, _array5.length); _iter5++) {
			String stringiterator = _array5.length == 0 ? _toSplit5 : _array5[_iter5];
			if ((stringiterator).equals("1")) {
				CoinSum = (new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(array.get((int) index) instanceof String _str4 ? _str4 : "") + 1) * 2 + CoinSum;
			}
			index = index + 1;
		}
		CoinTotal = CoinSum;
		restaurantID = client.getPersistentData().getDouble("RestaurantID");
		ModifyRestaurantObjectParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), CoinTotal, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
				MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "daily_stats", "coins_earned");
		chairDirection = getDirectionFromBlockState((world.getBlockState(BlockPos.containing(client.getPersistentData().getDouble("DestX"), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ")))));
		SetNumberNBTProcedure.execute(world, client.getPersistentData().getDouble("DestX") + chairDirection.getStepX(), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ") + chairDirection.getStepZ(),
				getBlockNBTNumber(world,
						BlockPos.containing(client.getPersistentData().getDouble("DestX") + chairDirection.getStepX(), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ") + chairDirection.getStepZ()), "coins")
						+ CoinTotal,
				"coins");
	}

	private static ArrayList<Object> string2ArrayList(String text, String separator) {
		return new ArrayList<>(Arrays.asList(text.split(separator)));
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