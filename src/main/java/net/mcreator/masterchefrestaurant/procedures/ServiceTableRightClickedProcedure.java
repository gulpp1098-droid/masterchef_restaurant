package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Display;
import net.minecraft.core.BlockPos;

import java.util.Comparator;

public class ServiceTableRightClickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (getBlockNBTNumber(world, BlockPos.containing(x, y, z), "coins") > 0) {
			if (entity instanceof Player _player) {
				ItemStack _setstack = new ItemStack(Items.EMERALD).copy();
				_setstack.setCount((int) getBlockNBTNumber(world, BlockPos.containing(x, y, z), "coins"));
				ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
			}
			setBlockNBTNumber(world, x, y, z, "coins", 0);
			if (!((findEntityInWorldRange(world, Display.TextDisplay.class, (x + 0.5), (y + 1), (z + 0.5), 0.5)) == null)) {
				if (!(findEntityInWorldRange(world, Display.TextDisplay.class, (x + 0.5), (y + 1), (z + 0.5), 0.5)).level().isClientSide())
					(findEntityInWorldRange(world, Display.TextDisplay.class, (x + 0.5), (y + 1), (z + 0.5), 0.5)).discard();
			}
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}

	private static void setBlockNBTNumber(LevelAccessor world, double x, double y, double z, String tag, double value) {
		if (!world.isClientSide()) {
			BlockPos pos = BlockPos.containing(x, y, z);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			BlockState blockState = world.getBlockState(pos);
			if (blockEntity != null) {
				blockEntity.getPersistentData().putDouble(tag, value);
			}
			if (world instanceof Level level) {
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	}

	private static Entity findEntityInWorldRange(LevelAccessor world, Class<? extends Entity> clazz, double x, double y, double z, double range) {
		return (Entity) world.getEntitiesOfClass(clazz, AABB.ofSize(new Vec3(x, y, z), range, range, range), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z))).findFirst().orElse(null);
	}
}