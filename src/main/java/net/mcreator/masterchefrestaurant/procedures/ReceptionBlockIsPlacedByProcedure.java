package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;
import net.mcreator.masterchefrestaurant.block.ChairBlock;

import java.io.File;

public class ReceptionBlockIsPlacedByProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		File Restaurantsfile = new File("");
		com.google.gson.JsonArray restaurantsArray = new com.google.gson.JsonArray();
		com.google.gson.JsonObject restaurantsObject = new com.google.gson.JsonObject();
		com.google.gson.JsonObject restaurant = new com.google.gson.JsonObject();
		Entity owner = null;
		BlockState ChairBlock = Blocks.AIR.defaultBlockState();
		double RestaurantID = 0;
		double ChairAmount = 0;
		double restaurantIndex = 0;
		owner = entity;
		if ((entity.level().dimension().location().toString()).equals("minecraft:overworld")) {
			if (!world.isClientSide()) {
				if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID > -1) {
					RestaurantID = owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID;
					if (RestaurantID >= 0) {
						if (IsInsideRestaurantProcedure.execute(world, x, z, RestaurantID)) {
							restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
							if ((GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
									MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception")).equals("")) {
								setDirectionBlockState(world, x, y, z, (owner.getDirection()));
								if (MasterchefRestaurantModBlocks.RUG_QUEUE.get().defaultBlockState().canSurvive(world, BlockPos.containing(x + (owner.getDirection()).getStepX(), y, z + (owner.getDirection()).getStepZ()))
										&& world.isEmptyBlock(BlockPos.containing(x + (owner.getDirection()).getStepX(), y, z + (owner.getDirection()).getStepZ()))) {
									if (owner instanceof Player _player && !_player.level().isClientSide())
										_player.displayClientMessage(Component.literal("Reception is inside restaurant!"), false);
									setBlockNBTNumber(world, x, y, z, "RestaurantID", RestaurantID);
									ModifyRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
											MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception", x + ":" + y + ":" + z);
									RugIsPlacedwithReceptionProcedure.execute(world, x, y, z, entity);
								} else {
									if (owner instanceof Player _player && !_player.level().isClientSide())
										_player.displayClientMessage(Component.literal("No valid position for Reception Queue!"), false);
									{
										BlockPos _pos = BlockPos.containing(x, y, z);
										Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
										world.destroyBlock(_pos, false);
									}
								}
							} else {
								if (owner instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("You already have Reception in your restaurant!"), false);
								{
									BlockPos _pos = BlockPos.containing(x, y, z);
									Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
									world.destroyBlock(_pos, false);
								}
							}
						} else {
							if (owner instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("Reception is NOT inside restaurant! Replace it!"), false);
							{
								BlockPos _pos = BlockPos.containing(x, y, z);
								Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
								world.destroyBlock(_pos, false);
							}
						}
					}
				} else {
					if (owner instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("You do not have Restaurant yet!"), false);
				}
			}
		} else {
			if (owner instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("Reception is NOT in the overworld!"), false);
			{
				BlockPos _pos = BlockPos.containing(x, y, z);
				Block.dropResources(world.getBlockState(_pos), world, BlockPos.containing(x, y, z), null);
				world.destroyBlock(_pos, false);
			}
		}
	}

	private static void setDirectionBlockState(LevelAccessor world, double x, double y, double z, Direction value) {
		BlockPos pos = BlockPos.containing(x, y, z);
		BlockState state = world.getBlockState(pos);
		Property<?> property = state.getBlock().getStateDefinition().getProperty("facing");
		if (property instanceof DirectionProperty directionProperty && directionProperty.getPossibleValues().contains(value)) {
			world.setBlock(pos, state.setValue(directionProperty, value), 3);
		} else {
			property = state.getBlock().getStateDefinition().getProperty("axis");
			if (property instanceof EnumProperty enumProperty && enumProperty.getPossibleValues().contains(value.getAxis())) {
				world.setBlock(pos, state.setValue(enumProperty, value.getAxis()), 3);
			}
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