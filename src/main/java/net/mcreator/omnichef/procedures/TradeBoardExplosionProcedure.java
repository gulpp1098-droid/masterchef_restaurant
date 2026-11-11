package net.mcreator.omnichef.procedures;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

@EventBusSubscriber
public class TradeBoardExplosionProcedure {
	@SubscribeEvent
	public static void onExplosionDetonate(net.neoforged.neoforge.event.level.ExplosionEvent.Detonate event) {
		if (event != null && event.getLevel() != null) {
			net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) event.getLevel();
			net.minecraft.world.phys.Vec3 pos = event.getExplosion().center();
			net.minecraft.world.entity.Entity exploder = event.getExplosion().getDirectSourceEntity();
			if (exploder == null) {
				exploder = event.getExplosion().getIndirectSourceEntity();
			}
			net.minecraft.world.level.block.state.BlockState sourceBlock = net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
			if (exploder instanceof net.minecraft.world.entity.item.PrimedTnt) {
				sourceBlock = net.minecraft.world.level.block.Blocks.TNT.defaultBlockState();
			} else if (exploder instanceof net.minecraft.world.entity.vehicle.MinecartTNT) {
				sourceBlock = net.minecraft.world.level.block.Blocks.TNT.defaultBlockState();
			} else {
				net.minecraft.core.BlockPos blockPos = net.minecraft.core.BlockPos.containing(pos);
				net.minecraft.world.level.block.state.BlockState stateAtPos = level.getBlockState(blockPos);
				if (!stateAtPos.isAir()) {
					sourceBlock = stateAtPos;
				}
			}
			execute(event);
		}
	}

}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Event event) {
		double part = 0;
		double sideX = 0;
		double sideZ = 0;
		double mainX = 0;
		double mainZ = 0;
		double mainY = 0;
		if (event instanceof net.neoforged.neoforge.event.level.ExplosionEvent.Detonate tbExplosion && !tbExplosion.getLevel().isClientSide()) {
			var tbInteraction = tbExplosion.getExplosion().getBlockInteraction();
			if (tbInteraction == net.minecraft.world.level.Explosion.BlockInteraction.DESTROY || tbInteraction == net.minecraft.world.level.Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
				for (var tbPos : java.util.List.copyOf(tbExplosion.getAffectedBlocks())) {
					var tbState = tbExplosion.getLevel().getBlockState(tbPos);
					if (tbState.is(net.mcreator.omnichef.init.OmnichefModBlocks.TRADE_BOARD.get())) {
						net.mcreator.omnichef.procedures.TradeBoardDestroyedProcedure.execute(tbExplosion.getLevel(), tbPos.getX(), tbPos.getY(), tbPos.getZ(), tbState);
					}
				}
			}
		}
	}
}