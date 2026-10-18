package net.mcreator.omnichef.procedures;

public class FillMissingJsonPropertiesProcedure {
	public static boolean execute(com.google.gson.JsonObject defaultsObject, com.google.gson.JsonObject targetObject) {
		if (defaultsObject == null || targetObject == null)
			return false;
		com.google.gson.JsonObject data = new com.google.gson.JsonObject();
		com.google.gson.JsonObject defaults = new com.google.gson.JsonObject();
		boolean changed = false;
		data = targetObject;
		defaults = defaultsObject;
		class JsonDefaultsHelper {
			boolean sameType(com.google.gson.JsonElement a, com.google.gson.JsonElement b) {
				boolean matches = false;
				if (a.isJsonObject() && b.isJsonObject()) {
					matches = true;
				} else if (a.isJsonArray() && b.isJsonArray()) {
					matches = true;
				} else if (a.isJsonPrimitive() && b.isJsonPrimitive()) {
					var x = a.getAsJsonPrimitive();
					var y = b.getAsJsonPrimitive();
					matches = (x.isNumber() && y.isNumber()) || (x.isBoolean() && y.isBoolean()) || (x.isString() && y.isString());
				}
				return matches;
			}

			boolean fill(com.google.gson.JsonObject target, com.google.gson.JsonObject template, String path) {
				boolean modified = false;
				for (var entry : template.entrySet()) {
					String key = entry.getKey();
					String fieldPath = path + "." + key;
					var expected = entry.getValue();
					if (!target.has(key)) {
						target.add(key, expected.deepCopy());
						modified = true;
					} else {
						var current = target.get(key);
						if (!sameType(current, expected)) {
							throw new IllegalStateException("Omnichef: invalid JSON type at " + fieldPath);
						}
						if (expected.isJsonObject()) {
							boolean nestedChanged = fill(current.getAsJsonObject(), expected.getAsJsonObject(), fieldPath);
							if (nestedChanged) {
								modified = true;
							}
						}
					}
				}
				return modified;
			}
		}
		changed = new JsonDefaultsHelper().fill(data, defaults, "$");
		return changed;
	}
}