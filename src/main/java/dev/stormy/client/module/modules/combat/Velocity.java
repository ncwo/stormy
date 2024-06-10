package dev.stormy.client.module.modules.combat;

import dev.stormy.client.module.Module;
import dev.stormy.client.module.setting.impl.ComboSetting;
import dev.stormy.client.module.setting.impl.SliderSetting;
import me.tryfle.stormy.mixins.IS12PacketEntityVelocity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.weavemc.loader.api.event.PacketEvent;
import net.weavemc.loader.api.event.SubscribeEvent;

import org.lwjgl.input.Keyboard;
import java.util.Random;

@SuppressWarnings("unused")
public class Velocity extends Module {
	public static SliderSetting horizontal, vertical, chance;
	public static ComboSetting<velomode> velomodes;
	public int key = mc.gameSettings.keyBindJump.getKeyCode();
	public Random rand = new Random();



	public Velocity() {
		super("Velocity", Module.ModuleCategory.Combat, 0);
		this.registerSetting(horizontal = new SliderSetting("Horizontal", 100.0D, 0.0D, 100.0D, 1.0D));
		this.registerSetting(vertical = new SliderSetting("Vertical", 100.0D, 0.0D, 100.0D, 1.0D));
		this.registerSetting(chance = new SliderSetting("Chance", 100.0D, 0.0D, 100.0D, 1.0D));
		this.registerSetting(velomodes = new ComboSetting<>("Mode", velomode.JumpReset));
	}

	@SubscribeEvent
	public void onPacket(me.tryfle.stormy.events.PacketEvent e) {
		if (velomodes.getMode() == velomode.Cancel || mc.thePlayer == null || (velomodes.getMode() == velomode.JumpReset && (!mc.thePlayer.onGround || Keyboard.isKeyDown(key)))) {
			return;
		}
		if (!e.isOutgoing()) {
			if (e.getPacket() instanceof S12PacketEntityVelocity) {
				if (chance.getInput() != 100.0D) {
					double ch = Math.random() * 100;
					if (ch >= chance.getInput()) {
						return;
					}
				}

				Entity entity = mc.theWorld.getEntityByID(((S12PacketEntityVelocity) e.getPacket()).getEntityID());
				if (entity == mc.thePlayer) {
					velo(e);
				}
			}
		}
	}

	public void velo(me.tryfle.stormy.events.PacketEvent e) {
		if (velomodes.getMode() == velomode.Cancel) {
			return;
		}
		//skidded from ravenweave tyty
		if (velomodes.getMode() == velomode.JumpReset || (velomodes.getMode() == velomode.Minemen && mc.thePlayer.onGround)) {
			KeyBinding.setKeyBindState(key, true);
			KeyBinding.onTick(key);
			javax.swing.Timer timer = new javax.swing.Timer(rand.nextInt(10, 20), actionevent -> KeyBinding.setKeyBindState(key, false));
			timer.setRepeats(false);
			timer.start();
			return;
		}
		S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
		IS12PacketEntityVelocity accessorPacket = (IS12PacketEntityVelocity) packet;
		accessorPacket.setMotionX((int) (packet.getMotionX() * horizontal.getInput() / 100));
		accessorPacket.setMotionZ((int) (packet.getMotionZ() * horizontal.getInput() / 100));
		accessorPacket.setMotionY((int) (packet.getMotionY() * vertical.getInput() / 100));
		e.setPacket(accessorPacket);
	}

	@SubscribeEvent
	public void onPacketReceive(PacketEvent.Receive e) {
		if (e.getPacket() instanceof S12PacketEntityVelocity s12 && velomodes.getMode() == velomode.Cancel) {
			if (mc.thePlayer != null && s12.getEntityID() == mc.thePlayer.entityId) {
				e.setCancelled(true);
			}
		}
	}

	public enum velomode {
		Normal, Cancel, Minemen, JumpReset
	}
}
