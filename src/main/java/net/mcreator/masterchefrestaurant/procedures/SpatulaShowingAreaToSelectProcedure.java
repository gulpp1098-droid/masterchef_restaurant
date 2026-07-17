package net.mcreator.masterchefrestaurant.procedures;

import org.checkerframework.checker.units.qual.Area;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModEntities;

import java.util.UUID;

public class SpatulaShowingAreaToSelectProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
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
				BlockXPos = Math.floor(Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getX() / 5d)
						* 5;
				BlockYPos = Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getY() + 1.1;
				BlockZPos = Math.floor(Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getZ() / 5d)
						* 5;
				EntityPositionX = BlockXPos + 2.5;
				EntityPositionZ = BlockZPos + 2.5;
				if (!world
						.isEmptyBlock(BlockPos
								.containing(Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getX(),
										BlockYPos - 1, Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner))
												.getBlockPos().getZ()))
						&& world.isEmptyBlock(BlockPos.containing(
								Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getX(), BlockYPos,
								Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getZ()))) {
					if ((world instanceof ServerLevel _level12 ? getEntityFromUUID(_level12, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null) != null) {
						if (!((world instanceof ServerLevel _level13 ? getEntityFromUUID(_level13, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).getX() == EntityPositionX
								&& (world instanceof ServerLevel _level15 ? getEntityFromUUID(_level15, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).getY() == BlockYPos - 1.1
								&& (world instanceof ServerLevel _level17 ? getEntityFromUUID(_level17, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).getZ() == EntityPositionZ)) {
							Area = world instanceof ServerLevel _level19 ? getEntityFromUUID(_level19, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null;
							{
								Entity _ent = (world instanceof ServerLevel _level20 ? getEntityFromUUID(_level20, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null);
								_ent.teleportTo(EntityPositionX, (BlockYPos - 1.1), EntityPositionZ);
								if (_ent instanceof ServerPlayer _serverPlayer)
									_serverPlayer.connection.teleport(EntityPositionX, (BlockYPos - 1.1), EntityPositionZ, _ent.getYRot(), _ent.getXRot());
							}
							{
								Entity _ent = (world instanceof ServerLevel _level22 ? getEntityFromUUID(_level22, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null);
								_ent.setYRot(0);
								_ent.setXRot(0);
								_ent.setYBodyRot(_ent.getYRot());
								_ent.setYHeadRot(_ent.getYRot());
								_ent.yRotO = _ent.getYRot();
								_ent.xRotO = _ent.getXRot();
								if (_ent instanceof LivingEntity _entity) {
									_entity.yBodyRotO = _entity.getYRot();
									_entity.yHeadRotO = _entity.getYRot();
								}
							}
						}
					} else {
						Area = world instanceof ServerLevel _level24 ? MasterchefRestaurantModEntities.LOCATION_AREA.get().spawn(_level24, BlockPos.containing(EntityPositionX, BlockYPos - 1.1, EntityPositionZ), MobSpawnType.MOB_SUMMONED) : null;
						Area.getPersistentData().putDouble("RestaurantID", Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).Restaurant_ID);
						{
							MasterchefRestaurantModVariables.PlayerVariables _vars = Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES);
							_vars.PreviewUUID = Area.getStringUUID();
							_vars.markSyncDirty();
						}
					}
				} else {
					if ((world instanceof ServerLevel _level27 ? getEntityFromUUID(_level27, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null) != null) {
						if (!(world instanceof ServerLevel _level28 ? getEntityFromUUID(_level28, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).level().isClientSide())
							(world instanceof ServerLevel _level28 ? getEntityFromUUID(_level28, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).discard();
					}
				}
			} else {
				if ((world instanceof ServerLevel _level30 ? getEntityFromUUID(_level30, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null) != null) {
					if (!(world instanceof ServerLevel _level31 ? getEntityFromUUID(_level31, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).level().isClientSide())
						(world instanceof ServerLevel _level31 ? getEntityFromUUID(_level31, Owner.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).discard();
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