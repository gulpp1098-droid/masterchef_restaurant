package net.mcreator.masterchefrestaurant.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.masterchefrestaurant.entity.LocationEdgeDownEntity;
import net.mcreator.masterchefrestaurant.client.model.ModelRestaurantEdge;

public class LocationEdgeDownRenderer extends MobRenderer<LocationEdgeDownEntity, ModelRestaurantEdge<LocationEdgeDownEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("masterchef_restaurant:textures/entities/textureedge.png");

	public LocationEdgeDownRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelRestaurantEdge<LocationEdgeDownEntity>(context.bakeLayer(ModelRestaurantEdge.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(LocationEdgeDownEntity entity) {
		if (entity.getTexture() != "textureedge")
			return ResourceLocation.parse("masterchef_restaurant:textures/entities/" + entity.getTexture() + ".png");
		return entityTexture;
	}
}