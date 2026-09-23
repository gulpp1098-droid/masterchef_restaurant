package net.mcreator.masterchefrestaurant.client.gui;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.masterchefrestaurant.world.inventory.ChefsDiaryFoodGUIMenu;
import net.mcreator.masterchefrestaurant.network.ChefsDiaryFoodGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryFoodGUIScreen extends AbstractContainerScreen<ChefsDiaryFoodGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefs_diary_food_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/bookmarks.png");

	public ChefsDiaryFoodGUIScreen(ChefsDiaryFoodGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 0;
		this.imageHeight = 0;
	}

	@Override
	public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;
		menuStateUpdateActive = false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiTools$alphaBlit(guiGraphics, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiTools$orderedImages : {
			guiTools$alphaBlit(guiGraphics, IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
			guiTools$alphaBlit(guiGraphics, IMAGE_1, this.leftPos + 141, this.topPos + -101, 0, 0, 35, 140, 35, 140);
			if (this.enhanced_image_button_empty != null && this.enhanced_image_button_empty.visible) {
				this.enhanced_image_button_empty.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy != null && this.enhanced_image_button_empty_copy.visible) {
				this.enhanced_image_button_empty_copy.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy_2 != null && this.enhanced_image_button_empty_copy_2.visible) {
				this.enhanced_image_button_empty_copy_2.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy_3 != null && this.enhanced_image_button_empty_copy_3.visible) {
				this.enhanced_image_button_empty_copy_3.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy_4 != null && this.enhanced_image_button_empty_copy_4.visible) {
				this.enhanced_image_button_empty_copy_4.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy_5 != null && this.enhanced_image_button_empty_copy_5.visible) {
				this.enhanced_image_button_empty_copy_5.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy_6 != null && this.enhanced_image_button_empty_copy_6.visible) {
				this.enhanced_image_button_empty_copy_6.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy_7 != null && this.enhanced_image_button_empty_copy_7.visible) {
				this.enhanced_image_button_empty_copy_7.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy_8 != null && this.enhanced_image_button_empty_copy_8.visible) {
				this.enhanced_image_button_empty_copy_8.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
			if (this.enhanced_image_button_empty_copy_9 != null && this.enhanced_image_button_empty_copy_9.visible) {
				this.enhanced_image_button_empty_copy_9.render(guiGraphics, mouseX, mouseY, partialTicks);
			}
		}
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_food_gui.label_food_wip"), -145, -98, -12829636, false);
		this.guiTools$renderMultilineLabel(guiGraphics,
				"Food is categoried into multiple tiers. Each tier is unlocked with leveling up your restaurant to certain level. Each tier represents your journey as chef. The more skill you have, the more new food you can discover and serve to your clients. Of course with tier comes also better rewards!",
				-145, -87, 126, 155, -12829636, false, 1.00F, 0, 0);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodGUIButtonMessage(0, x, y, z));
						ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_base_icon);
		imagebutton_food_icon = new ImageButton(this.leftPos + 148, this.topPos + -68, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/food_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/food_icon.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodGUIButtonMessage(1, x, y, z));
						ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_food_icon);
		imagebutton_clients_icon = new ImageButton(this.leftPos + 149, this.topPos + -39, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/clients_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/clients_icon.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodGUIButtonMessage(2, x, y, z));
						ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_clients_icon);
		imagebutton_appliences_icon = new ImageButton(this.leftPos + 148, this.topPos + -11, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/appliences_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/appliences_icon.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodGUIButtonMessage(3, x, y, z));
						ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_appliences_icon);
		imagebutton_stats_icon = new ImageButton(this.leftPos + 147, this.topPos + 17, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/stats_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/stats_icon.png")), e -> {
					int x = ChefsDiaryFoodGUIScreen.this.x;
					int y = ChefsDiaryFoodGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryFoodGUIButtonMessage(4, x, y, z));
						ChefsDiaryFoodGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiTools$alphaBlit(guiGraphics, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
		enhanced_image_button_empty = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + -92, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 0";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty);
		enhanced_image_button_empty_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + -76, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 1";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy);
		enhanced_image_button_empty_copy_2 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + -60, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 2";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy_2);
		enhanced_image_button_empty_copy_3 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + -45, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 3";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy_3);
		enhanced_image_button_empty_copy_4 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + -29, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 4";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy_4);
		enhanced_image_button_empty_copy_5 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + -13, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 5";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy_5);
		enhanced_image_button_empty_copy_6 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + 3, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 6";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy_6);
		enhanced_image_button_empty_copy_7 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + 18, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 7";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy_7);
		enhanced_image_button_empty_copy_8 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + 34, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 8";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy_8);
		enhanced_image_button_empty_copy_9 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 54, this.topPos + 50, 18, 18, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				net.minecraft.resources.ResourceLocation guiTools$normalTexture = net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				net.minecraft.resources.ResourceLocation guiTools$hoveredTexture = guiTools$normalTexture;
				net.minecraft.resources.ResourceLocation guiTools$pressedTexture = guiTools$hoveredTexture;
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed ? guiTools$pressedTexture : mouseOverButton ? guiTools$hoveredTexture : guiTools$normalTexture;
				guiTools$alphaBlit(guiGraphics, buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Tier 9";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -1, true);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addWidget(enhanced_image_button_empty_copy_9);
	}

	private final java.util.Map<String, java.util.List<java.util.List<String>>> guiTools$multilineCache = new java.util.HashMap<>();

	private void guiTools$renderMultilineLabel(GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int boxHeight, int color, boolean shadow, float scale, int overflowMode, int alignment) {
		if (text == null || scale <= 0.0F || boxWidth <= 0 || boxHeight <= 0)
			return;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int contentHeight = Math.max(0, (int) Math.floor(boxHeight / scale));
		int lineStep = this.font.lineHeight + 1;
		int maxLines = contentHeight < this.font.lineHeight ? 0 : 1 + (contentHeight - this.font.lineHeight) / lineStep;
		String cacheKey = text + "\u0000" + wrapWidth + "\u0000" + maxLines + "\u0000" + overflowMode;
		java.util.List<java.util.List<String>> paragraphs = this.guiTools$multilineCache.computeIfAbsent(cacheKey,
				key -> java.util.Arrays.stream(text.replace("\r", "").split("\n", -1)).map(paragraph -> this.guiTools$wrapMultilineText(paragraph, wrapWidth)).toList());
		if (this.guiTools$multilineCache.size() > 64)
			this.guiTools$multilineCache.clear();
		boolean clip = overflowMode != 0;
		if (clip)
			guiGraphics.enableScissor(this.leftPos + x, this.topPos + y, this.leftPos + x + boxWidth, this.topPos + y + boxHeight);
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.pose().translate(x, y, 0.0F);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			int currentY = 0;
			for (java.util.List<String> lines : paragraphs) {
				for (int index = 0; index < lines.size(); index++) {
					String line = lines.get(index);
					int remaining = wrapWidth - this.font.width(line);
					if (alignment == 3 && index < lines.size() - 1 && remaining > 0 && line.contains(" ")) {
						String[] words = line.split(" ");
						int advance = 0;
						for (int word = 0; word < words.length; word++) {
							int currentX = advance + (int) Math.round((double) remaining * word / (words.length - 1));
							guiGraphics.drawString(this.font, words[word], currentX, currentY, color, shadow);
							advance += this.font.width(words[word] + " ");
						}
					} else {
						int currentX = alignment == 1 ? remaining : alignment == 2 ? remaining / 2 : 0;
						guiGraphics.drawString(this.font, line, currentX, currentY, color, shadow);
					}
					currentY += lineStep;
				}
			}
		} finally {
			guiGraphics.pose().popPose();
			if (clip)
				guiGraphics.disableScissor();
		}
	}

	private boolean guiTools$isMultilineTruncated(String text, int boxWidth, int boxHeight, float scale, int overflowMode) {
		if (text == null || overflowMode == 0 || scale <= 0.0F)
			return false;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int contentHeight = Math.max(0, (int) Math.floor(boxHeight / scale));
		java.util.List<String> lines = this.guiTools$wrapMultilineText(text, wrapWidth);
		for (String line : lines)
			if (this.font.width(line) > wrapWidth)
				return true;
		return !lines.isEmpty() && this.font.lineHeight + (lines.size() - 1) * (this.font.lineHeight + 1) > contentHeight;
	}

	private java.util.List<String> guiTools$displayMultilineText(String text, int wrapWidth, int maxLines, int overflowMode) {
		java.util.List<String> wrapped = this.guiTools$wrapMultilineText(text, wrapWidth);
		if (overflowMode != 2)
			return wrapped;
		if (maxLines <= 0)
			return java.util.List.of();
		boolean verticalOverflow = wrapped.size() > maxLines;
		java.util.List<String> result = new java.util.ArrayList<>();
		for (int index = 0; index < Math.min(maxLines, wrapped.size()); index++) {
			result.add(this.guiTools$ellipsize(wrapped.get(index), wrapWidth, verticalOverflow && index == maxLines - 1));
		}
		return java.util.List.copyOf(result);
	}

	private String guiTools$ellipsize(String value, int maxWidth, boolean forceEllipsis) {
		if (!forceEllipsis && this.font.width(value) <= maxWidth)
			return value;
		String ellipsis = "…";
		if (this.font.width(ellipsis) > maxWidth)
			return "";
		int low = 0, high = value.length();
		while (low < high) {
			int middle = (low + high + 1) >>> 1;
			if (this.font.width(value.substring(0, middle) + ellipsis) <= maxWidth)
				low = middle;
			else
				high = middle - 1;
		}
		return value.substring(0, low).stripTrailing() + ellipsis;
	}

	private java.util.List<String> guiTools$wrapMultilineText(String text, int wrapWidth) {
		java.util.List<String> lines = new java.util.ArrayList<>();
		for (String paragraph : text.replace("\r", "").split("\n", -1)) {
			if (paragraph.isEmpty()) {
				lines.add("");
				continue;
			}
			StringBuilder line = new StringBuilder();
			for (String word : paragraph.split("\s+")) {
				String candidate = line.isEmpty() ? word : line + " " + word;
				if (!line.isEmpty() && this.font.width(candidate) > wrapWidth) {
					lines.add(line.toString());
					line.setLength(0);
					line.append(word);
				} else {
					line.setLength(0);
					line.append(candidate);
				}
			}
			lines.add(line.toString());
		}
		return java.util.List.copyOf(lines);
	}

	private static final boolean guiTools$enhancedImageButton = true;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_2;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_3;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_4;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_5;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_6;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_7;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_8;
	private net.minecraft.client.gui.components.ImageButton enhanced_image_button_empty_copy_9;

	private static net.minecraft.resources.ResourceLocation guiTools$buttonTexture(String value, net.minecraft.resources.ResourceLocation fallback) {
		if (value == null || value.isBlank())
			return fallback;
		try {
			String texture = value.trim().replace('\\', '/');
			if (texture.indexOf(':') >= 0)
				return net.minecraft.resources.ResourceLocation.parse(texture);
			while (texture.startsWith("/"))
				texture = texture.substring(1);
			if (texture.startsWith("textures/screens/"))
				texture = texture.substring("textures/screens/".length());
			if (!texture.endsWith(".png"))
				texture += ".png";
			return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("masterchef_restaurant", "textures/screens/" + texture);
		} catch (RuntimeException ignored) {
			return fallback;
		}
	}

	private static void guiTools$alphaBlit(net.minecraft.client.gui.GuiGraphics graphics, net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		boolean wasBlending = org.lwjgl.opengl.GL11.glIsEnabled(org.lwjgl.opengl.GL11.GL_BLEND);
		com.mojang.blaze3d.systems.RenderSystem.enableBlend();
		com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
		try {
			graphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
		} finally {
			if (!wasBlending)
				com.mojang.blaze3d.systems.RenderSystem.disableBlend();
		}
	}
}