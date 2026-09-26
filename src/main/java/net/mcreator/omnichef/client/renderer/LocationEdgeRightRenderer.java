package net.mcreator.omnichef.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.omnichef.entity.LocationEdgeRightEntity;
import net.mcreator.omnichef.client.model.ModelRestaurantEdge;

public class LocationEdgeRightRenderer extends MobRenderer<LocationEdgeRightEntity, ModelRestaurantEdge<LocationEdgeRightEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("omnichef:textures/entities/textureedge.png");

	public LocationEdgeRightRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelRestaurantEdge<LocationEdgeRightEntity>(context.bakeLayer(ModelRestaurantEdge.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(LocationEdgeRightEntity entity) {
		if (entity.getTexture() != "textureedge")
			return ResourceLocation.parse("omnichef:textures/entities/" + entity.getTexture() + ".png");
		return entityTexture;
	}
}