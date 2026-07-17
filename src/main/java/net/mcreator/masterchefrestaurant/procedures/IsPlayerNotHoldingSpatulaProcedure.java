package net.mcreator.masterchefrestaurant.procedures;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.masterchefrestaurant.network.MasterchefRestaurantModVariables;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModItems;

import javax.annotation.Nullable;

import java.util.UUID;

@EventBusSubscriber
public class IsPlayerNotHoldingSpatulaProcedure {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		execute(event, event.getEntity().level(), event.getEntity());
	}

	public static void execute(LevelAccessor world, Entity entity) {
		execute(null, world, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (!((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == MasterchefRestaurantModItems.SPATULA_GOLDEN.get())) {
			if ((world instanceof ServerLevel _level2 ? getEntityFromUUID(_level2, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null) != null) {
				if (!(world instanceof ServerLevel _level3 ? getEntityFromUUID(_level3, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).level().isClientSide())
					(world instanceof ServerLevel _level3 ? getEntityFromUUID(_level3, entity.getData(MasterchefRestaurantModVariables.PLAYER_VARIABLES).PreviewUUID) : null).discard();
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