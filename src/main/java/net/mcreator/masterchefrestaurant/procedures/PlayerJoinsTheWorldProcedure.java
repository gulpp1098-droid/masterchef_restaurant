package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class PlayerJoinsTheWorldProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (!entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).BookGained) {
			if (entity instanceof ServerPlayer _player) {
				AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("masterchef_restaurant:cooking_time"));
				if (_adv != null) {
					AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
					if (!_ap.isDone()) {
						for (String criteria : _ap.getRemainingCriteria())
							_player.getAdvancements().award(_adv, criteria);
					}
				}
			}
			{
				MasterchefRestaurantModVariables.PlayerVariables _vars = entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
				_vars.BookGained = true;
				_vars.markSyncDirty();
			}
		}
		CreateOverlayDataTransferProcedure.execute(world, entity);
	}
}