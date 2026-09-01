package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModBlocks;

import java.util.UUID;

public class ClientStuckStateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		String ownerString = "";
		String receptionString = "";
		BlockState block = Blocks.AIR.defaultBlockState();
		double RecZ = 0;
		double RecX = 0;
		double RecY = 0;
		double DestX = 0;
		double DestY = 0;
		double DestZ = 0;
		double restaurantIndex = 0;
		if (!world.isClientSide()) {
			client = entity;
			if ((client.getPersistentData().getString("state")).equals("restaurant_go") || (client.getPersistentData().getString("state")).equals("queue_move") || (client.getPersistentData().getString("state")).equals("table_go")
					|| (client.getPersistentData().getString("state")).equals("find_chair")) {
				if (client.getPersistentData().getDouble("stuckCounter") >= 0) {
					if (client.getPersistentData().getDouble("stuckCounter") > 0) {
						client.getPersistentData().putDouble("stuckCounter", (client.getPersistentData().getDouble("stuckCounter") - 0.5));
					} else if (client.getPersistentData().getDouble("stuckCounter") <= 0) {
						restaurantIndex = RestaurantIndexSearchByIDProcedure.execute(world, client.getPersistentData().getDouble("RestaurantID"));
						if (!client.getPersistentData().getBoolean("alertSent")) {
							ownerString = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
									MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
							if ((world instanceof ServerLevel _level12 ? getEntityFromUUID(_level12, ownerString) : null) != null) {
								if ((world instanceof ServerLevel _level16 ? getEntityFromUUID(_level16, ownerString) : null) instanceof Player _player && !_player.level().isClientSide())
									_player.displayClientMessage(Component.literal(("Hey! I might be stuck at: " + (int) x + " " + (int) y + " " + (int) z + " ! Please check this out!")), false);
							}
							if (client instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 1));
							client.getPersistentData().putBoolean("alertSent", true);
							client.getPersistentData().putDouble("stuckCounter", 10);
						} else {
							if ((client.getPersistentData().getString("state")).equals("queue_move") && client.getPersistentData().getDouble("patience") > 0) {
								client.getPersistentData().putDouble("stuckCounter", 10);
							} else {
								ClientExpPayProcedure.execute(world, entity);
								DestX = client.getPersistentData().getDouble("DestX");
								DestY = client.getPersistentData().getDouble("DestY");
								DestZ = client.getPersistentData().getDouble("DestZ");
								block = (world.getBlockState(BlockPos.containing(DestX, DestY, DestZ)));
								if (block.getBlock() == MasterchefRestaurantModBlocks.RUG_QUEUE.get()) {
									SetLogicNBTProcedure.execute(world, DestX, DestY, DestZ, false, "occupied");
									receptionString = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_File_Name,
											MasterchefRestaurantModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
									RecX = new Object() {
										double convert(String s) {
											try {
												return Double.parseDouble(s.trim());
											} catch (Exception e) {
											}
											return 0;
										}
									}.convert(GetPartFromStringProcedure.execute(0, receptionString));
									RecY = new Object() {
										double convert(String s) {
											try {
												return Double.parseDouble(s.trim());
											} catch (Exception e) {
											}
											return 0;
										}
									}.convert(GetPartFromStringProcedure.execute(1, receptionString));
									RecZ = new Object() {
										double convert(String s) {
											try {
												return Double.parseDouble(s.trim());
											} catch (Exception e) {
											}
											return 0;
										}
									}.convert(GetPartFromStringProcedure.execute(2, receptionString));
									SetNumberNBTProcedure.execute(world, RecX, RecY, RecZ, getBlockNBTNumber(world, BlockPos.containing(RecX, RecY, RecZ), "queue_length") - 1, "queue_length");
								} else if (block.getBlock() == MasterchefRestaurantModBlocks.SERVICE_TABLE.get() && client.getPersistentData().getBoolean("leader")) {
									SetLogicNBTProcedure.execute(world, DestX, DestY, DestZ, false, "occupied");
								} else if (block.getBlock() == MasterchefRestaurantModBlocks.CHAIR.get() && client.getPersistentData().getBoolean("leader")) {
									if ((world.getBlockState(BlockPos.containing(DestX + (getDirectionFromBlockState(block)).getStepX(), DestY, DestZ + (getDirectionFromBlockState(block)).getStepZ())))
											.getBlock() == MasterchefRestaurantModBlocks.SERVICE_TABLE.get()) {
										SetLogicNBTProcedure.execute(world, DestX + (getDirectionFromBlockState(block)).getStepX(), DestY, DestZ + (getDirectionFromBlockState(block)).getStepZ(), false, "occupied");
									}
								}
								client.getPersistentData().putString("state", "leave");
							}
						}
					}
				}
			}
		}
	}

	private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
		try {
			return level.getEntity(UUID.fromString(uuid));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
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