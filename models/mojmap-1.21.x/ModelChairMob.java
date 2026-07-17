// Made with Blockbench 5.0.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelChairMob<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "chairmob"), "main");
	private final ModelPart Chair;
	private final ModelPart Support;
	private final ModelPart Support2;
	private final ModelPart Desks;
	private final ModelPart Legs;
	private final ModelPart Sit;

	public ModelChairMob(ModelPart root) {
		this.Chair = root.getChild("Chair");
		this.Support = this.Chair.getChild("Support");
		this.Support2 = this.Support.getChild("Support2");
		this.Desks = this.Support.getChild("Desks");
		this.Legs = this.Chair.getChild("Legs");
		this.Sit = this.Chair.getChild("Sit");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Chair = partdefinition.addOrReplaceChild("Chair", CubeListBuilder.create(),
				PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition Support = Chair.addOrReplaceChild("Support", CubeListBuilder.create(),
				PartPose.offset(7.0F, 2.0F, -7.0F));

		PartDefinition Support2 = Support.addOrReplaceChild("Support2", CubeListBuilder.create().texOffs(0, 15).addBox(
				-14.0F, -9.0F, 11.0F, 14.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Desks = Support.addOrReplaceChild("Desks",
				CubeListBuilder.create().texOffs(0, 28)
						.addBox(-3.0F, -8.0F, 12.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(4, 28)
						.addBox(-12.0F, -8.0F, 12.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Legs = Chair.addOrReplaceChild("Legs",
				CubeListBuilder.create().texOffs(0, 20)
						.addBox(-4.0F, -6.0F, 12.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(8, 20)
						.addBox(-4.0F, -6.0F, 2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(16, 20)
						.addBox(-14.0F, -6.0F, 12.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(24, 20)
						.addBox(-14.0F, -6.0F, 2.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(8.0F, 8.0F, -8.0F));

		PartDefinition Sit = Chair.addOrReplaceChild("Sit", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, 1.0F,
				-7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Chair.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}