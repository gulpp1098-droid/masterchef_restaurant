package net.mcreator.omnichef;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
    private static final Map<String, String> DISCOVERIES = new HashMap<>();
    private static final Map<String, Boolean> SERVABLE = new HashMap<>();
    private static final Map<String, List<String>> VARIANTS = new HashMap<>();

    public static void receive(JsonObject payload) {
        if (payload == null) return;
        if (payload.has("reset") && payload.get("reset").getAsBoolean()) clear();

        JsonObject roles = payload.getAsJsonObject("roles");
        if (roles == null) return;

        for (var entry : roles.entrySet()) {
            String itemId = entry.getKey();
            String role;
            int tier = -1;
            String discoveryId = itemId;
            boolean canServe = false;
            JsonObject data = null;

            if (entry.getValue().isJsonObject()) {
                data = entry.getValue().getAsJsonObject();
                role = data.get("role").getAsString();
                if (data.has("tier")) tier = data.get("tier").getAsInt();
                if (data.has("discovery_id")) discoveryId = data.get("discovery_id").getAsString();
                if (data.has("can_serve")) canServe = data.get("can_serve").getAsBoolean();
            } else {
                role = entry.getValue().getAsString();
            }

            if (!"FOOD".equals(role) && !"INGREDIENT".equals(role)) continue;
            ROLES.put(itemId, role);
            if (tier >= 0) TIERS.put(itemId, tier);
            else TIERS.remove(itemId);
            DISCOVERIES.put(itemId, discoveryId);
            SERVABLE.put(itemId, canServe);

            // Only the representative carries the full group list.
            if (data != null && itemId.equals(discoveryId) && data.has("variants")) {
                var members = new LinkedHashSet<String>();
                members.add(discoveryId);
                for (var member : data.getAsJsonArray("variants")) members.add(member.getAsString());
                VARIANTS.put(discoveryId, List.copyOf(members));
            }
        }
    }

    public static String getRole(String itemId) {
        return ROLES.getOrDefault(itemId, "");
    }

    public static int getTier(String itemId) {
        return TIERS.getOrDefault(itemId, -1);
    }

    public static String getDiscoveryID(String itemId) {
        return DISCOVERIES.getOrDefault(itemId, itemId);
    }

    public static boolean canServe(String itemId) {
        return SERVABLE.getOrDefault(itemId, false);
    }

    public static List<String> getVariants(String itemId) {
        if (!ROLES.containsKey(itemId)) return List.of();
        String discoveryId = getDiscoveryID(itemId);
        List<String> members = VARIANTS.get(discoveryId);
        if (members != null) return members;
        // An alias can arrive before the packet containing its representative.
        return itemId.equals(discoveryId) ? List.of(itemId) : List.of();
    }

    public static int getVariantCount(String itemId) {
        return getVariants(itemId).size();
    }

    private static void clear() {
        ROLES.clear();
        TIERS.clear();
        DISCOVERIES.clear();
        SERVABLE.clear();
        VARIANTS.clear();
    }

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        clear();
    }
}
