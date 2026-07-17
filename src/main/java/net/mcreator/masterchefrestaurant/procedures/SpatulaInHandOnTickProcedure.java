package net.mcreator.masterchefrestaurant.procedures;

import org.checkerframework.checker.units.qual.Area;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.component.DataComponents;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.entity.LocationEdgeRightEntity;
import net.mcreator.masterchefrestaurant.entity.LocationEdgeLeftEntity;
import net.mcreator.masterchefrestaurant.entity.LocationEdgeEntity;
import net.mcreator.masterchefrestaurant.entity.LocationEdgeDownEntity;

import java.util.UUID;
import java.util.Comparator;

public class SpatulaInHandOnTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		Entity Owner = null;
		Entity Area = null;
		double BlockXPos = 0;
		double BlockYPos = 0;
		double BlockZPos = 0;
		double EntityPositionX = 0;
		double EntityPositionZ = 0;
		if (!world.isClientSide()) {
			Owner = entity;
			if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("SelectingRestaurantLocation")) {
				SpatulaShowingAreaToSelectProcedure.execute(world, entity, itemstack);
			} else {
				if ((world instanceof ServerLevel _level3 ? getEntityFromUUID(_level3, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null) != null) {
					if (!(world instanceof ServerLevel _level4 ? getEntityFromUUID(_level4, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).level().isClientSide())
						(world instanceof ServerLevel _level4 ? getEntityFromUUID(_level4, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).discard();
				}
			}
			if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("ShowRestaurantArea") && itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("SpawnArea")) {
				ShowingClaimedAreaOfRestaurantProcedure.execute(world, x, y, z, entity, itemstack);
			} else if (!itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("ShowRestaurantArea")) {
				{
					final Vec3 _center = new Vec3(x, y, z);
					for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(100 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList()) {
						if ((entityiterator instanceof LocationEdgeEntity || entityiterator instanceof LocationEdgeDownEntity || entityiterator instanceof LocationEdgeLeftEntity || entityiterator instanceof LocationEdgeRightEntity)
								&& entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID == entityiterator.getPersistentData().getDouble("RestaurantID")) {
							if (!entityiterator.level().isClientSide())
								entityiterator.discard();
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