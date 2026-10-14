package net.mcreator.omnichef.procedures;

import org.joml.Matrix4f;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;

import net.mcreator.omnichef.network.OmnichefModVariables;
import net.mcreator.omnichef.init.OmnichefModItems;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;

@EventBusSubscriber(value = Dist.CLIENT)
public class RenderRestaurantBoundaryProcedure {
	private static BufferBuilder bufferBuilder = null;
	private static VertexBuffer vertexBuffer = null;
	private static VertexFormat.Mode mode = null;
	private static VertexFormat format = null;
	private static PoseStack poseStack = null;
	private static Matrix4f modelViewMatrix = null;
	private static Matrix4f projectionMatrix = null;
	private static boolean worldCoordinate = true;
	private static Vec3 offset = Vec3.ZERO;
	private static int currentStage = 0;
	private static int targetStage = 0; // NONE: 0, SKY: 1, WORLD: 2

	private static void add(float x, float y, float z, int color) {
		add(x, y, z, 0.0F, 0.0F, color);
	}

	private static void add(float x, float y, float z, float u, float v, int color) {
		if (bufferBuilder == null)
			return;
		if (format == DefaultVertexFormat.POSITION_COLOR) {
			bufferBuilder.addVertex(x, y, z).setColor(color);
		} else if (format == DefaultVertexFormat.POSITION_TEX_COLOR) {
			bufferBuilder.addVertex(x, y, z).setUv(u, v).setColor(color);
		}
	}

	private static boolean begin(VertexFormat.Mode mode, VertexFormat format, boolean update) {
		if (RenderRestaurantBoundaryProcedure.bufferBuilder == null) {
			if (update)
				clear();
			if (RenderRestaurantBoundaryProcedure.vertexBuffer == null) {
				if (format == DefaultVertexFormat.POSITION_COLOR) {
					RenderRestaurantBoundaryProcedure.mode = mode;
					RenderRestaurantBoundaryProcedure.format = format;
					RenderRestaurantBoundaryProcedure.bufferBuilder = Tesselator.getInstance().begin(mode, DefaultVertexFormat.POSITION_COLOR);
					return true;
				} else if (format == DefaultVertexFormat.POSITION_TEX_COLOR) {
					RenderRestaurantBoundaryProcedure.mode = mode;
					RenderRestaurantBoundaryProcedure.format = format;
					RenderRestaurantBoundaryProcedure.bufferBuilder = Tesselator.getInstance().begin(mode, DefaultVertexFormat.POSITION_TEX_COLOR);
					return true;
				}
			}
		}
		return false;
	}

	private static void clear() {
		if (vertexBuffer != null) {
			vertexBuffer.close();
			vertexBuffer = null;
		}
	}

	private static void end() {
		if (bufferBuilder == null)
			return;
		if (vertexBuffer != null)
			vertexBuffer.close();
		MeshData meshData = bufferBuilder.build();
		if (meshData == null) {
			vertexBuffer = null;
			bufferBuilder = null;
		} else {
			vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
			vertexBuffer.bind();
			vertexBuffer.upload(meshData);
			VertexBuffer.unbind();
			bufferBuilder = null;
		}
	}

	private static void offset(double x, double y, double z) {
		offset = new Vec3(x, y, z);
	}

	private static void release() {
		targetStage = 0;
	}

	private static VertexBuffer shape() {
		return vertexBuffer;
	}

	private static void system(boolean worldCoordinate) {
		RenderRestaurantBoundaryProcedure.worldCoordinate = worldCoordinate;
	}

	private static boolean target(int targetStage) {
		if (targetStage == currentStage) {
			RenderRestaurantBoundaryProcedure.targetStage = targetStage;
			return true;
		}
		return false;
	}

