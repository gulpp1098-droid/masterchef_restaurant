/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.omnichef.client.renderer.CriticRenderer;
import net.mcreator.omnichef.client.renderer.ClientRenderer;
import net.mcreator.omnichef.client.renderer.ChairMobRenderer;

@EventBusSubscriber(Dist.CLIENT)
public class OmnichefModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(OmnichefModEntities.CHAIR_MOB.get(), ChairMobRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.CLIENT.get(), ClientRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.CRITIC.get(), CriticRenderer::new);
	}
}