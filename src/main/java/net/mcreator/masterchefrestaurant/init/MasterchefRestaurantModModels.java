/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.masterchefrestaurant.client.model.ModelRestaurantEdge;
import net.mcreator.masterchefrestaurant.client.model.ModelRestaurantArea;
import net.mcreator.masterchefrestaurant.client.model.ModelClientNew;
import net.mcreator.masterchefrestaurant.client.model.ModelChairMob;

@EventBusSubscriber(Dist.CLIENT)
public class MasterchefRestaurantModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelChairMob.LAYER_LOCATION, ModelChairMob::createBodyLayer);
		event.registerLayerDefinition(ModelClientNew.LAYER_LOCATION, ModelClientNew::createBodyLayer);
		event.registerLayerDefinition(ModelRestaurantArea.LAYER_LOCATION, ModelRestaurantArea::createBodyLayer);
		event.registerLayerDefinition(ModelRestaurantEdge.LAYER_LOCATION, ModelRestaurantEdge::createBodyLayer);
	}
}