	private static void renderShape(VertexBuffer vertexBuffer, double x, double y, double z, float yaw, float pitch, float roll, float xScale, float yScale, float zScale, int color) {
		if (currentStage == 0 || currentStage != targetStage)
			return;
		if (poseStack == null || projectionMatrix == null)
			return;
		if (vertexBuffer == null)
			return;
		float i, j, k;
		if (worldCoordinate) {
			Vec3 pos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
			i = (float) (x - pos.x());
			j = (float) (y - pos.y());
			k = (float) (z - pos.z());
		} else {
			i = (float) x;
			j = (float) y;
			k = (float) z;
		}
		poseStack.pushPose();
		poseStack.mulPose(modelViewMatrix);
		poseStack.translate(i, j, k);
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.scale(xScale, yScale, zScale);
		poseStack.translate(offset.x(), offset.y(), offset.z());
		RenderSystem.setShaderColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, (color >>> 24) / 255.0F);
		vertexBuffer.bind();
		vertexBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, vertexBuffer.getFormat().hasUV(0) ? GameRenderer.getPositionTexColorShader() : GameRenderer.getPositionColorShader());
		VertexBuffer.unbind();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

	@SubscribeEvent
	public static void renderLevel(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
			currentStage = 1;
			poseStack = new PoseStack();
			RenderSystem.depthMask(false);
			renderShapes(event);
			RenderSystem.enableCull();
			RenderSystem.depthMask(true);
			currentStage = 0;
		} else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			currentStage = 2;
			poseStack = event.getPoseStack();
			RenderSystem.depthMask(true);
			renderShapes(event);
			RenderSystem.enableCull();
			RenderSystem.depthMask(true);
			currentStage = 0;
		}
	}

	private static void renderShapes(RenderLevelStageEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
		if (level != null && entity != null) {
			modelViewMatrix = event.getModelViewMatrix();
			projectionMatrix = event.getProjectionMatrix();
			Vec3 pos = entity.getPosition(event.getPartialTick().getGameTimeDeltaPartialTick(false));
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			execute(event, entity);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.defaultBlendFunc();
			RenderSystem.disableBlend();
			RenderSystem.enableDepthTest();
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		String RemainingData = "";
		String WallData = "";
		double SeparatorIndex = 0;
		double WallX = 0;
		double WallYaw = 0;
		double WallZ = 0;
		if (begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR, false)) {
			add((float) (-0.5), 0, 0, 0, 0, 255 << 24 | 255 << 16 | 255 << 8 | 255);
			add((float) 0.5, 0, 0, 0, 0, 255 << 24 | 255 << 16 | 255 << 8 | 255);
			add((float) 0.5, 1, 0, 0, 0, 255 << 24 | 255 << 16 | 255 << 8 | 255);
			add((float) (-0.5), 1, 0, 0, 0, 255 << 24 | 255 << 16 | 255 << 8 | 255);
			end();
		}
		if (target(2)) {
			system(true);
			RenderSystem.disableCull();
			RenderSystem.depthMask(false);
			RenderSystem.enableDepthTest();
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == OmnichefModItems.SPATULA_GOLDEN.get()
					&& (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("ShowRestaurantArea")) {
				RemainingData = entity.getData(OmnichefModVariables.PLAYER_VARIABLES).RestaurantBoundaryData;
				while (RemainingData.contains(";")) {
					SeparatorIndex = RemainingData.indexOf(";");
					WallData = RemainingData.substring(0, (int) SeparatorIndex);
					RemainingData = RemainingData.substring((int) (SeparatorIndex + 1), (RemainingData).length());
					WallX = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(0, WallData));
					WallZ = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(1, WallData));
					WallYaw = new Object() {
						double convert(String s) {
							try {
								return Double.parseDouble(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert(GetPartFromStringProcedure.execute(2, WallData));
					renderShape(shape(), WallX, (-64), WallZ, (float) WallYaw, 0, 0, 5, 384, 1, 120 << 24 | 80 << 16 | 200 << 8 | 220);
				}
			}
			RenderSystem.enableCull();
			RenderSystem.depthMask(true);
			release();
		}
	}
}