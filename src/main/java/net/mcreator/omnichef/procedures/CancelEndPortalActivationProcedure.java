package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.init.OmnichefModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class CancelEndPortalActivationProcedure {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != InteractionHand.MAIN_HAND)
			return;
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getLevel().getBlockState(event.getPos()), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		execute(null, world, x, y, z, blockstate, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		boolean found = false;
		double sx = 0;
		double sy = 0;
		double sz = 0;
		if (blockstate.getBlock() == Blocks.END_PORTAL_FRAME) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.ENDER_EYE
					|| (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.ENDER_EYE) {
				sx = -3;
				found = false;
				for (int _i1 = 0; _i1 < 7; _i1++) {
					sz = -3;
					for (int _i2 = 0; _i2 < 7; _i2++) {
						if ((world.getBlockState(BlockPos.containing(x + sx, y, z + sz))).getBlock() == OmnichefModBlocks.CHAIR.get() || (world.getBlockState(BlockPos.containing(x + sx, y, z + sz))).getBlock() == OmnichefModBlocks.RECEPTION.get()
								|| (world.getBlockState(BlockPos.containing(x + sx, y, z + sz))).getBlock() == OmnichefModBlocks.RUG_QUEUE.get()
								|| (world.getBlockState(BlockPos.containing(x + sx, y, z + sz))).getBlock() == OmnichefModBlocks.SERVICE_TABLE.get()) {
							if (IsInsideAnyRestaurantProcedure.execute(world, x + sx, z + sz)) {
								if (event instanceof ICancellableEvent _cancellable) {
									_cancellable.setCanceled(true);
								}
								found = true;
								if (entity instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal("Really? Do not do that.."), true);
								break;
							}
						}
						sz = sz + 1;
					}
					if (found) {
						break;
					}
					sx = sx + 1;
				}
			}
		}
	}
}