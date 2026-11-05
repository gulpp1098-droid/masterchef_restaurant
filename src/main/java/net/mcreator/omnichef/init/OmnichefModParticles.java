/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.omnichef.client.particle.UnsatisfiedParticleParticle;
import net.mcreator.omnichef.client.particle.SelectAreaParticle;
import net.mcreator.omnichef.client.particle.SatisfiedParticleParticle;
import net.mcreator.omnichef.client.particle.PerfectParticleParticle;

@EventBusSubscriber(Dist.CLIENT)
public class OmnichefModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(OmnichefModParticleTypes.SELECT_AREA.get(), SelectAreaParticle::provider);
		event.registerSpriteSet(OmnichefModParticleTypes.PERFECT_PARTICLE.get(), PerfectParticleParticle::provider);
		event.registerSpriteSet(OmnichefModParticleTypes.SATISFIED_PARTICLE.get(), SatisfiedParticleParticle::provider);
		event.registerSpriteSet(OmnichefModParticleTypes.UNSATISFIED_PARTICLE.get(), UnsatisfiedParticleParticle::provider);
	}
}