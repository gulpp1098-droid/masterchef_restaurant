/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

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

import net.mcreator.omnichef.entity.CriticEntity;
import net.mcreator.omnichef.entity.ClientEntity;
import net.mcreator.omnichef.entity.ChairMobEntity;
import net.mcreator.omnichef.OmnichefMod;

@EventBusSubscriber
public class OmnichefModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, OmnichefMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<ChairMobEntity>> CHAIR_MOB = register("chair_mob",
			EntityType.Builder.<ChairMobEntity>of(ChairMobEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(16).setUpdateInterval(3).fireImmune()

					.sized(0.1f, 0.5f));
	public static final DeferredHolder<EntityType<?>, EntityType<ClientEntity>> CLIENT = register("client",
			EntityType.Builder.<ClientEntity>of(ClientEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<CriticEntity>> CRITIC = register("critic",
			EntityType.Builder.<CriticEntity>of(CriticEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune()

					.sized(0.6f, 1.8f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(RegisterSpawnPlacementsEvent event) {
		ChairMobEntity.init(event);
		ClientEntity.init(event);
		CriticEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(CHAIR_MOB.get(), ChairMobEntity.createAttributes().build());
		event.put(CLIENT.get(), ClientEntity.createAttributes().build());
		event.put(CRITIC.get(), CriticEntity.createAttributes().build());
	}
}