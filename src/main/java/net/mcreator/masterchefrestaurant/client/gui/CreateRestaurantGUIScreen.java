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
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

import net.mcreator.masterchefrestaurant.world.inventory.CreateRestaurantGUIMenu;
import net.mcreator.masterchefrestaurant.network.CreateRestaurantGUIButtonMessage;
import net.mcreator.masterchefrestaurant.init.MasterchefRestaurantModScreens;

import com.mojang.blaze3d.systems.RenderSystem;

public class CreateRestaurantGUIScreen extends AbstractContainerScreen<CreateRestaurantGUIMenu> implements MasterchefRestaurantModScreens.ScreenAccessor {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	private boolean menuStateUpdateActive = false;
	private EditBox Restaurant_Name;
	private ImageButton imagebutton_checkboxon;
	private static final ResourceLocation BACKGROUND = ResourceLocation.parse("masterchef_restaurant:textures/screens/create_restaurant_gui.png");
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("masterchef_restaurant:textures/screens/newrestaurantgui.png");
	private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("masterchef_restaurant:textures/screens/ribbon_icon.png");

	public CreateRestaurantGUIScreen(CreateRestaurantGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 109;
		this.imageHeight = 64;
	}

	@Override
	public void updateMenuState(int elementType, String name, Object elementState) {
		menuStateUpdateActive = true;
		if (elementType == 0 && elementState instanceof String stringState) {
			if (name.equals("Restaurant_Name"))
				Restaurant_Name.setValue(stringState);
		}
		menuStateUpdateActive = false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		Restaurant_Name.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		guiGraphics.blit(IMAGE_0, this.leftPos + -66, this.topPos + -20, 0, 0, 250, 88, 250, 88);
		guiGraphics.blit(IMAGE_1, this.leftPos + 4, this.topPos + -10, 0, 0, 93, 21, 93, 21);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		if (Restaurant_Name.isFocused())
			return Restaurant_Name.keyPressed(key, b, c);
		return super.keyPressed(key, b, c);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String Restaurant_NameValue = Restaurant_Name.getValue();
		super.resize(minecraft, width, height);
		Restaurant_Name.setValue(Restaurant_NameValue);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, Component.translatable("gui.masterchef_restaurant.create_restaurant_gui.label_management"), 17, -7, -1, false);
	}

	@Override
	public void init() {
		super.init();
		Restaurant_Name = new EditBox(this.font, this.leftPos + -48, this.topPos + 19, 166, 18, Component.translatable("gui.masterchef_restaurant.create_restaurant_gui.Restaurant_Name"));
		Restaurant_Name.setMaxLength(8192);
		Restaurant_Name.setResponder(content -> {
			if (!menuStateUpdateActive)
				menu.sendMenuStateUpdate(entity, 0, "Restaurant_Name", content, false);
		});
		Restaurant_Name.setHint(Component.translatable("gui.masterchef_restaurant.create_restaurant_gui.Restaurant_Name"));
		this.addWidget(this.Restaurant_Name);
		imagebutton_checkboxon = new ImageButton(this.leftPos + 131, this.topPos + 15, 26, 26,
				new WidgetSprites(ResourceLocation.parse("masterchef_restaurant:textures/screens/create_icon.png"), ResourceLocation.parse("masterchef_restaurant:textures/screens/create_icon.png")), e -> {
					int x = CreateRestaurantGUIScreen.this.x;
					int y = CreateRestaurantGUIScreen.this.y;
					if (true) {
						PacketDistributor.sendToServer(new CreateRestaurantGUIButtonMessage(0, x, y, z));
						CreateRestaurantGUIButtonMessage.handleButtonAction(entity, 0, x, y, z);
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		};
		this.addRenderableWidget(imagebutton_checkboxon);
	}
}