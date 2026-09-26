package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class CanDeleteRestaurantProcedure {
	public static boolean execute(LevelAccessor world, Entity entity, double RestaurantID) {
		if (entity == null)
			return false;
		double restaurantID = 0;
		double restaurantIndex = 0;
		double tableIndex = 0;
		Entity owner = null;
		com.google.gson.JsonArray tableList = new com.google.gson.JsonArray();
		String tableX = "";
		String tableY = "";
		String tableZ = "";
		String tableString = "";
		boolean tableReady = false;
		restaurantID = RestaurantID;
		owner = entity;
		if (restaurantID <= 0) {
			if (owner instanceof Player _player2 && !_player2.level().isClientSide())
				_player2.displayClientMessage(Component.literal("You do not have restaurant!").withStyle(ChatFormatting.RED), false);
			return false;
		}
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, restaurantID);
		if (restaurantIndex < 0) {
			if (owner instanceof Player _player5 && !_player5.level().isClientSide())
				_player5.displayClientMessage(Component.literal("Restaurant does not exist!").withStyle(ChatFormatting.RED), false);
			return false;
		}
		if ((GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner"))
				.equals(owner.getStringUUID())) {
			if (!GetRestaurantLogicParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
				if (GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path,
						"active_groups") == 0) {
					tableList = GetRestaurantArrayParameterProcedure.execute(restaurantIndex, "restaurants", "tables", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
							OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path);
					tableIndex = 0;
					for (int _i1 = 0; _i1 < (int) tableList.size(); _i1++) {
						tableString = tableList.get((int) tableIndex).getAsString();
						tableX = GetPartFromStringProcedure.execute(0, tableString);
						tableY = GetPartFromStringProcedure.execute(1, tableString);
						tableZ = GetPartFromStringProcedure.execute(2, tableString);
						if (getBlockNBTNumber(world, BlockPos.containing(new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(tableX), new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(tableY), new Object() {
							double convert(String s) {
								try {
									return Double.parseDouble(s.trim());
								} catch (Exception e) {
								}
								return 0;
							}
						}.convert(tableZ)), "coins") > 0) {
							if (owner instanceof Player _player12 && !_player12.level().isClientSide())
								_player12.displayClientMessage(Component.literal("Collect all coins from your Service Tables before deleting the restaurant.").withStyle(ChatFormatting.RED), false);
							return false;
						}
						tableIndex = tableIndex + 1;
					}
					return true;
				} else {
					if (owner instanceof Player _player15 && !_player15.level().isClientSide())
						_player15.displayClientMessage(Component.literal("You cannot delete the restaurant while customers are still active.").withStyle(ChatFormatting.RED), false);
				}
			} else {
				if (owner instanceof Player _player18 && !_player18.level().isClientSide())
					_player18.displayClientMessage(Component.literal("Close your restaurant before deleting it.").withStyle(ChatFormatting.RED), false);
			}
		} else {
			if (owner instanceof Player _player21 && !_player21.level().isClientSide())
				_player21.displayClientMessage(Component.literal("Only the restaurant owner can delete the restaurant.").withStyle(ChatFormatting.RED), false);
		}
		return false;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}