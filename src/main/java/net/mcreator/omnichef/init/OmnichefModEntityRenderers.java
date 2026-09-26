/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.omnichef.client.renderer.*;

@EventBusSubscriber(Dist.CLIENT)
public class OmnichefModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(OmnichefModEntities.LOCATION_AREA.get(), LocationAreaRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.LOCATION_EDGE.get(), LocationEdgeRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.LOCATION_EDGE_LEFT.get(), LocationEdgeLeftRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.LOCATION_EDGE_RIGHT.get(), LocationEdgeRightRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.LOCATION_EDGE_DOWN.get(), LocationEdgeDownRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.CHAIR_MOB.get(), ChairMobRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.CLIENT.get(), ClientRenderer::new);
		event.registerEntityRenderer(OmnichefModEntities.CRITIC.get(), CriticRenderer::new);
	}
}