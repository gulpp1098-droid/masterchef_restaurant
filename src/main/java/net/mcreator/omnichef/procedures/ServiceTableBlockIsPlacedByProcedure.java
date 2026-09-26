package net.mcreator.omnichef.procedures;

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
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.block.ChairBlock;

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
			if (owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID > -1) {
				setIntegerBlockState(world, x, y, z, "table_type", 0);
				RestaurantID = owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID;
				if (true) {
					if (RestaurantID >= 0) {
						if (!IsInsideRestaurantProcedure.execute(world, x, z, RestaurantID)) {
							if (owner instanceof Player _player4 && !_player4.level().isClientSide())
								_player4.displayClientMessage(Component.literal("This Service Table is outside your restaurant area and will remain inactive.").withStyle(ChatFormatting.YELLOW), true);
						}
						setBlockNBTNumber(world, x, y, z, "RestaurantID", RestaurantID);
						setBlockNBTNumber(world, x, y, z, "TableNumber", owner.getData(OmnichefModVariables.PLAYER_VARIABLES).TableNumber);
						{
							OmnichefModVariables.PlayerVariables _vars = owner.getData(OmnichefModVariables.PLAYER_VARIABLES);
							_vars.TableNumber = owner.getData(OmnichefModVariables.PLAYER_VARIABLES).TableNumber + 1;
							_vars.markSyncDirty();
						}
						ModifyRestaurantArrayParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, owner.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants", "tables",
								OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, x + ":" + y + ":" + z);
						ChairAmount = 0;
						ServiceTableNeighbourBlockChangesProcedure.execute(world, x, y, z);
					}
				}
			} else {
				if (owner instanceof Player _player9 && !_player9.level().isClientSide())
					_player9.displayClientMessage(Component.literal("You need to create a restaurant first.").withStyle(ChatFormatting.RED), true);
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