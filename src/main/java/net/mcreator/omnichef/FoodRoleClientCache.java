package net.mcreator.omnichef;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = "omnichef", value = Dist.CLIENT)
public class FoodRoleClientCache {
	private static final Map<String, String> ROLES = new HashMap<>();

	public static void receive(JsonObject payload) {
		if (payload.has("reset") && payload.get("reset").getAsBoolean()) {
			ROLES.clear();
		}

		JsonObject roles = payload.getAsJsonObject("roles");

		for (var entry : roles.entrySet()) {
			String role = entry.getValue().getAsString();

			if ("FOOD".equals(role) || "INGREDIENT".equals(role)) {
				ROLES.put(entry.getKey(), role);
			}
		}
	}

	public static String getRole(String itemId) {
		return ROLES.getOrDefault(itemId, "");
	}

	@SubscribeEvent
	public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
		ROLES.clear();
	}
}