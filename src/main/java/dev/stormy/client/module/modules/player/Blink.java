package dev.stormy.client.module.modules.player;

import java.util.ArrayList;

import dev.stormy.client.module.Module;
import dev.stormy.client.module.setting.impl.DescriptionSetting;
import dev.stormy.client.module.setting.impl.SliderSetting;
import dev.stormy.client.module.setting.impl.TickSetting;
import dev.stormy.client.utils.math.TimerUtils;
import me.tryfle.stormy.events.EventDirection;
import me.tryfle.stormy.events.PacketEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.weavemc.loader.api.event.EventBus;
import net.weavemc.loader.api.event.ShutdownEvent;
import net.weavemc.loader.api.event.StartGameEvent;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.TickEvent;
import net.weavemc.loader.api.event.WorldEvent;

public class Blink extends Module {
	public static TickSetting inbound;

	public static TickSetting outbound;

	public static TickSetting spawnFake;

	public static TickSetting pulse;

	public static SliderSetting pulseDelay;

	@SuppressWarnings("rawtypes")
	private final ArrayList<? extends Packet> outboundPackets = new ArrayList<>();

	public static EntityOtherPlayerMP fakePlayer;

	private TimerUtils t = new TimerUtils();

	public Blink() {
		super("Blink", Module.ModuleCategory.Player, 0);
		registerSetting(new DescriptionSetting("Chokes packets until disabled."));
		registerSetting(spawnFake = new TickSetting("Spawn fake player", true));
		registerSetting(pulse = new TickSetting("Pulse", false));
		registerSetting(pulseDelay = new SliderSetting("Pulse MS", 0.0D, 0.0D, 1000.0D, 10.0D));
	}

	@SubscribeEvent
	public void onPacket(PacketEvent e) {
		if ((e.getDirection() == EventDirection.INCOMING) || !e.getPacket().getClass().getCanonicalName().startsWith("net.minecraft.network.play.client")) {
			return;
		}
		this.outboundPackets.add(e.getPacket());
		e.setCancelled(true);
	}

	@Override
	public void onEnable() {
		this.outboundPackets.clear();
		if (spawnFake.isToggled() && mc.thePlayer != null) {
			fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
			fakePlayer.setRotationYawHead(mc.thePlayer.rotationYawHead);
			fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);
			mc.theWorld.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);
		}
	}

	@Override
	public void onDisable() {
		EventBus.unsubscribe(this);
		for (Packet<?> packet : this.outboundPackets) {
			mc.getNetHandler().addToSendQueue(packet);
		}
		this.outboundPackets.clear();
		if (fakePlayer != null) {
			mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
			fakePlayer = null;
		}
	}

	@SubscribeEvent
	public void onShutdown(ShutdownEvent e) {
		disable();
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent e) {
		disable();
	}

	@SubscribeEvent
	public void onStart(StartGameEvent e) {
		disable();
	}

	@SubscribeEvent
	public void onTick(TickEvent e) {
		if (mc.thePlayer == null) {
			return;
		}
		if (pulse.isToggled() && this.t.hasReached(pulseDelay.getInput())) {
			reToggle();
			this.t.reset();
		}
	}

	private void reToggle() {
		toggle();
		toggle();
	}
}
