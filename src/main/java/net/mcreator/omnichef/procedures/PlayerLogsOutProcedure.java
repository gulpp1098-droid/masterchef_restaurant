package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

@EventBusSubscriber
public class PlayerLogsOutProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		execute(event);
	}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Event event) {
		if (event instanceof PlayerEvent.PlayerLoggedOutEvent logoutEvent && logoutEvent.getEntity() instanceof ServerPlayer player && player.getServer() != null) {
			double restaurantId = entity.getData(net.mcreator.omnichef.network.OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID;
			String previewUuid = entity.getData(net.mcreator.omnichef.network.OmnichefModVariables.PLAYER_VARIABLES).PreviewUUID;
			for (net.minecraft.server.level.ServerLevel level : player.getServer().getAllLevels()) {
				java.util.List<net.minecraft.world.entity.Entity> entities = new java.util.ArrayList<>();
				level.getAllEntities().forEach(entities::add);
				for (net.minecraft.world.entity.Entity marker : entities) {
					boolean isAreaMarker = marker instanceof net.mcreator.omnichef.entity.LocationAreaEntity || marker instanceof net.mcreator.omnichef.entity.LocationEdgeEntity || marker instanceof net.mcreator.omnichef.entity.LocationEdgeDownEntity
							|| marker instanceof net.mcreator.omnichef.entity.LocationEdgeLeftEntity || marker instanceof net.mcreator.omnichef.entity.LocationEdgeRightEntity;
					boolean belongsToRestaurant = restaurantId >= 0 && marker.getPersistentData().getDouble("RestaurantID") == restaurantId;
					boolean isPlayerPreview = previewUuid != null && !previewUuid.isEmpty() && marker.getStringUUID().equals(previewUuid);
					if (isAreaMarker && (belongsToRestaurant || isPlayerPreview)) {
						marker.discard();
					}
				}
			}
		}
	}
}