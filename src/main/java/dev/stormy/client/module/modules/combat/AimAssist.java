package dev.stormy.client.module.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import dev.stormy.client.module.Module;
import dev.stormy.client.module.modules.player.Blink;
import dev.stormy.client.module.setting.impl.DescriptionSetting;
import dev.stormy.client.module.setting.impl.SliderSetting;
import dev.stormy.client.module.setting.impl.TickSetting;
import dev.stormy.client.utils.player.PlayerUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.weavemc.loader.api.event.RenderHandEvent;
import net.weavemc.loader.api.event.RenderLivingEvent;
import net.weavemc.loader.api.event.SubscribeEvent;

public class AimAssist extends Module {
	public static SliderSetting speed;

	public static SliderSetting fov;

	public static SliderSetting distance;

	public static TickSetting clickAim;

	public static TickSetting weaponOnly;

	public static TickSetting aimInvis;

	public static TickSetting breakBlocks;

	public boolean breakHeld = false;

	long lastTime = 0L;

	public AimAssist() {
		super("AimAssist", Module.ModuleCategory.Combat, 0);
		registerSetting(new DescriptionSetting("Aims at enemies."));
		registerSetting(speed = new SliderSetting("Speed", 15.0D, 1.0D, 100.0D, 1.0D));
		registerSetting(fov = new SliderSetting("FOV", 30.0D, 15.0D, 180.0D, 1.0D));
		registerSetting(distance = new SliderSetting("Distance", 4.5D, 1.0D, 10.0D, 0.5D));
		registerSetting(clickAim = new TickSetting("Clicking only", true));
		registerSetting(weaponOnly = new TickSetting("Weapon only", false));
		registerSetting(aimInvis = new TickSetting("Aim at invis", false));
		registerSetting(breakBlocks = new TickSetting("Break Blocks", true));
	}

	@SubscribeEvent
	public void onLivingEvent(RenderLivingEvent e) {
		aim();
	}

	@SubscribeEvent
	public void onRender(RenderHandEvent e) {
		aim();
	}

	public void aim() {
		long currentTime = System.nanoTime();
		if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus || (weaponOnly

				.isToggled() && PlayerUtils.isPlayerHoldingWeapon()) || (breakBlocks.isToggled() && breakBlock()) || (clickAim.isToggled() && !Mouse.isButtonDown(0))) {
			this.lastTime = currentTime;
			return;
		}
		Entity en = getEnemy();
		if (en != null) {
			double n = n(en);
			if (n > 1.0D || n < -1.0D) {
				double spd = (800L * (currentTime - this.lastTime)) / 1.0E9D / (101.0D - ThreadLocalRandom.current().nextDouble(speed.getInput() - 0.97388D, speed.getInput()));
				float val = (float) (-n * ((speed.getInput() == 100.0D) ? 1.0D : spd));
				mc.thePlayer.rotationYaw += val / 2.0F;
			}
		}
		this.lastTime = currentTime;
	}

	public Entity getEnemy() {
		List<EntityPlayer> valid = new ArrayList<>();
		for (EntityPlayer en : mc.theWorld.playerEntities) {
			if (!isTarget(en) || !fov(en, (float) fov.getInput()) || (!aimInvis.isToggled() && en.isInvisible()) || mc.thePlayer.getDistanceToEntity(en) > distance.getInput() || (Blink.fakePlayer != null && en.getEntityId() == Blink.fakePlayer.getEntityId())) {
				continue;
			}
			valid.add(en);
		}
		return valid.stream().min(Comparator.comparingDouble(AimAssist::n)).orElse(null);
	}

	public static boolean fov(Entity entity, float fov) {
		fov = (float) (fov * 0.5D);
		double v = ((mc.thePlayer.rotationYaw - m(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
		return ((v > 0.0D && v < fov) || (-fov < v && v < 0.0D));
	}

	public static double n(Entity en) {
		return ((mc.thePlayer.rotationYaw - m(en)) % 360.0D + 540.0D) % 360.0D - 180.0D;
	}

	public static float m(Entity ent) {
		double x = ent.posX - mc.thePlayer.posX;
		double z = ent.posZ - mc.thePlayer.posZ;
		double yaw = Math.atan2(x, z) * 57.2957795D;
		return (float) (yaw * -1.0D);
	}

	public boolean breakBlock() {
		if (breakBlocks.isToggled() && mc.objectMouseOver != null) {
			BlockPos p = mc.objectMouseOver.getBlockPos();
			if (p != null && Mouse.isButtonDown(0)) {
				if (mc.theWorld.getBlockState(p).getBlock() != Blocks.air && !(mc.theWorld.getBlockState(p).getBlock() instanceof net.minecraft.block.BlockLiquid)) {
					if (!this.breakHeld) {
						int e = mc.gameSettings.keyBindAttack.getKeyCode();
						KeyBinding.setKeyBindState(e, true);
						KeyBinding.onTick(e);
						this.breakHeld = true;
					}
					return true;
				}
				if (this.breakHeld) {
					this.breakHeld = false;
				}
			}
		}
		return false;
	}

	public boolean isTarget(EntityPlayer en) {
		return (en != mc.thePlayer && en.deathTime == 0);
	}
}
