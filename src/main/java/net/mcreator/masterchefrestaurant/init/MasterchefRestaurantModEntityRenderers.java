/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.masterchefrestaurant.client.renderer.*;

@EventBusSubscriber(Dist.CLIENT)
public class MasterchefRestaurantModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MasterchefRestaurantModEntities.LOCATION_AREA.get(), LocationAreaRenderer::new);
		event.registerEntityRenderer(MasterchefRestaurantModEntities.LOCATION_EDGE.get(), LocationEdgeRenderer::new);
		event.registerEntityRenderer(MasterchefRestaurantModEntities.LOCATION_EDGE_LEFT.get(), LocationEdgeLeftRenderer::new);
		event.registerEntityRenderer(MasterchefRestaurantModEntities.LOCATION_EDGE_RIGHT.get(), LocationEdgeRightRenderer::new);
		event.registerEntityRenderer(MasterchefRestaurantModEntities.LOCATION_EDGE_DOWN.get(), LocationEdgeDownRenderer::new);
		event.registerEntityRenderer(MasterchefRestaurantModEntities.CHAIR_MOB.get(), ChairMobRenderer::new);
		event.registerEntityRenderer(MasterchefRestaurantModEntities.CLIENT.get(), ClientRenderer::new);
	}
}