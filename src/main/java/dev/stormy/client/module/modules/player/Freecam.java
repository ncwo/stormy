package dev.stormy.client.module.modules.player;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.weavemc.loader.api.event.RenderHandEvent;
import net.weavemc.loader.api.event.RenderLivingEvent;
import net.weavemc.loader.api.event.RenderWorldEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.MouseEvent;
import dev.stormy.client.clickgui.Theme;
import dev.stormy.client.module.Module;
import dev.stormy.client.module.setting.impl.SliderSetting;
import dev.stormy.client.module.setting.impl.TickSetting;
import dev.stormy.client.utils.Utils;
import dev.stormy.client.utils.player.PlayerUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

//skidded from ravenweave tyty
public class Freecam extends Module {
	public static SliderSetting speed;
	public static TickSetting onDamage;
	public static TickSetting cancel;
	public static EntityOtherPlayerMP en;
	private long lastTime;
	private int[] lcc = { Integer.MAX_VALUE, 0 };
	private final float[] sAng = { 0.0F, 0.0F };

	public Freecam() {
		super("Freecam", ModuleCategory.Player, 0);
		this.registerSetting(speed = new SliderSetting("Speed", 2D, 0.1D, 5.0D, 0.5D));
		this.registerSetting(cancel = new TickSetting("Cancel Mouse Events", true));
		this.registerSetting(onDamage = new TickSetting("Disable on damage", true));
	}

	@Override
	public void onEnable() {
		if (!PlayerUtils.isPlayerInGame()) {
			return;
		}
		if (!mc.thePlayer.onGround) {
			this.disable();
		} else {
			en = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
			en.copyLocationAndAnglesFrom(mc.thePlayer);
			this.sAng[0] = en.rotationYawHead = mc.thePlayer.rotationYawHead;
			this.sAng[1] = mc.thePlayer.rotationPitch;
			en.setVelocity(0.0D, 0.0D, 0.0D);
			en.setInvisible(true);
			mc.theWorld.addEntityToWorld(-8008, en);
			mc.setRenderViewEntity(en);
		}
	}

	@Override
	public void onDisable() {
		if (!PlayerUtils.isPlayerInGame()) {
			return;
		}
		if (en != null) {
			mc.setRenderViewEntity(mc.thePlayer);
			mc.thePlayer.rotationYaw = mc.thePlayer.rotationYawHead = this.sAng[0];
			mc.thePlayer.rotationPitch = this.sAng[1];
			mc.theWorld.removeEntity(en);
			en = null;
		}

		this.lcc = new int[] { Integer.MAX_VALUE, 0 };
		int x = mc.thePlayer.chunkCoordX;
		int z = mc.thePlayer.chunkCoordZ;

		for (int x2 = -1; x2 <= 1; ++x2) {
			for (int z2 = -1; z2 <= 1; ++z2) {
				int a = x + x2;
				int b = z + z2;
				mc.theWorld.markBlockRangeForRenderUpdate(a * 16, 0, b * 16, a * 16 + 15, 256, b * 16 + 15);
			}
		}

	}

	@SubscribeEvent
	public void onLivingEvent(RenderLivingEvent e) {
		updatePos();
	}

	@SubscribeEvent
	public void onRender(RenderHandEvent e) {
		updatePos();
	}

	public void updatePos() {
		if (!PlayerUtils.isPlayerInGame() || en == null)
			return;
		if (onDamage.isToggled() && mc.thePlayer.hurtTime != 0) {
			this.disable();
		} else {
			mc.thePlayer.setSprinting(false);
			mc.thePlayer.moveForward = 0.0F;
			mc.thePlayer.moveStrafing = 0.0F;
			en.rotationYaw = en.rotationYawHead = mc.thePlayer.rotationYaw;
			en.rotationPitch = mc.thePlayer.rotationPitch;
			long currentTime = System.nanoTime();
			//0.215D
			double s = 5D * (currentTime - this.lastTime) / 1.0E9D * speed.getInput();
			EntityOtherPlayerMP var10000;
			double rad;
			double dx;
			double dz;
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
				rad = (double) en.rotationYawHead * 0.017453292519943295D;
				dx = -1.0D * Math.sin(rad) * s;
				dz = Math.cos(rad) * s;
				var10000 = en;
				var10000.posX += dx;
				var10000.posZ += dz;
			}

			if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
				rad = (double) en.rotationYawHead * 0.017453292519943295D;
				dx = -1.0D * Math.sin(rad) * s;
				dz = Math.cos(rad) * s;
				var10000 = en;
				var10000.posX -= dx;
				var10000.posZ -= dz;
			}

			if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
				rad = (double) (en.rotationYawHead - 90.0F) * 0.017453292519943295D;
				dx = -1.0D * Math.sin(rad) * s;
				dz = Math.cos(rad) * s;
				var10000 = en;
				var10000.posX += dx;
				var10000.posZ += dz;
			}

			if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
				rad = (double) (en.rotationYawHead + 90.0F) * 0.017453292519943295D;
				dx = -1.0D * Math.sin(rad) * s;
				dz = Math.cos(rad) * s;
				var10000 = en;
				var10000.posX += dx;
				var10000.posZ += dz;
			}

			if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
				var10000 = en;
				var10000.posY += 0.93D * s;
			}

			if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
				var10000 = en;
				var10000.posY -= 0.93D * s;
			}

			mc.thePlayer.setSneaking(false);
			if (this.lcc[0] != Integer.MAX_VALUE && (this.lcc[0] != en.chunkCoordX || this.lcc[1] != en.chunkCoordZ)) {
				int x = en.chunkCoordX;
				int z = en.chunkCoordZ;
				mc.theWorld.markBlockRangeForRenderUpdate(x * 16, 0, z * 16, x * 16 + 15, 256, z * 16 + 15);
			}

			this.lcc[0] = en.chunkCoordX;
			this.lcc[1] = en.chunkCoordZ;
			this.lastTime = currentTime;
		}
	}

	@SubscribeEvent
	public void onRenderWorld(RenderWorldEvent event) {
		if (!this.enabled) return;
		if (PlayerUtils.isPlayerInGame()) {
			mc.thePlayer.renderArmPitch = mc.thePlayer.prevRenderArmPitch = 700.0F;
			Utils.HUD.drawBoxAroundEntity(mc.thePlayer, 1, 0.0D, 0.0D, Theme.getMainColor().getRGB(), false);
			Utils.HUD.drawBoxAroundEntity(mc.thePlayer, 2, 0.0D, 0.0D, Theme.getMainColor().getRGB(), false);
		}
	}

	@SubscribeEvent
	public void onMouseEvent(MouseEvent e) {
		if (this.enabled && cancel.isToggled() && PlayerUtils.isPlayerInGame() && e.getButton() != -1) {
			e.setCancelled(true);
		}
	}
}
