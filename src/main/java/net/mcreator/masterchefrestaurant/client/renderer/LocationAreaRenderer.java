package net.mcreator.masterchefrestaurant.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.masterchefrestaurant.entity.LocationAreaEntity;
import net.mcreator.masterchefrestaurant.client.model.ModelRestaurantArea;

public class LocationAreaRenderer extends MobRenderer<LocationAreaEntity, ModelRestaurantArea<LocationAreaEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("masterchef_restaurant:textures/entities/texturetwoarea.png");

	public LocationAreaRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelRestaurantArea<LocationAreaEntity>(context.bakeLayer(ModelRestaurantArea.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(LocationAreaEntity entity) {
		return entityTexture;
	}
}