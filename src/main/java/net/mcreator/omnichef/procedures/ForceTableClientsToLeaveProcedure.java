package net.mcreator.omnichef.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.entity.CriticEntity;
import net.mcreator.omnichef.entity.ClientEntity;

import java.util.Comparator;

public class ForceTableClientsToLeaveProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		Entity tableLeader = null;
		double restaurantID = 0;
		if (!world.isClientSide()) {
			restaurantID = getBlockNBTNumber(world, BlockPos.containing(x, y, z), "RestaurantID");
			{
				final Vec3 _center = new Vec3(x, y, z);
				for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
					if ((entityiterator instanceof ClientEntity || entityiterator instanceof CriticEntity) && entityiterator.getPersistentData().getBoolean("leader") && entityiterator.getPersistentData().getBoolean("table_reserved")
							&& restaurantID == entityiterator.getPersistentData().getDouble("RestaurantID") && entityiterator.getPersistentData().getDouble("reserved_table_x") == x
							&& entityiterator.getPersistentData().getDouble("reserved_table_y") == y && entityiterator.getPersistentData().getDouble("reserved_table_z") == z) {
						tableLeader = entityiterator;
						break;
					}
				}
			}
			if (tableLeader != null) {
				{
					final Vec3 _center = new Vec3(x, y, z);
					for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
						if (entityiterator == tableLeader || IsMemberOfClientGroupProcedure.execute(entityiterator, tableLeader)) {
							ForceClientToLeaveProcedure.execute(world, entityiterator);
						}
					}
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
}