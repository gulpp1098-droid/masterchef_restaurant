package net.mcreator.masterchefrestaurant.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;

import net.mcreator.masterchefrestaurant.entity.ClientEntity;
import net.mcreator.masterchefrestaurant.client.model.animations.ClientNewAnimation;
import net.mcreator.masterchefrestaurant.client.model.ModelClientNew;

public class ClientRenderer extends MobRenderer<ClientEntity, ModelClientNew<ClientEntity>> {
	private final ResourceLocation entityTexture = ResourceLocation.parse("masterchef_restaurant:textures/entities/textureclientnew.png");

	public ClientRenderer(EntityRendererProvider.Context context) {
		super(context, new AnimatedModel(context.bakeLayer(ModelClientNew.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(ClientEntity entity) {
		if (entity.getTexture() != "textureclientnew")
			return ResourceLocation.parse("masterchef_restaurant:textures/entities/" + entity.getTexture() + ".png");
		return entityTexture;
	}

	private static final class AnimatedModel extends ModelClientNew<ClientEntity> {
		private final ModelPart root;
		private final HierarchicalModel animator = new HierarchicalModel<ClientEntity>() {
			@Override
			public ModelPart root() {
				return root;
			}

			@Override
			public void setupAnim(ClientEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
				this.root().getAllParts().forEach(ModelPart::resetPose);
				this.animate(entity.animationState0, ClientNewAnimation.Walk, ageInTicks, 1f);
				this.animate(entity.animationState1, ClientNewAnimation.Sit, ageInTicks, 1.5f);
				this.animate(entity.animationState2, ClientNewAnimation.Idle, ageInTicks, 1f);
			}
		};

		public AnimatedModel(ModelPart root) {
			super(root);
			this.root = root;
		}

		@Override
		public void setupAnim(ClientEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
			animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}
	}
}