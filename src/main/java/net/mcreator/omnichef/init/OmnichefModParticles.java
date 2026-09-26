/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.omnichef.client.particle.SelectAreaParticle;

@EventBusSubscriber(Dist.CLIENT)
public class OmnichefModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(OmnichefModParticleTypes.SELECT_AREA.get(), SelectAreaParticle::provider);
	}
}