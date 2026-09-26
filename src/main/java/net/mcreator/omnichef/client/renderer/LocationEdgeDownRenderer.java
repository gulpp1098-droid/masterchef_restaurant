package net.mcreator.omnichef.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.omnichef.entity.LocationEdgeDownEntity;
import net.mcreator.omnichef.client.model.ModelRestaurantEdge;

public class LocationEdgeDownRenderer extends MobRenderer<LocationEdgeDownEntity, ModelRestaurantEdge<LocationEdgeDownEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("omnichef:textures/entities/textureedge.png");

	public LocationEdgeDownRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelRestaurantEdge<LocationEdgeDownEntity>(context.bakeLayer(ModelRestaurantEdge.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(LocationEdgeDownEntity entity) {
		if (entity.getTexture() != "textureedge")
			return ResourceLocation.parse("omnichef:textures/entities/" + entity.getTexture() + ".png");
		return entityTexture;
	}
}