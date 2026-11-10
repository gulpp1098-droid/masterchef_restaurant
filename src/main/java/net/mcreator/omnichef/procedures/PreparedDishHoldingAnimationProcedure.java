package net.mcreator.omnichef.procedures;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;

import net.mcreator.omnichef.network.PlayPlayerAnimationMessage;
import net.mcreator.omnichef.init.OmnichefModItems;

import javax.annotation.Nullable;

@EventBusSubscriber
public class PreparedDishHoldingAnimationProcedure {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (world.isClientSide()) {
			if (entity instanceof Player) {
				if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == OmnichefModItems.PREPARED_DISH.get()) {
					if (!(entity instanceof Player ? entity.getPersistentData().getString("PlayerCurrentAnimation").equals("omnichef:animation.model.prepdishwalk") : false)) {
						if (entity instanceof Player) {
							if (entity.level().isClientSide()) {
								CompoundTag data = entity.getPersistentData();
								data.putString("PlayerCurrentAnimation", "omnichef:animation.model.prepdishwalk");
								data.putBoolean("OverrideCurrentAnimation", true);
								data.putBoolean("FirstPersonAnimation", false);
							} else {
								PacketDistributor.sendToPlayersInDimension((ServerLevel) entity.level(), new PlayPlayerAnimationMessage(entity.getId(), "omnichef:animation.model.prepdishwalk", true, false));
							}
						}
					}
				} else {
					if (entity instanceof Player ? entity.getPersistentData().getString("PlayerCurrentAnimation").equals("omnichef:animation.model.prepdishwalk") : false) {
						if (entity instanceof Player) {
							if (entity.level().isClientSide()) {
								CompoundTag data = entity.getPersistentData();
								data.remove("PlayerCurrentAnimation");
								data.remove("PlayerAnimationProgress");
								data.putBoolean("ResetPlayerAnimation", true);
								data.putBoolean("FirstPersonAnimation", false);
							} else {
								PacketDistributor.sendToPlayersInDimension((ServerLevel) entity.level(), new PlayPlayerAnimationMessage(entity.getId(), "", false, false));
							}
						}
					}
				}
			}
		}
	}
}