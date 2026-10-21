package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class TradeBoardInputChangedProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		boolean tradeSuccessful = false;
		if (!world.isClientSide()) {
			tradeSuccessful = TryExecuteTradeBoardOfferProcedure.execute(world, entity);
		}
	}
}