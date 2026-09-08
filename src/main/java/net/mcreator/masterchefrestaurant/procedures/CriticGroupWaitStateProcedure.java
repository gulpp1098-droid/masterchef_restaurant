package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.network.LevelUpAnimationMessage;

import java.util.UUID;

public class CriticGroupWaitStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		boolean AllReady = false;
		Entity client = null;
		Entity owner = null;
		Direction chairDirection = Direction.NORTH;
		double restaurantIndex = 0;
		client = entity;
		owner = world instanceof ServerLevel _level0
				? getEntityFromUUID(_level0,
						GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner"))
				: null;
		if (!(client.getPersistentData().getString("food_delivered")).contains("" + 0)) {
			restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
			if (owner instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("You have satisfied Critic! Your restaurant have got a new star!"), false);
			if (owner instanceof ServerPlayer player5)
				PacketDistributor.sendToPlayer(player5, new LevelUpAnimationMessage(""));
			ModifyRestaurantNumberParameterProcedure.execute(
					GetRestaurantNumberParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
							MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level") + 1,
					restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "level");
		} else {
			if (owner instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("Critic is leaving unsatisfied. Try better next time!"), false);
		}
		client.getPersistentData().putString("state", "leave");
		client.getPersistentData().putDouble("despawn_time", (world.dayTime() + 250));
		client.stopRiding();
		chairDirection = getDirectionFromBlockState((world.getBlockState(BlockPos.containing(client.getPersistentData().getDouble("DestX"), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ")))));
		SetLogicNBTProcedure.execute(world, client.getPersistentData().getDouble("DestX") + chairDirection.getStepX(), client.getPersistentData().getDouble("DestY"), client.getPersistentData().getDouble("DestZ") + chairDirection.getStepZ(), false,
				"occupied");
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
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