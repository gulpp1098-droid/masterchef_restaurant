package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;

public class MarkRestaurantsOpenDirtyProcedure {
	public static void execute(LevelAccessor world) {
		if (!world.isClientSide()) {
			net.mcreator.omnichef.network.OmnichefModVariables.MapVariables.get(world).markSyncDirty();
		}
	}
}