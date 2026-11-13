package net.mcreator.omnichef.client.model;

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

// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports
public class Modelchefs_chestplate<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("omnichef", "modelchefs_chestplate"), "main");
	public final ModelPart Waist;
	public final ModelPart Body;
	public final ModelPart right_arm;
	public final ModelPart left_arm;

	public Modelchefs_chestplate(ModelPart root) {
		this.Waist = root.getChild("Waist");
		this.Body = this.Waist.getChild("Body");
		this.right_arm = this.Waist.getChild("right_arm");
		this.left_arm = this.Waist.getChild("left_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition Waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
		PartDefinition Body = Waist.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -0.5F, -2.4F, 9.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));
		PartDefinition right_arm = Waist.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(2, 19).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.0F, -10.0F, 0.0F));
		PartDefinition left_arm = Waist.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 19).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(5.0F, -10.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int rgb) {
		Waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, rgb);
	}
}