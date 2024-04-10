package dev.stormy.client.module.modules.combat;

import java.lang.reflect.Method;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import dev.stormy.client.module.Module;
import dev.stormy.client.module.setting.impl.DescriptionSetting;
import dev.stormy.client.module.setting.impl.SliderSetting;
import dev.stormy.client.module.setting.impl.TickSetting;
import dev.stormy.client.utils.Utils;
import dev.stormy.client.utils.math.TimerUtils;
import dev.stormy.client.utils.player.PlayerUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.weavemc.loader.api.event.Event;
import net.weavemc.loader.api.event.EventBus;
import net.weavemc.loader.api.event.MouseEvent;
import net.weavemc.loader.api.event.RenderHandEvent;
import net.weavemc.loader.api.event.SubscribeEvent;

public class AutoClicker extends Module {
	public static TickSetting breakBlocks;

	public static TickSetting hitSelect;

	public static TickSetting inventoryFill;

	public static SliderSetting leftCPS;

	public boolean shouldClick;

	public boolean breakHeld = false;

	private final Method playerMouseInput;

	TimerUtils t = new TimerUtils();

	long lastClickTime = 0L;

	int lmb = mc.gameSettings.keyBindAttack.getKeyCode();

	int delay = 0;

	public boolean delaying = false;

	public AutoClicker() {
		super("AutoClicker", Module.ModuleCategory.Combat, 0);
		registerSetting(new DescriptionSetting("Click automatically"));
		registerSetting(leftCPS = new SliderSetting("CPS", 10.0D, 1.0D, 20.0D, 1.0D));
		registerSetting(breakBlocks = new TickSetting("Break blocks", true));
		registerSetting(hitSelect = new TickSetting("Hit Select", false));
		registerSetting(inventoryFill = new TickSetting("Inventory Fill", true));
		try {
			this.playerMouseInput = GuiScreen.class.getMethod("mouseClicked", new Class[] { int.class, int.class, int.class });
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (this.playerMouseInput != null) {
			this.playerMouseInput.setAccessible(true);
		}
	}

	public boolean breakBlock() {
		if (breakBlocks.isToggled() && mc.objectMouseOver != null) {
			BlockPos p = mc.objectMouseOver.getBlockPos();
			if (p != null) {
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

	public boolean hitSelectLogic() {
		if (!hitSelect.isToggled()) {
			return false;
		}
		MovingObjectPosition result = mc.objectMouseOver;
		if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			Entity entity = result.entityHit;
			if (entity instanceof EntityPlayer) {
				EntityPlayer targetPlayer = (EntityPlayer) entity;
				return (hitSelect.isToggled() && PlayerUtils.lookingAtPlayer(mc.thePlayer, targetPlayer, 4.0D));
			}
		}
		return false;
	}

	@SubscribeEvent
	public void onRender(RenderHandEvent e) {
		if (inventoryFill.isToggled() && (mc.currentScreen != null || !mc.inGameHasFocus)) {
			doInvClick();
			return;
		}
		randomizer();
		if (PlayerUtils.isPlayerInGame() && Mouse.isButtonDown(0) && this.shouldClick && mc.currentScreen == null) {
			if ((hitSelect.isToggled() && !hitSelectLogic()) || breakBlock()) {
				return;
			}
			long currentTime = System.currentTimeMillis();
			if (this.t.hasReached(Utils.Java.randomInt(5000.0D, 10000.0D))) {
				this.delay = 1000 / (int) (leftCPS.getInput() + Utils.Java.randomInt(-4.0D, 0.0D));
				this.t.reset();
			} else {
				this.delay = 1000 / (int) (leftCPS.getInput() + Utils.Java.randomInt(-3.0D, 3.0D));
			}
			if (this.delay < 0) {
				this.delay = 1000 / (int) (leftCPS.getInput() + Utils.Java.randomInt(0.0D, 3.0D));
			}
			if (currentTime - this.lastClickTime >= this.delay && !this.delaying) {
				this.lastClickTime = currentTime;
				KeyBinding.setKeyBindState(this.lmb, true);
				KeyBinding.onTick(this.lmb);
				this.delaying = true;
			}
			if (this.delaying) {
				finishDelay();
			}
		}
	}

	public void randomizer() {
		double random = Utils.Java.randomInt(0.0D, 4.0D);
		this.shouldClick = (random >= 0.5D);
	}

	public void finishDelay() {
		long currentTime = System.currentTimeMillis();
		int newdelay = Utils.Java.randomInt(30.0D, 120.0D);
		if (currentTime - this.lastClickTime >= newdelay) {
			this.lastClickTime = currentTime;
			KeyBinding.setKeyBindState(this.lmb, false);
			KeyBinding.onTick(this.lmb);
			EventBus.callEvent((Event) new MouseEvent());
			this.delaying = false;
			this.shouldClick = false;
		}
	}

	public void doInvClick() {
		if ((mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiChest) && (Keyboard.isKeyDown(54) || Keyboard.isKeyDown(42)) && Mouse.isButtonDown(0)) {
			long currentTime = System.currentTimeMillis();
			// servers cant flag inv cps, but still reset dropping timer
			// so that first click out of inv after 10s isnt always dropping
			if (this.t.hasReached(Utils.Java.randomInt(5000.0D, 10000.0D)))
				this.t.reset();
			this.delay = 1000 / (int) (leftCPS.getInput() + Utils.Java.randomInt(-1.0D, 5.0D));
			if (this.delay < 0)
				this.delay = 1000 / (int) (leftCPS.getInput() + Utils.Java.randomInt(2.0D, 5.0D));
			if (currentTime - this.lastClickTime >= this.delay && !this.delaying) {
				this.lastClickTime = currentTime;
				try {
					GuiScreen guiScreen = mc.currentScreen;
					this.playerMouseInput.invoke(guiScreen, new Object[] { Integer.valueOf(Mouse.getX() * guiScreen.width / mc.displayWidth), Integer.valueOf(guiScreen.height - Mouse.getY() * guiScreen.height / mc.displayHeight - 1), Integer.valueOf(0) });
				} catch (IllegalAccessException | java.lang.reflect.InvocationTargetException illegalAccessException) {
				}
				this.delaying = true;
			}
			if (this.delaying && currentTime - this.lastClickTime >= Utils.Java.randomInt(30.0D, 120.0D)) {
				this.lastClickTime = currentTime;
				this.delaying = false;
				this.shouldClick = false;
			}
		}
	}
}
