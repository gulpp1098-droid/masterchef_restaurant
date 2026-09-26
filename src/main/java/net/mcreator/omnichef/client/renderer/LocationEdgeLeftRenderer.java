package net.mcreator.omnichef.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.omnichef.entity.LocationEdgeLeftEntity;
import net.mcreator.omnichef.client.model.ModelRestaurantEdge;

public class LocationEdgeLeftRenderer extends MobRenderer<LocationEdgeLeftEntity, ModelRestaurantEdge<LocationEdgeLeftEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("omnichef:textures/entities/textureedge.png");

	public LocationEdgeLeftRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelRestaurantEdge<LocationEdgeLeftEntity>(context.bakeLayer(ModelRestaurantEdge.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(LocationEdgeLeftEntity entity) {
		if (entity.getTexture() != "textureedge")
			return ResourceLocation.parse("omnichef:textures/entities/" + entity.getTexture() + ".png");
		return entityTexture;
	}
}