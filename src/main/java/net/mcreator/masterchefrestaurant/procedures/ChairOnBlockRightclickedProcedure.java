package net.mcreator.masterchefrestaurant.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModEntities;
import net.mcreator.masterchefrestaurant.entity.ChairMobEntity;

import java.util.Comparator;

public class ChairOnBlockRightclickedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity chair = null;
		chair = findEntityInWorldRange(world, ChairMobEntity.class, (x + 0.5), y, (z + 0.5), 1);
		if (chair == null) {
			chair = world instanceof ServerLevel _level1 ? MasterchefRestaurantModEntities.CHAIR_MOB.get().spawn(_level1, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED) : null;
		}
		if (chair != null) {
			entity.startRiding(chair);
		}
	}

	private static Entity findEntityInWorldRange(LevelAccessor world, Class<? extends Entity> clazz, double x, double y, double z, double range) {
		return (Entity) world.getEntitiesOfClass(clazz, AABB.ofSize(new Vec3(x, y, z), range, range, range), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(x, y, z))).findFirst().orElse(null);
	}
}