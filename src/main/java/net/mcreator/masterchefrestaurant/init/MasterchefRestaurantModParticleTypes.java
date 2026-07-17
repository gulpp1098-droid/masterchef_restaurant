/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.mcreator.masterchefrestaurant.MasterchefRestaurantMod;

public class MasterchefRestaurantModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, MasterchefRestaurantMod.MODID);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SELECT_AREA = REGISTRY.register("select_area", () -> new SimpleParticleType(false));
}