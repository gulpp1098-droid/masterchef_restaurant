package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.entity.CriticEntity;

import java.util.UUID;

public class ClientStuckStateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		Entity leader = null;
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
								if ((world instanceof ServerLevel _level18 ? getEntityFromUUID(_level18, ownerString) : null) instanceof Player _player19 && !_player19.level().isClientSide())
									_player19.displayClientMessage(Component.literal(("A customer appears to be stuck at: " + (int) x + ", " + (int) y + ", " + (int) z + ". Please check the area.")).withStyle(ChatFormatting.YELLOW), false);
							}
							if (client instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 1));
							client.getPersistentData().putBoolean("alertSent", true);
							client.getPersistentData().putDouble("stuckCounter", 10);
						} else {
							if ((client.getPersistentData().getString("state")).equals("queue_move") && client.getPersistentData().getBoolean("patience_needed") && world.dayTime() < client.getPersistentData().getDouble("patience_end_time")) {
								client.getPersistentData().putDouble("stuckCounter", 10);
							} else {
								if (client.getPersistentData().getBoolean("leader")) {
									if (!(client instanceof CriticEntity)) {
										ClientExpPayProcedure.execute(world, entity);
									}
									ClientBeginLeavingProcedure.execute(world, entity);
								} else {
									leader = world instanceof ServerLevel _level31 ? getEntityFromUUID(_level31, (client.getPersistentData().getString("leaderUUID"))) : null;
									if (leader != null) {
										client.stopRiding();
										{
											Entity _ent = client;
											double _tx = (leader.getX());
											double _ty = (leader.getY());
											double _tz = (leader.getZ());
											_ent.teleportTo(_tx, _ty, _tz);
											if (_ent instanceof ServerPlayer _serverPlayer)
												_serverPlayer.connection.teleport(_tx, _ty, _tz, _ent.getYRot(), _ent.getXRot());
										}
										client.getPersistentData().putDouble("stuckCounter", 10);
										client.getPersistentData().putBoolean("alertSent", false);
									} else {
										ClientBeginLeavingProcedure.execute(world, entity);
									}
								}
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
}