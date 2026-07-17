package net.mcreator.masterchefrestaurant.client.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.EntityModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class ModelClientNew<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("masterchef_restaurant", "model_client_new"), "main");
	public final ModelPart Body;
	public final ModelPart Legs;
	public final ModelPart Rightleg;
	public final ModelPart Leftleg;
	public final ModelPart UpperBody;
	public final ModelPart Head;
	public final ModelPart SunGlasses;
	public final ModelPart Torso;
	public final ModelPart Hips;
	public final ModelPart Chest;
	public final ModelPart Hands;
	public final ModelPart Lefthand;
	public final ModelPart Righthand;

	public ModelClientNew(ModelPart root) {
		this.Body = root.getChild("Body");
		this.Legs = this.Body.getChild("Legs");
		this.Rightleg = this.Legs.getChild("Rightleg");
		this.Leftleg = this.Legs.getChild("Leftleg");
		this.UpperBody = this.Body.getChild("UpperBody");
		this.Head = this.UpperBody.getChild("Head");
		this.SunGlasses = this.Head.getChild("SunGlasses");
		this.Torso = this.UpperBody.getChild("Torso");
		this.Hips = this.Torso.getChild("Hips");
		this.Chest = this.Torso.getChild("Chest");
		this.Hands = this.UpperBody.getChild("Hands");
		this.Lefthand = this.Hands.getChild("Lefthand");
		this.Righthand = this.Hands.getChild("Righthand");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
		PartDefinition Legs = Body.addOrReplaceChild("Legs", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition Rightleg = Legs.addOrReplaceChild("Rightleg", CubeListBuilder.create().texOffs(32, 0).addBox(-9.5F, -2.0F, -2.5F, 9.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.5F, -2.0F, 4.5F));
		PartDefinition Leftleg = Legs.addOrReplaceChild("Leftleg", CubeListBuilder.create().texOffs(24, 31).addBox(-9.5F, -2.0F, -2.5F, 9.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.5F, -2.0F, -4.5F));
		PartDefinition UpperBody = Body.addOrReplaceChild("UpperBody", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition Head = UpperBody.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -28.0F, 0.0F));
		PartDefinition SunGlasses = Head.addOrReplaceChild("SunGlasses", CubeListBuilder.create().texOffs(40, 47).addBox(-4.5F, -6.0F, -4.5F, 0.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition cube_r1 = SunGlasses.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 39).addBox(-0.5F, -3.5F, -4.5F, 0.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -1.5F, 4.0F, 0.0F, 1.5708F, 0.0F));
		PartDefinition cube_r2 = SunGlasses.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 39).addBox(-0.5F, -3.5F, -4.5F, 0.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -1.5F, -5.0F, 0.0F, 1.5708F, 0.0F));
		PartDefinition cube_r3 = SunGlasses.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 41).addBox(0.0F, -5.0F, -4.5F, 0.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-0.5F, -4.5F, 0.0F, 0.0F, 0.0F, 1.5708F));
		PartDefinition Torso = UpperBody.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition Hips = Torso.addOrReplaceChild("Hips",
				CubeListBuilder.create().texOffs(40, 40).addBox(-2.0F, -5.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 16).addBox(-3.0F, -12.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition Chest = Torso.addOrReplaceChild("Chest", CubeListBuilder.create().texOffs(0, 31).addBox(-2.0F, -24.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition Hands = UpperBody.addOrReplaceChild("Hands", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 0.0F));
		PartDefinition Lefthand = Hands.addOrReplaceChild("Lefthand", CubeListBuilder.create(), PartPose.offset(-1.0F, -24.0F, -3.0F));
		PartDefinition cube_r4 = Lefthand.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(44, 23).addBox(-1.5F, -2.0F, -3.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, 12.0F, -3.0F, -0.1309F, 0.0F, 0.0F));
		PartDefinition cube_r5 = Lefthand.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 9).addBox(-2.0F, 0.0084F, -3.9939F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.0F, 0.0F));
		PartDefinition Righthand = Hands.addOrReplaceChild("Righthand", CubeListBuilder.create(), PartPose.offset(-1.0F, -24.0F, 3.0F));
		PartDefinition cube_r6 = Righthand.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(24, 40).addBox(-2.0F, 0.0084F, 0.0061F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));
		PartDefinition cube_r7 = Righthand.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(44, 23).addBox(-1.5F, -2.0F, -3.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, 12.0F, 4.0F, 0.1309F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
	}
}