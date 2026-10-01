package net.mcreator.omnichef.procedures;

import net.minecraft.client.Minecraft;

import net.mcreator.omnichef.entity.ChairMobEntity;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT)
public class HideChairHealthProcedure {

    @SubscribeEvent
    public static void onRenderVehicleHealth(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.VEHICLE_HEALTH)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null
                && minecraft.player.getVehicle() instanceof ChairMobEntity) {
            event.setCanceled(true);
        }
    }
}