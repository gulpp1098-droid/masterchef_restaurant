package net.mcreator.omnichef.client;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

import net.mcreator.omnichef.procedures.ShowRestaurantResultNoticeProcedure;
import net.mcreator.omnichef.procedures.ShowRestaurantResultButtonProcedure;
import net.mcreator.omnichef.network.OmnichefModInventoryButtonMessage;

import java.util.List;
import java.util.ArrayList;

@EventBusSubscriber(Dist.CLIENT)
public class InventoryRenderer {
	private static Minecraft mc = Minecraft.getInstance();
	private static EffectRenderingInventoryScreen screen = null;
	private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("omnichef:textures/screens/new_result_icon.png");
	private static final List<AbstractWidget> buttons = new ArrayList<>();

	@SubscribeEvent
	public static void openScreen(ScreenEvent.Init.Pre event) {
		if (event.getScreen() instanceof EffectRenderingInventoryScreen inventory) {
			if (mc == null)
				mc = Minecraft.getInstance();
			screen = inventory;
			initButtons(event);
		}
	}

	@SubscribeEvent
	public static void closeScreen(ScreenEvent.Closing event) {
		if (mc == null)
			mc = Minecraft.getInstance();
		if (mc != null) {
			if (mc.gameMode == null) {
				buttons.clear();
				return;
			}
		}
		if (event.getScreen() instanceof InventoryScreen && !mc.gameMode.hasInfiniteItems())
			buttons.clear();
		if (event.getScreen() instanceof CreativeModeInventoryScreen)
			buttons.clear();
	}

	@SubscribeEvent
	public static void renderBackground(ContainerScreenEvent.Render.Background event) {
		if (!(event.getContainerScreen() instanceof EffectRenderingInventoryScreen))
			return;
		if (mc == null)
			mc = Minecraft.getInstance();
		if (screen == null)
			screen = (EffectRenderingInventoryScreen) event.getContainerScreen();
		boolean flag = getFlag();
		if (!buttons.isEmpty()) {
			buttons.get(0).visible = flag && ShowRestaurantResultButtonProcedure.execute(mc.player);
		}
		renderImages(event.getGuiGraphics(), event.getMouseX(), event.getMouseY());
	}

	@SubscribeEvent
	public static void renderForeground(ContainerScreenEvent.Render.Foreground event) {
		if (mc == null)
			mc = Minecraft.getInstance();
		if (!(event.getContainerScreen() instanceof EffectRenderingInventoryScreen))
			return;
		if (screen == null)
			screen = (EffectRenderingInventoryScreen) event.getContainerScreen();

	}

	private static void initButtons(ScreenEvent.Init.Pre event) {
		buttons.add(new ImageButton(screen.getGuiLeft() + 308, screen.getGuiTop() + 37, 26, 26,
				new WidgetSprites(ResourceLocation.parse("omnichef:textures/screens/result_icon.png"), ResourceLocation.parse("omnichef:textures/screens/result_icon.png")), e -> {
					if (getFlag() && ShowRestaurantResultButtonProcedure.execute(mc.player)) {
						PacketDistributor.sendToServer(new OmnichefModInventoryButtonMessage(0, (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ()));
						OmnichefModInventoryButtonMessage.handleButtonAction(mc.player, 0, (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
					}
				}) {
			@Override
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
				this.setX(screen.getGuiLeft() + 183);
				this.setY(screen.getGuiTop() + 0);
				if (getFlag() && ShowRestaurantResultButtonProcedure.execute(mc.player))
					guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
			}
		});
		if (screen instanceof InventoryScreen)
			event.addListener(buttons.get(0));
	}

	private static void renderImages(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (screen instanceof InventoryScreen)
			if (ShowRestaurantResultNoticeProcedure.execute(mc.player)) {
				guiGraphics.blit(IMAGE_0, screen.getGuiLeft() + 203, screen.getGuiTop() + -7, 0, 0, 6, 22, 6, 22);
			}
	}

	private static boolean getFlag() {
		if (screen instanceof InventoryScreen inventory)
			return true;
		if (screen instanceof CreativeModeInventoryScreen inventory)
			return inventory.isInventoryOpen();
		return false;
	}
}