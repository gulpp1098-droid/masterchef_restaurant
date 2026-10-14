package net.mcreator.omnichef.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.network.OmnichefModVariables;

public class SpatulaShowingAreaToSelectProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		Entity Owner = null;
		double BlockXPos = 0;
		double BlockZPos = 0;
		double EntityPositionX = 0;
		double EntityPositionZ = 0;
		double HitX = 0;
		double HitY = 0;
		double HitZ = 0;
		double PreviewState = 0;
		String PreviewData = "";
		if (!world.isClientSide()) {
			Owner = entity;
			if (itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("SelectingRestaurantLocation")
					|| itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("RelocatingRestaurant")) {
				HitX = Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getX();
				HitY = Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getY();
				HitZ = Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getZ();
				if (!world.isEmptyBlock(BlockPos.containing(HitX, HitY, HitZ))) {
					BlockXPos = Math.floor(
							Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getX() / 5d) * 5;
					BlockZPos = Math.floor(
							Owner.level().clip(new ClipContext(Owner.getEyePosition(1f), Owner.getEyePosition(1f).add(Owner.getViewVector(1f).scale(10)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Owner)).getBlockPos().getZ() / 5d) * 5;
					EntityPositionX = BlockXPos + 2.5;
					EntityPositionZ = BlockZPos + 2.5;
					PreviewState = GetRestaurantPreviewStateProcedure.execute(world, EntityPositionX, EntityPositionZ, entity);
					PreviewData = EntityPositionX + ":" + EntityPositionZ + ":" + PreviewState;
				}
			}
			if ((entity.getData(OmnichefModVariables.PLAYER_VARIABLES).RestaurantPreviewData).equals(PreviewData)) {
				{
					OmnichefModVariables.PlayerVariables _vars = entity.getData(OmnichefModVariables.PLAYER_VARIABLES);
					_vars.RestaurantPreviewData = PreviewData;
					_vars.markSyncDirty();
				}
			}
		}
	}
}