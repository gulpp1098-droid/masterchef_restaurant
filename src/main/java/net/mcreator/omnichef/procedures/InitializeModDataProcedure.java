package net.mcreator.omnichef.procedures;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import net.mcreator.omnichef.network.OmnichefModVariables;

import javax.annotation.Nullable;

@EventBusSubscriber
public class InitializeModDataProcedure {
	@SubscribeEvent
	public static void onWorldLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		if (!world.isClientSide()) {
			if ((world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD)) == Level.OVERWORLD) {
				OmnichefModVariables.MapVariables.get(world).ModDataReady = false;
				OmnichefModVariables.MapVariables.get(world).markSyncDirty();
				if (BackupModDataProcedure.execute(world)) {
					CreatingFoodListJsonProcedure.execute(world);
					CreatingClientsNameDatabaseJsonFileProcedure.execute(world);
					CreatingClientsDatabaseJsonFileProcedure.execute(world);
					CreatingRestaurantJsonFileProcedure.execute(world);
					CreatingRestaurantFoodJsonFileProcedure.execute(world);
					OmnichefModVariables.MapVariables.get(world).ModDataReady = true;
					OmnichefModVariables.MapVariables.get(world).markSyncDirty();
				}
			}
		}
	}
}