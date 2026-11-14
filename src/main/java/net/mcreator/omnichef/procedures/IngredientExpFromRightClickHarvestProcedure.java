package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.OmnichefMod;

import javax.annotation.Nullable;

@EventBusSubscriber
public class IngredientExpFromRightClickHarvestProcedure {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != InteractionHand.MAIN_HAND)
			return;
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getLevel().getBlockState(event.getPos()), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		execute(null, world, x, y, z, blockstate, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		double cropAge = 0;
		double nextAge = 0;
		if (!world.isClientSide()) {
			if (IsUserRestaurantOwnerProcedure.execute(world, entity)) {
				if (blockstate.is(BlockTags.create(ResourceLocation.parse("minecraft:crops")))) {
					if (getIntFromBlockState(blockstate, "age") >= 0) {
						if (getIntFromBlockState(blockstate, "age") >= (blockstate.getBlock().getStateDefinition().getProperty("age") instanceof IntegerProperty _max8 ? _max8.getPossibleValues().stream().max(Integer::compareTo).get() : -1)) {
							OmnichefMod.queueServerWork(1, () -> {
								if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == blockstate.getBlock()) {
									if (getIntFromBlockState(blockstate, "age") >= 0) {
										if (getIntFromBlockState(blockstate,
												"age") < (blockstate.getBlock().getStateDefinition().getProperty("age") instanceof IntegerProperty _max17 ? _max17.getPossibleValues().stream().max(Integer::compareTo).get() : -1)) {
											AddIngredientActionExpProcedure.execute(world, 1, entity.getData(OmnichefModVariables.PLAYER_VARIABLES).Restaurant_ID);
										}
									}
								}
							});
						}
					}
				}
			}
		}
	}

	private static int getIntFromBlockState(BlockState blockState, String property) {
		Property<?> prop = blockState.getBlock().getStateDefinition().getProperty(property);
		return prop instanceof IntegerProperty ip ? blockState.getValue(ip) : -1;
	}
}