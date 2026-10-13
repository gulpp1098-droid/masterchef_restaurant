package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class ClientRestaurantGoStateProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		Entity client = null;
		String goToString = "";
		String ReceptionPosition = "";
		double RugX = 0;
		double RugY = 0;
		double RugZ = 0;
		double restaurantID = 0;
		double RecX = 0;
		double RecY = 0;
		double RecZ = 0;
		client = entity;
		goToString = client.getPersistentData().getString("last_rug");
		RugX = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(GetPartFromStringProcedure.execute(0, goToString));
		RugY = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(GetPartFromStringProcedure.execute(1, goToString));
		RugZ = new Object() {
			double convert(String s) {
				try {
					return Double.parseDouble(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert(GetPartFromStringProcedure.execute(2, goToString));
		if (!getBlockNBTLogic(world, BlockPos.containing(RugX, RugY, RugZ), "occupied")) {
			if (Math.floor(client.getX()) == RugX && Math.floor(client.getZ()) == RugZ && Math.abs(client.getY() - RugY) < 0.5) {
				if (client instanceof Mob _entity)
					_entity.getNavigation().moveTo(RugX, RugY, RugZ, 1);
			} else {
				if (client instanceof Mob _entity)
					_entity.getNavigation().stop();
				if (client instanceof Mob _mob7)
					_mob7.setNoAi(true);
				client.getPersistentData().putDouble("stuckCounter", 10);
				client.getPersistentData().putString("state", "queue_move");
				StartClientPatienceProcedure.execute(world, entity, 3600);
				client.getPersistentData().putDouble("current_position", (getBlockNBTNumber(world, BlockPos.containing(RugX, RugY, RugZ), "queue") + 2));
				client.getPersistentData().putDouble("DestX", RugX);
				client.getPersistentData().putDouble("DestY", RugY);
				client.getPersistentData().putDouble("DestZ", RugZ);
				restaurantID = client.getPersistentData().getDouble("RestaurantID");
				ReceptionPosition = GetRestaurantStringParameterProcedure.execute(RestaurantIndexSearchByIDProcedure.execute(world, restaurantID), "restaurants", OmnichefModVariables.MapVariables.get(world).Restaurant_File_Name,
						OmnichefModVariables.MapVariables.get(world).Restaurant_Info_Path, "reception");
				RecX = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(0, ReceptionPosition));
				RecY = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(1, ReceptionPosition));
				RecZ = new Object() {
					double convert(String s) {
						try {
							return Double.parseDouble(s.trim());
						} catch (Exception e) {
						}
						return 0;
					}
				}.convert(GetPartFromStringProcedure.execute(2, ReceptionPosition));
				SetNumberNBTProcedure.execute(world, RecX, RecY, RecZ, getBlockNBTNumber(world, BlockPos.containing(RecX, RecY, RecZ), "queue_length") + 1, "queue_length");
				client.getPersistentData().putBoolean("queue_registered", true);
				SetLogicNBTProcedure.execute(world, RugX, RugY, RugZ, true, "occupied");
			}
		}
	}

	private static boolean getBlockNBTLogic(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getBoolean(tag);
		return false;
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}
}