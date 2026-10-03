package net.mcreator.omnichef.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public abstract class ItemDisplayMixinMixin {

    @Inject(method = "isPickable", at = @At("HEAD"), cancellable = true)
    private void omnichef$disableClientDisplayPicking(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;

        if (self instanceof Display.ItemDisplay
                && !self.getPersistentData().getString("clientUUID").isEmpty()) {
            cir.setReturnValue(false);
        }
    }
}