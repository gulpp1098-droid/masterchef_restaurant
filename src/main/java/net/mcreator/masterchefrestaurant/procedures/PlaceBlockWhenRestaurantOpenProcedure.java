package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class PlaceBlockWhenRestaurantOpenProcedure {
	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getZ(), event.getState(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double z, BlockState blockstate, Entity entity) {
		execute(null, world, x, z, blockstate, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double z, BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		double restaurantID = 0;
		if (blockstate.getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get() || blockstate.getBlock() == MasterchefRestaurantModBlocks.RECEPTION.get() || blockstate.getBlock() == MasterchefRestaurantModBlocks.CHAIR.get()
				|| blockstate.getBlock() == MasterchefRestaurantModBlocks.SERVICE_TABLE.get()) {
			if (Level.OVERWORLD == (world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD))) {
				if (IsInsideAnyRestaurantProcedure.execute(world, x, z)) {
					if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID > 0) {
						if (!IsInsideRestaurantProcedure.execute(world, x, z, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID)) {
							if (entity instanceof Player _player13 && !_player13.level().isClientSide())
								_player13.displayClientMessage(Component.literal("You cannot place restaurant blocks inside another restaurant.").withStyle(ChatFormatting.RED), true);
							if (event instanceof ICancellableEvent _cancellable) {
								_cancellable.setCanceled(true);
							}
						}
					} else {
						if (entity instanceof Player _player16 && !_player16.level().isClientSide())
							_player16.displayClientMessage(Component.literal("You do not own a restaurant yet.").withStyle(ChatFormatting.RED), true);
						if (event instanceof ICancellableEvent _cancellable) {
							_cancellable.setCanceled(true);
						}
					}
				}
				if (blockstate.getBlock() == MasterchefRestaurantModBlocks.RECEPTION.get() || blockstate.getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get()) {
					if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID > 0) {
						if (GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants",
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
							if (entity instanceof Player _player23 && !_player23.level().isClientSide())
								_player23.displayClientMessage(Component.literal("You cannot modify your reception while the restaurant is open.").withStyle(ChatFormatting.RED), true);
							if (event instanceof ICancellableEvent _cancellable) {
								_cancellable.setCanceled(true);
							}
						}
					} else {
						if (entity instanceof Player _player26 && !_player26.level().isClientSide())
							_player26.displayClientMessage(Component.literal("You do not own a restaurant yet.").withStyle(ChatFormatting.RED), true);
						if (event instanceof ICancellableEvent _cancellable) {
							_cancellable.setCanceled(true);
						}
					}
				}
			} else {
				if (entity instanceof Player _player29 && !_player29.level().isClientSide())
					_player29.displayClientMessage(Component.literal("Restaurant blocks can only be placed in the Overworld.").withStyle(ChatFormatting.RED), true);
				if (event instanceof ICancellableEvent _cancellable) {
					_cancellable.setCanceled(true);
				}
			}
		}
	}
}