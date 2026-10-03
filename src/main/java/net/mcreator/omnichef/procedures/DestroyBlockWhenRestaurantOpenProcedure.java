package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModBlocks;
import net.mcreator.omnichef.entity.ChairMobEntity;

import javax.annotation.Nullable;

import java.util.Comparator;

@EventBusSubscriber
public class DestroyBlockWhenRestaurantOpenProcedure {
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getState(), event.getPlayer());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		execute(null, world, x, y, z, blockstate, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		double restaurantID = 0;
		Entity entityChair = null;
		Direction direction = Direction.NORTH;
		if (blockstate.getBlock() == OmnichefModBlocks.SERVICE_TABLE.get() || blockstate.getBlock() == OmnichefModBlocks.RUG_QUEUE.get() || blockstate.getBlock() == OmnichefModBlocks.RECEPTION.get()
				|| blockstate.getBlock() == OmnichefModBlocks.CHAIR.get()) {
			restaurantID = getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID");
			if (restaurantID == entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID) {
				if (GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
					if (entity instanceof Player _player11 && !_player11.level().isClientSide())
						_player11.displayClientMessage(Component.literal("You cannot modify restaurant blocks while your restaurant is open.").withStyle(ChatFormatting.RED), true);
					if (event instanceof ICancellableEvent _cancellable) {
						_cancellable.setCanceled(true);
					}
				}
				if (blockstate.getBlock() == OmnichefModBlocks.SERVICE_TABLE.get()) {
					if (getBlockNBTLogic(world, BlockPos.containing(x, y, z), "active") || getBlockNBTLogic(world, BlockPos.containing(x, y, z), "occupied")) {
						if (entity instanceof Player _player18 && !_player18.level().isClientSide())
							_player18.displayClientMessage(Component.literal("You cannot modify restaurant blocks while your restaurant is open.").withStyle(ChatFormatting.RED), true);
						if (event instanceof ICancellableEvent _cancellable) {
							_cancellable.setCanceled(true);
						}
					}
				} else if (blockstate.getBlock() == OmnichefModBlocks.CHAIR.get()) {
					direction = getDirectionFromBlockState(blockstate);
					if (getBlockNBTLogic(world, BlockPos.containing(x + direction.getStepX(), y, z + direction.getStepZ()), "active")
							|| getBlockNBTLogic(world, BlockPos.containing(x + direction.getStepX(), y, z + direction.getStepZ()), "occupied")) {
						if (entity instanceof Player _player31 && !_player31.level().isClientSide())
							_player31.displayClientMessage(Component.literal("You cannot modify restaurant blocks while your restaurant is open.").withStyle(ChatFormatting.RED), true);
						if (event instanceof ICancellableEvent _cancellable) {
							_cancellable.setCanceled(true);
						}
					}
					{
						final Vec3 _center = new Vec3((x + 0.5), y, (z + 0.5));
						for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(1 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
							if (entityiterator instanceof ChairMobEntity) {
								entityChair = entityiterator;
								break;
							}
						}
					}
					if (entityChair != null && entityChair.isVehicle()) {
						if (entity instanceof Player _player37 && !_player37.level().isClientSide())
							_player37.displayClientMessage(Component.literal("You cannot break chair while someone is using it.").withStyle(ChatFormatting.RED), true);
						if (event instanceof ICancellableEvent _cancellable) {
							_cancellable.setCanceled(true);
						}
					}
				}
			} else if (restaurantID == 0) {
			} else {
				if (entity instanceof Player _player41 && !_player41.level().isClientSide())
					_player41.displayClientMessage(Component.literal("This block does not belong to your restaurant.").withStyle(ChatFormatting.RED), true);
				if (event instanceof ICancellableEvent _cancellable) {
					_cancellable.setCanceled(true);
				}
			}
		} else if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == OmnichefModBlocks.RUG_QUEUE.get()) {
			restaurantID = getBlockNBTNumber(world, BlockPos.containing(x, y + 1, z), "RestaurantID");
			if (restaurantID == entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID) {
				if (GetRestaurantLogicParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "open")) {
					if (entity instanceof Player _player47 && !_player47.level().isClientSide())
						_player47.displayClientMessage(Component.literal("You cannot modify restaurant blocks while your restaurant is open.").withStyle(ChatFormatting.RED), true);
					if (event instanceof ICancellableEvent _cancellable) {
						_cancellable.setCanceled(true);
					}
				}
			} else if (restaurantID == 0) {
			} else {
				if (entity instanceof Player _player51 && !_player51.level().isClientSide())
					_player51.displayClientMessage(Component.literal("This block does not belong to your restaurant.").withStyle(ChatFormatting.RED), true);
				if (event instanceof ICancellableEvent _cancellable) {
					_cancellable.setCanceled(true);
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

	private static boolean getBlockNBTLogic(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getBoolean(tag);
		return false;
	}

	private static Direction getDirectionFromBlockState(BlockState blockState) {
		Property<?> prop = getPropertyByName(blockState, "facing");
		if (prop instanceof DirectionProperty dp)
			return blockState.getValue(dp);
		prop = getPropertyByName(blockState, "axis");
		return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}

	private static Property<?> getPropertyByName(BlockState state, String name) {
		for (Property<?> property : state.getProperties()) {
			if (property.getName().equals(name)) {
				return property;
			}
		}
		return null;
	}
}