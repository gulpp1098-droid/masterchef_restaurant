package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.block.ChairBlock;

import java.io.File;

public class ServiceTableBlockIsPlacedByProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		File Restaurantsfile = new File("");
		com.google.gson.JsonArray restaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject restaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		Entity owner = null;
		double RestaurantID = 0;
		double ChairAmount = 0;
		BlockState ChairBlock = Blocks.AIR.defaultBlockState();
		if (!world.isClientSide()) {
			owner = entity;
			if (owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID > -1) {
				setIntegerBlockState(world, x, y, z, "table_type", 0);
				RestaurantID = owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID;
				if ((owner.level().dimension().location().toString()).equals("minecraft:overworld")) {
					if (RestaurantID >= 0) {
						if (IsInsideRestaurantProcedure.execute(world, x, z, RestaurantID)) {
							if (owner instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("Table is inside restaurant!"), false);
						} else {
							if (owner instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("Table is NOT inside restaurant!"), false);
						}
						setBlockNBTNumber(world, x, y, z, "RestaurantID", RestaurantID);
						setBlockNBTNumber(world, x, y, z, "TableNumber", owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).TableNumber);
						{
							MasterchefRestaurantModVariables.PlayerVariables _vars = owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
							_vars.TableNumber = owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).TableNumber + 1;
							_vars.markSyncDirty();
						}
						ModifyRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants", "tables",
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, x + ":" + y + ":" + z);
						ChairAmount = 0;
						ServiceTableNeighbourBlockChangesProcedure.execute(world, x, y, z);
					}
				}
			} else {
				if (owner instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("You do not have Restaurant yet!"), false);
			}
		}
	}

	private static void setIntegerBlockState(LevelAccessor world, double x, double y, double z, String property, int value) {
		BlockPos pos = BlockPos.containing(x, y, z);
		BlockState state = world.getBlockState(pos);
		if (state.getBlock().getStateDefinition().getProperty(property) instanceof IntegerProperty integerProperty && integerProperty.getPossibleValues().contains(value)) {
			world.setBlock(pos, state.setValue(integerProperty, value), 3);
		}
	}

	private static void setBlockNBTNumber(LevelAccessor world, double x, double y, double z, String tag, double value) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			if (blockEntity != null) {
				blockEntity.getPersistentData().putDouble(tag, value);
			}
			if (world instanceof Level level) {
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	}
}