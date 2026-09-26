package net.mcreator.omnichef.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.omnichef.entity.LocationAreaEntity;
import net.mcreator.omnichef.client.model.ModelRestaurantArea;

public class LocationAreaRenderer extends MobRenderer<LocationAreaEntity, ModelRestaurantArea<LocationAreaEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("omnichef:textures/entities/texturetwoarea.png");

	public LocationAreaRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelRestaurantArea<LocationAreaEntity>(context.bakeLayer(ModelRestaurantArea.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(LocationAreaEntity entity) {
		if (entity.getTexture() != "texturetwoarea")
			return ResourceLocation.parse("omnichef:textures/entities/" + entity.getTexture() + ".png");
		return entityTexture;
	}
}