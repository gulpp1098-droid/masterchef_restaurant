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
	private static final Map<String, Integer> TIERS = new HashMap<>();

	public static void receive(JsonObject payload) {
		if (payload.has("reset") && payload.get("reset").getAsBoolean()) {
			ROLES.clear();
			TIERS.clear();
		}

		JsonObject roles = payload.getAsJsonObject("roles");
		if (roles == null)
			return;

		for (var entry : roles.entrySet()) {
			String itemId = entry.getKey();
			String role;
			int tier = -1;

			if (entry.getValue().isJsonObject()) {
				JsonObject data = entry.getValue().getAsJsonObject();
				role = data.get("role").getAsString();
				if (data.has("tier"))
					tier = data.get("tier").getAsInt();
			} else {
				role = entry.getValue().getAsString();
			}

			if ("FOOD".equals(role) || "INGREDIENT".equals(role)) {
				ROLES.put(itemId, role);
				if (tier >= 0)
					TIERS.put(itemId, tier);
				else
					TIERS.remove(itemId);
			}
		}
	}

	public static String getRole(String itemId) {
		return ROLES.getOrDefault(itemId, "");
	}

	public static int getTier(String itemId) {
		return TIERS.getOrDefault(itemId, -1);
	}

	@SubscribeEvent
	public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
		ROLES.clear();
		TIERS.clear();
	}
}