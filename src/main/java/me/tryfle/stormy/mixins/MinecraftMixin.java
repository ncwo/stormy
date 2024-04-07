package me.tryfle.stormy.mixins;

import java.util.ArrayList;
import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.stormy.client.main.Stormy;
import dev.stormy.client.module.modules.combat.NoHitDelay;
import me.tryfle.stormy.utils.CPSHandler;
import net.minecraft.client.Minecraft;

@Mixin({ Minecraft.class })
public class MinecraftMixin {
	@Shadow
	public int leftClickCounter;

	@Unique
	private final ArrayList<Long> accurateCPS$leftClickTimes = new ArrayList<>();

	@Unique
	private final ArrayList<Long> accurateCPS$rightClickTimes = new ArrayList<>();

	@Inject(method = { "clickMouse" }, at = { @At("HEAD") })
	public void clickMouseAfter(CallbackInfo ci) {
		if (Stormy.moduleManager.getModuleByClazz(NoHitDelay.class).isEnabled()) {
			this.leftClickCounter = 0;
		}
	}

	@Inject(method = { "clickMouse" }, at = { @At("HEAD") })
	public void countClick(CallbackInfo ci) {
		CPSHandler.INSTANCE.leftCps++;
		this.accurateCPS$leftClickTimes.add(Long.valueOf(System.currentTimeMillis()));
	}

	@Inject(method = { "rightClickMouse" }, at = { @At("HEAD") })
	public void countRightClick(CallbackInfo ci) {
		CPSHandler.INSTANCE.rightCps++;
		this.accurateCPS$rightClickTimes.add(Long.valueOf(System.currentTimeMillis()));
	}

	@Inject(method = { "runTick" }, at = { @At("HEAD") })
	public void handleCPS(CallbackInfo ci) {
		accurateCPS$handleCps(this.accurateCPS$leftClickTimes, 1);
		accurateCPS$handleCps(this.accurateCPS$rightClickTimes, 2);
	}

	@Unique
	private void accurateCPS$handleCps(ArrayList<Long> clickTimes, int button) {
		long currentTime = System.currentTimeMillis();
		Iterator<Long> iterator = clickTimes.iterator();
		while (iterator.hasNext()) {
			long clickTime = iterator.next().longValue();
			if (currentTime - clickTime > 1000L) {
				if (button == 1) {
					CPSHandler.INSTANCE.leftCps--;
				} else {
					CPSHandler.INSTANCE.rightCps--;
				}
				iterator.remove();
			}
		}
	}
}
