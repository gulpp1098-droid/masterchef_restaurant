package net.mcreator.masterchefrestaurant.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.masterchefrestaurant.entity.ChairMobEntity;
import net.mcreator.masterchefrestaurant.client.model.ModelChairMob;

public class ChairMobRenderer extends MobRenderer<ChairMobEntity, ModelChairMob<ChairMobEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("masterchef_restaurant:textures/entities/chairmob.png");

	public ChairMobRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelChairMob<ChairMobEntity>(context.bakeLayer(ModelChairMob.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(ChairMobEntity entity) {
		if (entity.getTexture() != "chairmob")
			return ResourceLocation.parse("masterchef_restaurant:textures/entities/" + entity.getTexture() + ".png");
		return entityTexture;
	}

	@Override
	protected boolean isBodyVisible(ChairMobEntity entity) {
		return false;
	}
}