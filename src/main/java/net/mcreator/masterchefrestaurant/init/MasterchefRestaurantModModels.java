/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.masterchefrestaurant.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.masterchefrestaurant.client.model.*;

@EventBusSubscriber(Dist.CLIENT)
public class MasterchefRestaurantModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelChairMob.LAYER_LOCATION, ModelChairMob::createBodyLayer);
		event.registerLayerDefinition(ModelClientNew.LAYER_LOCATION, ModelClientNew::createBodyLayer);
		event.registerLayerDefinition(ModelClientNewV2.LAYER_LOCATION, ModelClientNewV2::createBodyLayer);
		event.registerLayerDefinition(ModelCriticBase.LAYER_LOCATION, ModelCriticBase::createBodyLayer);
		event.registerLayerDefinition(ModelClientBase.LAYER_LOCATION, ModelClientBase::createBodyLayer);
		event.registerLayerDefinition(ModelRestaurantArea.LAYER_LOCATION, ModelRestaurantArea::createBodyLayer);
		event.registerLayerDefinition(ModelRestaurantEdge.LAYER_LOCATION, ModelRestaurantEdge::createBodyLayer);
	}
}