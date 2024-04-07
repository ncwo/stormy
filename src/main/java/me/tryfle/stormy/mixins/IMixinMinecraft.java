package me.tryfle.stormy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

@Mixin(Minecraft.class)
public interface IMixinMinecraft {
	@Accessor("timer")
	Timer getTimer();
}
