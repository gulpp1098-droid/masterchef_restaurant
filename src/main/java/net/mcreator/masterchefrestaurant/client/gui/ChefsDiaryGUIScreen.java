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

import net.mcreator.masterchefrestaurant.world.inventory.ChefsDiaryGUIMenu;
import net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class ChefsDiaryGUIScreen extends AbstractContainerScreen<ChefsDiaryGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private ImageButton imagebutton_base_icon;
	private ImageButton imagebutton_food_icon;
	private ImageButton imagebutton_clients_icon;
	private ImageButton imagebutton_appliences_icon;
	private ImageButton imagebutton_stats_icon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefs_diary_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/chefsdiary2.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/bookmarks.png");
	private static final ResourceLocation IMAGE_2 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_3 = ResourceLocation.parse("masterchef_restaurant:textures/screens/restaurant_icon.png");
	private static final ResourceLocation IMAGE_4 = ResourceLocation.parse("masterchef_restaurant:textures/screens/location_icon.png");
	private static final ResourceLocation IMAGE_5 = ResourceLocation.parse("masterchef_restaurant:textures/screens/requirements_icon.png");
	private static final ResourceLocation IMAGE_6 = ResourceLocation.parse("masterchef_restaurant:textures/screens/open_icon.png");
	private static final ResourceLocation IMAGE_7 = ResourceLocation.parse("masterchef_restaurant:textures/screens/serve_icon.png");
	private static final ResourceLocation IMAGE_8 = ResourceLocation.parse("masterchef_restaurant:textures/screens/coin_icon.png");
	private static final ResourceLocation IMAGE_9 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_10 = ResourceLocation.parse("masterchef_restaurant:textures/screens/separator_icon.png");
	private static final ResourceLocation IMAGE_11 = ResourceLocation.parse("masterchef_restaurant:textures/screens/spatula_icon.png");
	private static final ResourceLocation IMAGE_12 = ResourceLocation.parse("masterchef_restaurant:textures/screens/hammer_icon.png");

	public ChefsDiaryGUIScreen(ChefsDiaryGUIMenu container, Inventory inventory, Component text) {
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
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + -178, this.topPos + -125, 0, 0, 340, 230, 340, 230);
		guiGraphics.blit(IMAGE_1, this.leftPos + 141, this.topPos + -101, 0, 0, 35, 140, 35, 140);
		guiGraphics.blit(IMAGE_2, this.leftPos + -146, this.topPos + -90, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_3, this.leftPos + -145, this.topPos + -81, 0, 0, 39, 32, 39, 32);
		guiGraphics.blit(IMAGE_4, this.leftPos + 10, this.topPos + -33, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_5, this.leftPos + 10, this.topPos + -55, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_6, this.leftPos + 10, this.topPos + 10, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_7, this.leftPos + 10, this.topPos + 32, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_8, this.leftPos + 10, this.topPos + 54, 0, 0, 15, 17, 15, 17);
		guiGraphics.blit(IMAGE_9, this.leftPos + -146, this.topPos + 58, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_10, this.leftPos + 0, this.topPos + -90, 0, 0, 133, 11, 133, 11);
		guiGraphics.blit(IMAGE_11, this.leftPos + 10, this.topPos + -76, 0, 0, 16, 16, 16, 16);
		guiGraphics.blit(IMAGE_12, this.leftPos + 10, this.topPos + -11, 0, 0, 16, 16, 16, 16);
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
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_overview_wip"), -145, -103, -12829636, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_welcome_chef"), -102, -79, -3407872, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.chefs_diary_gui.label_basic_tutorial"), 4, -101, -12829636, false);
		this.guiTools$renderMultilineLabel(guiGraphics, "This diary will guide you through", -102, -69, 96, 22, -12829636, false, 1.00F);
		this.guiTools$renderMultilineLabel(guiGraphics,
				"the basics of running your own restaurant. Your goal is to build, manage and improve a restaurant of your dreams. You will need a proper equipment, place and enough patiance to handle hungry customers.", -144, -46, 135, 110,
				-12829636, false, 1.00F);
	}

	@Override
	public void init() {
		super.init();
		imagebutton_base_icon = new ImageButton(this.leftPos + 147, this.topPos + -98, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/base_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(0, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_base_icon);
		imagebutton_food_icon = new ImageButton(this.leftPos + 148, this.topPos + -68, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/food_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/food_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(1, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 1, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_food_icon);
		imagebutton_clients_icon = new ImageButton(this.leftPos + 149, this.topPos + -39, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/clients_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/clients_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(2, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 2, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_clients_icon);
		imagebutton_appliences_icon = new ImageButton(this.leftPos + 148, this.topPos + -11, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/appliences_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/appliences_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(3, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 3, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_appliences_icon);
		imagebutton_stats_icon = new ImageButton(this.leftPos + 147, this.topPos + 17, 18, 18,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/stats_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/stats_icon.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new ChefsDiaryGUIButtonMessage(4, x, y, z));
						ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 4, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_stats_icon);
		enhanced_image_button_empty = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 30, this.topPos + -77, 78, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage(5, x, y, z));
						net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 5, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed
						? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")
						: mouseOverButton ? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png") : net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Golden Spatula";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty);
		enhanced_image_button_empty_copy = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 30, this.topPos + -55, 83, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage(6, x, y, z));
						net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 6, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed
						? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")
						: mouseOverButton ? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png") : net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Required blocks";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy);
		enhanced_image_button_empty_copy_2 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 30, this.topPos + -33, 104, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage(7, x, y, z));
						net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 7, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed
						? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")
						: mouseOverButton ? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png") : net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Set restaurant area";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_2);
		enhanced_image_button_empty_copy_3 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 30, this.topPos + -12, 91, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage(8, x, y, z));
						net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 8, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed
						? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")
						: mouseOverButton ? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png") : net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Set Up restaurant";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_3);
		enhanced_image_button_empty_copy_4 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 30, this.topPos + 10, 81, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage(9, x, y, z));
						net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 9, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed
						? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")
						: mouseOverButton ? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png") : net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Open restaurant";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_4);
		enhanced_image_button_empty_copy_5 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 30, this.topPos + 32, 82, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage(10, x, y, z));
						net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 10, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed
						? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")
						: mouseOverButton ? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png") : net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Serve Customers";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_5);
		enhanced_image_button_empty_copy_6 = new net.minecraft.client.gui.components.ImageButton(this.leftPos + 30, this.topPos + 54, 70, 20, new net.minecraft.client.gui.components.WidgetSprites(
				net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png"), net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")), e -> {
					int x = ChefsDiaryGUIScreen.this.x;
					int y = ChefsDiaryGUIScreen.this.y;
					if (true) {
						net.neoforged.neoforge.network.PacketDistributor.sendToServer(new net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage(11, x, y, z));
						net.mcreator.masterchefrestaurant.network.ChefsDiaryGUIButtonMessage.handleButtonAction(entity, 11, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				boolean mouseOverButton = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
				boolean mousePressed = mouseOverButton && org.lwjgl.glfw.GLFW.glfwGetMouseButton(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(), org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
				net.minecraft.resources.ResourceLocation buttonTexture = mousePressed
						? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png")
						: mouseOverButton ? net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png") : net.minecraft.resources.ResourceLocation.parse("masterchef_restaurant:textures/screens/empty.png");
				guiGraphics.blit(buttonTexture, getX(), getY(), 0, 0, width, height, width, height);
				String guiTools$buttonText = "Exp and coins";
				if (!guiTools$buttonText.isEmpty()) {
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(getX() + width / 2.0, getY() + height / 2.0, 0);
					guiGraphics.pose().scale(1.0f, 1.0f, 1.0f);
					guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, guiTools$buttonText, -net.minecraft.client.Minecraft.getInstance().font.width(guiTools$buttonText) / 2,
							-net.minecraft.client.Minecraft.getInstance().font.lineHeight / 2, -13421773, false);
					guiGraphics.pose().popPose();
				}
			}
		};
		this.addRenderableWidget(enhanced_image_button_empty_copy_6);
	}

	private final java.util.Map<String, java.util.List<String>> guiTools$multilineCache = new java.util.HashMap<>();

	private void guiTools$renderMultilineLabel(GuiGraphics guiGraphics, String text, int x, int y, int boxWidth, int boxHeight, int color, boolean shadow, float scale) {
		if (text == null || scale <= 0.0F || boxWidth <= 0 || boxHeight <= 0)
			return;
		int wrapWidth = Math.max(1, (int) Math.floor(boxWidth / scale));
		int lineStep = this.font.lineHeight + 1;
		int currentY = 0;
		java.util.List<String> lines = this.guiTools$multilineCache.computeIfAbsent(text + "\u0000" + wrapWidth, key -> this.guiTools$wrapMultilineText(text, wrapWidth));
		if (this.guiTools$multilineCache.size() > 64)
			this.guiTools$multilineCache.clear();
		guiGraphics.pose().pushPose();
		try {
			guiGraphics.pose().translate(x, y, 0.0F);
			guiGraphics.pose().scale(scale, scale, 1.0F);
			for (String line : lines) {
				guiGraphics.drawString(this.font, line, 0, currentY, color, shadow);
				currentY += lineStep;
			}
		} finally {
			guiGraphics.pose().popPose();
		}
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
}