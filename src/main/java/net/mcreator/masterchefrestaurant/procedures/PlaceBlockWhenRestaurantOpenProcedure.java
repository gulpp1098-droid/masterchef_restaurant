package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class PlaceBlockWhenRestaurantOpenProcedure {
	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		execute(event, event.getLevel(), event.getState(), event.getEntity());
	}

	public static void execute(LevelAccessor world, BlockState blockstate, Entity entity) {
		execute(null, world, blockstate, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		double restaurantID = 0;
		if (blockstate.getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get() || blockstate.getBlock() == MasterchefRestaurantModBlocks.RECEPTION.get()) {
			if (entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID > -1) {
				if (GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants",
						MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name, MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
					if (event instanceof ICancellableEvent _cancellable) {
						_cancellable.setCanceled(true);
					}
				}
			} else {
				if (event instanceof ICancellableEvent _cancellable) {
					_cancellable.setCanceled(true);
				}
			}
		}
	}
}