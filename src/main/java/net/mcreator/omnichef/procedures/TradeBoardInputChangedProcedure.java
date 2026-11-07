package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.omnichef.OmnichefMod;

public class TradeBoardInputChangedProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			OmnichefMod.queueServerWork(1, () -> {
				TryExecuteTradeBoardOfferProcedure.execute(world, entity);
			});
		}
	}
}