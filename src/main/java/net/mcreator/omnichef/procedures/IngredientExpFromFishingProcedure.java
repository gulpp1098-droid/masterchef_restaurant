package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class IngredientExpFromFishingProcedure {
	@SubscribeEvent
	public static void onPlayerFishItem(ItemFishedEvent event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
				AddIngredientActionExpProcedure.execute(world, 1, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
			}
		}
	}
}