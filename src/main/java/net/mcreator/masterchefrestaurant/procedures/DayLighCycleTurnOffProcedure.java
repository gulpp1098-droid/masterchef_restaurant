package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.GameRules;

import javax.annotation.Nullable;

@EventBusSubscriber
public class DayLighCycleTurnOffProcedure {
	@SubscribeEvent
	public static void onWorldLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
		execute(event, event.getLevel());
	}

	public static void execute(LevelAccessor world) {
		execute(null, world);
	}

	private static void execute(@Nullable Event event, LevelAccessor world) {
		world.getLevelData().getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, world.getServer());
	}
}