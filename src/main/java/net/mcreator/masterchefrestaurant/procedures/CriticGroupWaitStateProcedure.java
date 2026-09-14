package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;

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
		restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
		owner = world instanceof ServerLevel _level1
				? getEntityFromUUID(_level1,
						GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner"))
				: null;
		if (!(client.getPersistentData().getString("food_delivered")).contains("" + 0)) {
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
		ClientBeginLeavingProcedure.execute(world, entity);
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}