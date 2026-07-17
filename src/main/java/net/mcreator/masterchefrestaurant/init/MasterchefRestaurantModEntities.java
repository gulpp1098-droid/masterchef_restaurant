/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;

import net.mcreator.masterchefrestaurant.entity.*;
import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

@EventBusSubscriber
public class MasterchefRestaurantModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, MasterchefRestaurantMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<LocationAreaEntity>> LOCATION_AREA = register("location_area",
			EntityType.Builder.<LocationAreaEntity>of(LocationAreaEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<LocationEdgeEntity>> LOCATION_EDGE = register("location_edge",
			EntityType.Builder.<LocationEdgeEntity>of(LocationEdgeEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<LocationEdgeLeftEntity>> LOCATION_EDGE_LEFT = register("location_edge_left",
			EntityType.Builder.<LocationEdgeLeftEntity>of(LocationEdgeLeftEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<LocationEdgeRightEntity>> LOCATION_EDGE_RIGHT = register("location_edge_right",
			EntityType.Builder.<LocationEdgeRightEntity>of(LocationEdgeRightEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<LocationEdgeDownEntity>> LOCATION_EDGE_DOWN = register("location_edge_down",
			EntityType.Builder.<LocationEdgeDownEntity>of(LocationEdgeDownEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(5f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<ChairMobEntity>> CHAIR_MOB = register("chair_mob",
			EntityType.Builder.<ChairMobEntity>of(ChairMobEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).fireImmune()

					.sized(0.1f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<ClientEntity>> CLIENT = register("client",
			EntityType.Builder.<ClientEntity>of(ClientEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(0.6f, 1.8f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(RegisterSpawnPlacementsEvent event) {
		LocationAreaEntity.init(event);
		LocationEdgeEntity.init(event);
		LocationEdgeLeftEntity.init(event);
		LocationEdgeRightEntity.init(event);
		LocationEdgeDownEntity.init(event);
		ChairMobEntity.init(event);
		ClientEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(LOCATION_AREA.get(), LocationAreaEntity.createAttributes().build());
		event.put(LOCATION_EDGE.get(), LocationEdgeEntity.createAttributes().build());
		event.put(LOCATION_EDGE_LEFT.get(), LocationEdgeLeftEntity.createAttributes().build());
		event.put(LOCATION_EDGE_RIGHT.get(), LocationEdgeRightEntity.createAttributes().build());
		event.put(LOCATION_EDGE_DOWN.get(), LocationEdgeDownEntity.createAttributes().build());
		event.put(CHAIR_MOB.get(), ChairMobEntity.createAttributes().build());
		event.put(CLIENT.get(), ClientEntity.createAttributes().build());
	}
}