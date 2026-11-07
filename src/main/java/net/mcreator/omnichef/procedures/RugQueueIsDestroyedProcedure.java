package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class RugQueueIsDestroyedProcedure {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getPlayer());
	}

	public static boolean execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		return execute(null, world, x, y, z, entity);
	}

	private static boolean execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return false;
		boolean CanBreak = false;
		String receptionString = "";
		double NBT = 0;
		double X = 0;
		double Y = 0;
		double Z = 0;
		double ID = 0;
		double receX = 0;
		double receY = 0;
		double receZ = 0;
		double newNBT = 0;
		if (OmnichefModBlocks.RUG_QUEUE.get() == (world.getBlockState(BlockPos.containing(x, y, z))).getBlock()) {
			ID = getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID");
			if (ID != 0) {
				if (CanBypassRestaurantProtectionProcedure.execute(entity)) {
					CleanupRestaurantQueueFromRugProcedure.execute(world, x, y, z);
					return true;
				}
				if (getBlockNBTNumber(world, BlockPos.containing(x, y, z), "queue") > 0) {
					if (entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID == ID
							&& !GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID), "restaurants",
									OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name, OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
						CleanupRestaurantQueueFromRugProcedure.execute(world, x, y, z);
					} else {
						if (event instanceof ICancellableEvent _cancellable) {
							_cancellable.setCanceled(true);
						}
					}
				} else {
					if (event instanceof ICancellableEvent _cancellable) {
						_cancellable.setCanceled(true);
					}
				}
			}
		}
		return true;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}