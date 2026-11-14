package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.network.OmnichefModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class IngredientExpFromHuntingProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
		}
	}

	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		execute(null, world, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		double cropAge = 0;
		double nextAge = 0;
		if (!world.isClientSide()) {
			if (IsUserRestaurantOwnerProcedure.execute(world, sourceentity)) {
				if (entity instanceof Animal) {
					AddIngredientActionExpProcedure.execute(world, 1, sourceentity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
				}
			}
		}
	}
}