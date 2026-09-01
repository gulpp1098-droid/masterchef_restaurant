package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class LateSleepProcedure {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != InteractionHand.MAIN_HAND)
			return;
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		com.google.gson.JsonArray restaurants = new com.google.gson.JsonArray();
		double index = 0;
		if (!world.isClientSide()) {
			if ((world.getBlockState(BlockPos.containing(x, y, z))).is(BlockTags.create(ResourceLocation.parse("minecraft:beds")))) {
				if (!(world instanceof Level _lvl3 && _lvl3.isDay())) {
					restaurants = GetRestaurantsListArrayProcedure.execute(world);
					index = 0;
					for (int _i1 = 0; _i1 < (int) restaurants.size(); _i1++) {
						if (GetRestaurantLogicParameterProcedure.execute(index, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
								MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
							if (event instanceof ICancellableEvent _cancellable) {
								_cancellable.setCanceled(true);
							}
							if (entity instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("You can NOT sleep yet due to restaurants being opened!"), false);
							break;
						} else {
							index = index + 1;
						}
					}
				}
			}
		}
	}
}