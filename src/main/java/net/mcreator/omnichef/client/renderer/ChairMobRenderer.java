package net.mcreator.omnichef.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.omnichef.entity.ChairMobEntity;
import net.mcreator.omnichef.client.model.ModelChairMob;

public class ChairMobRenderer extends MobRenderer<ChairMobEntity, ModelChairMob<ChairMobEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("omnichef:textures/entities/chairmob.png");

	public ChairMobRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelChairMob<ChairMobEntity>(context.bakeLayer(ModelChairMob.LAYER_LOCATION)), 0f);
	}

	@Override
	public ResourceLocation getTextureLocation(ChairMobEntity entity) {
		if (entity.getTexture() != "chairmob")
			return ResourceLocation.parse("omnichef:textures/entities/" + entity.getTexture() + ".png");
		return entityTexture;
	}

	@Override
	protected boolean isBodyVisible(ChairMobEntity entity) {
		return false;
	}
}