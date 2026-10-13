/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.omnichef.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.omnichef.OmnichefMod;

public class OmnichefModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, OmnichefMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> CLIENTRING = REGISTRY.register("clientring", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("omnichef", "clientring")));
}