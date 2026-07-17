package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

@EventBusSubscriber
public class OnWorldTickUpdateProcedure {
	@SubscribeEvent
	public static void onWorldTick(LevelTickEvent.Post event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		if ((((Level) world).dimension().location().toString()).equals("minecraft:overworld")) {
			if (!world.isClientSide()) {
				ChangingDayAndNightLengthProcedure.execute(world);
				ClosingRestaurantsProcedure.execute(world);
				NewMenuGenerateTriggerProcedure.execute(world);
				DeleteClientsDatabaseFileProcedure.execute(world);
				SpawnClientsProcedure.execute(world);
			}
		}
	}
}