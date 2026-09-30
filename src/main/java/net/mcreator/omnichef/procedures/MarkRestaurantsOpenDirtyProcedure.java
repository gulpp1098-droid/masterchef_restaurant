package net.mcreator.omnichef.procedures;

public class MarkRestaurantsOpenDirtyProcedure {
	public static void execute() {
		net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world).markSyncDirty();
	}
}