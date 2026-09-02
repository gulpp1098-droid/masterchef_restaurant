// Made with Blockbench 5.1.6
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelClientNewV2<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "clientnewv2"), "main");
	private final ModelPart Body;
	private final ModelPart Legs;
	private final ModelPart Rightleg;
	private final ModelPart Leftleg;
	private final ModelPart UpperBody;
	private final ModelPart Head;
	private final ModelPart Hair;
	private final ModelPart Hands;
	private final ModelPart Torso;
	private final ModelPart Lefthand;
	private final ModelPart Righthand;

	public ModelClientNewV2(ModelPart root) {
		this.Body = root.getChild("Body");
		this.Legs = this.Body.getChild("Legs");
		this.Rightleg = this.Legs.getChild("Rightleg");
		this.Leftleg = this.Legs.getChild("Leftleg");
		this.UpperBody = this.Body.getChild("UpperBody");
		this.Head = this.UpperBody.getChild("Head");
		this.Hair = this.Head.getChild("Hair");
		this.Hands = this.UpperBody.getChild("Hands");
		this.Torso = this.Hands.getChild("Torso");
		this.Lefthand = this.Hands.getChild("Lefthand");
		this.Righthand = this.Hands.getChild("Righthand");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition Legs = Body.addOrReplaceChild("Legs", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Rightleg = Legs.addOrReplaceChild("Rightleg",
				CubeListBuilder.create().texOffs(64, 62)
						.addBox(-10.5F, -2.0F, -3.5F, 10.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(60, 77)
						.addBox(-9.5F, -3.0F, -2.5F, 9.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(5.5F, -2.0F, 4.5F));

		PartDefinition Leftleg = Legs.addOrReplaceChild("Leftleg",
				CubeListBuilder.create().texOffs(64, 52)
						.addBox(-10.5F, -2.0F, -2.5F, 10.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(60, 72)
						.addBox(-9.5F, -3.0F, -1.5F, 9.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(5.5F, -2.0F, -4.5F));

		PartDefinition UpperBody = Body.addOrReplaceChild("UpperBody", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition Head = UpperBody.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(64, 36).addBox(-4.0F,
				-4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -28.0F, 0.0F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-4.5F, -6.0F, -8.0F, 0.0F, 20.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(0, 36)
						.addBox(4.5F, -6.0F, -8.0F, 0.0F, 20.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = Hair.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(32, 36).addBox(0.5F, -5.0F, -8.0F, 0.0F, 20.0F, 16.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -1.0F, 4.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r2 = Hair.addOrReplaceChild("cube_r2",
				CubeListBuilder.create().texOffs(32, 0).addBox(0.5F, -5.0F, -8.0F, 0.0F, 20.0F, 16.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -1.0F, -5.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition Hands = UpperBody.addOrReplaceChild("Hands", CubeListBuilder.create(),
				PartPose.offset(2.0F, 0.0F, 0.0F));

		PartDefinition Torso = Hands.addOrReplaceChild("Torso",
				CubeListBuilder.create().texOffs(64, 15)
						.addBox(-2.0F, -23.0F, -4.0F, 6.0F, 13.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(64, 0)
						.addBox(-3.0F, -10.0F, -5.0F, 8.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 72)
						.addBox(-2.0F, -5.0F, -4.0F, 6.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(20, 81)
						.addBox(0.0F, -24.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-2.0F, 0.0F, 0.0F));

		PartDefinition Lefthand = Hands.addOrReplaceChild("Lefthand", CubeListBuilder.create(),
				PartPose.offset(-1.0F, -24.0F, -3.0F));

		PartDefinition cube_r3 = Lefthand.addOrReplaceChild("cube_r3",
				CubeListBuilder.create().texOffs(0, 81).addBox(-1.5F, -2.0F, -3.0F, 5.0F, 2.0F, 5.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, 13.0F, -4.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r4 = Lefthand.addOrReplaceChild("cube_r4",
				CubeListBuilder.create().texOffs(28, 72).addBox(-2.0F, 0.0084F, -3.9939F, 4.0F, 14.0F, 4.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition Righthand = Hands.addOrReplaceChild("Righthand", CubeListBuilder.create(),
				PartPose.offset(-1.0F, -23.0F, 4.0F));

		PartDefinition cube_r5 = Righthand
				.addOrReplaceChild("cube_r5",
						CubeListBuilder.create().texOffs(44, 72).addBox(-2.0F, 0.0084F, 0.0061F, 4.0F, 14.0F, 4.0F,
								new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r6 = Righthand.addOrReplaceChild("cube_r6",
				CubeListBuilder.create().texOffs(60, 82).addBox(-1.5F, -2.0F, -3.0F, 5.0F, 2.0F, 5.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, 12.0F, 4.0F, 0.1309F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}