package net.mcreator.omnichef.procedures;

import net.minecraft.world.phys.Vec3;
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
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.entity.CriticEntity;

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
		double currentDistance = 0;
		double step = 0;
		double targetX = 0;
		double targetZ = 0;
		boolean keepGoing = false;
		Vec3 vektor = Vec3.ZERO;
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
							ownerString = GetRestaurantStringParameterProcedure.execute(restaurantIndex, "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
									OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "owner");
							if ((world instanceof ServerLevel _level12 ? getEntityFromUUID(_level12, ownerString) : null) != null) {
								if ((world instanceof ServerLevel _level18 ? getEntityFromUUID(_level18, ownerString) : null) instanceof Player _player19 && !_player19.level().isClientSide())
									_player19.displayClientMessage(Component.literal(("A customer appears to be stuck at: " + (int) x + ", " + (int) y + ", " + (int) z + ". Please check the area.")).withStyle(ChatFormatting.YELLOW), false);
							}
							if (client instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 1));
							client.getPersistentData().putBoolean("alertSent", true);
							client.getPersistentData().putDouble("stuckCounter", 10);
							if (client.getPersistentData().getBoolean("leader") && (client.getPersistentData().getString("state")).equals("restaurant_go")) {
								client.getPersistentData().putDouble("stuckJumpDistance", (-1));
								vektor = new Vec3(((client.getPersistentData().getDouble("RugX") + 0.5) - client.getX()), 0, ((client.getPersistentData().getDouble("RugZ") + 0.5) - client.getZ()));
								client.setDeltaMovement(new Vec3(((vektor.normalize()).x() * 0.22), 0.68, ((vektor.normalize()).z() * 0.22)));
								client.getPersistentData().putDouble("stuckCounter", 1);
							}
						} else {
							if ((client.getPersistentData().getString("state")).equals("queue_move") && client.getPersistentData().getBoolean("patience_needed") && world.dayTime() < client.getPersistentData().getDouble("patience_end_time")) {
								client.getPersistentData().putDouble("stuckCounter", 10);
							} else {
								if (client.getPersistentData().getBoolean("leader")) {
									if ((client.getPersistentData().getString("state")).equals("restaurant_go")) {
										if (!client.onGround()) {
											client.getPersistentData().putDouble("stuckCounter", (-1));
											keepGoing = true;
										} else {
											vektor = new Vec3(((client.getPersistentData().getDouble("RugX") + 0.5) - client.getX()), 0, ((client.getPersistentData().getDouble("RugZ") + 0.5) - client.getZ()));
											currentDistance = vektor.length();
											if (client.getPersistentData().getDouble("stuckJumpDistance") == -1) {
												client.getPersistentData().putDouble("stuckJumpDistance", currentDistance);
												client.getPersistentData().putDouble("stuckCounter", 1);
												keepGoing = true;
											} else if (currentDistance <= client.getPersistentData().getDouble("stuckJumpDistance") - 0.3) {
												client.getPersistentData().putBoolean("alertSent", false);
												client.getPersistentData().putDouble("stuckCounter", 10);
												keepGoing = true;
											} else {
												step = 1;
												for (int _i1 = 0; _i1 < 2; _i1++) {
													if (!keepGoing && currentDistance >= step && y == Math.floor(y)) {
														targetX = x + (vektor.normalize()).x();
														targetZ = z + (vektor.normalize()).z();
														if (!world.isEmptyBlock(BlockPos.containing(targetX, y - 1, targetZ)) && world.isEmptyBlock(BlockPos.containing(targetX, y, targetZ))
																&& world.isEmptyBlock(BlockPos.containing(targetX, y + 1, targetZ))) {
															{
																Entity _ent = client;
																double _tx = targetX;
																double _ty = y;
																double _tz = targetZ;
																_ent.teleportTo(_tx, _ty, _tz);
																if (_ent instanceof ServerPlayer _serverPlayer)
																	_serverPlayer.connection.teleport(_tx, _ty, _tz, _ent.getYRot(), _ent.getXRot());
															}
															break;
														}
													}
													step = step + 1;
												}
											}
										}
									}
									if (!keepGoing) {
										if (!(client instanceof CriticEntity)) {
											ClientExpPayProcedure.execute(entity);
										}
										ClientBeginLeavingProcedure.execute(world, entity);
									}
								} else {
									leader = world instanceof ServerLevel _level68 ? getEntityFromUUID(_level68, (client.getPersistentData().getString("leaderUUID"))) : null;
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