package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModEntities;
import net.mcreator.masterchefrestaurant.entity.ChairMobEntity;

import java.util.Comparator;

public class ClientInteractWithChairProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity chair = null;
		Entity client = null;
		client = entity;
		if (client.getPersistentData().getDouble("DestX") - 0.5 == x && client.getPersistentData().getDouble("DestY") == y && client.getPersistentData().getDouble("DestZ") - 0.5 == z
				&& client.getPersistentData().getDouble("member") == getBlockNBTNumber(world, BlockPos.containing(x, y, z), "ChairNumber") && !client.isPassenger() && !(!((findEntityInWorldRange(world, ChairMobEntity.class, x, y, z, 0.5)) == null))
				&& (client.getPersistentData().getString("state")).equals("find_chair") && client.getPersistentData().getDouble("TableNumber") == getBlockNBTNumber(world, BlockPos.containing(x, y, z), "TableNumber")) {
			chair = world instanceof ServerLevel _level11 ? MasterchefRestaurantModEntities.CHAIR_MOB.get().spawn(_level11, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED) : null;
			if (chair != null) {
				client.startRiding(chair);
				client.getPersistentData().putDouble("stuckCounter", (-1));
				client.getPersistentData().putString("state", "order_pick");
				client.getPersistentData().putDouble("order_pick_time", (Mth.nextInt(RandomSource.create(), 20, 100) + world.dayTime()));
			}
		}
	}

	private static double getBlockNBTNumber(LevelAccessor world, BlockPos pos, String tag) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity != null)
			return blockEntity.getPersistentData().getDouble(tag);
		return -1;
	}

	private static Entity findEntityInWorldRange(LevelAccessor world, Class<? extends Entity> clazz, double x, double y, double z, double range) {
		return (Entity) world.getEntitiesOfClass(clazz, AABB.ofSize(new Vec3(x, y, z), range, range, range), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z))).findFirst().orElse(null);
	}
}