package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Display;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModItems;

import java.util.Comparator;

public class ServiceTableRightClickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		double tabelRestaurantID = 0;
		double X = 0;
		double Y = 0;
		double Z = 0;
		if (!world.isClientSide()) {
			tabelRestaurantID = getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID");
			X = x;
			Y = y;
			Z = z;
			if (getBlockNBTNumber(world, BlockPos.containing(X, Y, Z), "coins") > 0) {
				if (tabelRestaurantID == entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID) {
					if (entity instanceof Player _player) {
						ItemStack _setstack = new ItemStack(MasterchefRestaurantModItems.COPPER_COIN.get()).copy();
						_setstack.setCount((int) getBlockNBTNumber(world, BlockPos.containing(X, Y, Z), "coins"));
						ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
					}
					setBlockNBTNumber(world, X, Y, Z, "coins", 0);
					if (!((findEntityInWorldRange(world, Display.TextDisplay.class, (X + 0.5), (Y + 1), (Z + 0.5), 0.5)) == null)) {
						if (!(findEntityInWorldRange(world, Display.TextDisplay.class, (X + 0.5), (Y + 1), (Z + 0.5), 0.5)).level().isClientSide())
							(findEntityInWorldRange(world, Display.TextDisplay.class, (X + 0.5), (Y + 1), (Z + 0.5), 0.5)).discard();
					}
				} else {
					if (entity instanceof Player _player12 && !_player12.level().isClientSide())
						_player12.displayClientMessage(Component.literal("This Service Table belongs to another restaurant.").withStyle(ChatFormatting.RED), true);
				}
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