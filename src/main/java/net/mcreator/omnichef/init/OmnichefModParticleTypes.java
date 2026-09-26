/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.mcreator.omnichef.OmnichefMod;

public class OmnichefModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, OmnichefMod.MODID);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SELECT_AREA = REGISTRY.register("select_area", () -> new SimpleParticleType(false));
}