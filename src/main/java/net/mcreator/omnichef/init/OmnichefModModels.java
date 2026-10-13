/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.omnichef.client.model.ModelCriticBase;
import net.mcreator.omnichef.client.model.ModelClientBase;
import net.mcreator.omnichef.client.model.ModelChairMob;

@EventBusSubscriber(Dist.CLIENT)
public class OmnichefModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelChairMob.LAYER_LOCATION, ModelChairMob::createBodyLayer);
		event.registerLayerDefinition(ModelCriticBase.LAYER_LOCATION, ModelCriticBase::createBodyLayer);
		event.registerLayerDefinition(ModelClientBase.LAYER_LOCATION, ModelClientBase::createBodyLayer);
	}
}