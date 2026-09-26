package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.init.OmnichefModBlocks;

public class EntityStaysOnTableProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((world.getBlockState(BlockPos.containing(entity.getOnPos().getX(), entity.getOnPos().getY(), entity.getOnPos().getZ()))).getBlock() == OmnichefModBlocks.SERVICE_TABLE.get()) {
			if (world.dayTime() % 200 <= 50) {
				entity.push(0.1, 0, 0.1);
			} else if (world.dayTime() % 200 <= 100) {
				entity.push((-0.1), 0, (-0.1));
			} else if (world.dayTime() % 200 <= 150) {
				entity.push((-0.1), 0, 0.1);
			} else {
				entity.push(0.1, 0, (-0.1));
			}
		}
	}
}