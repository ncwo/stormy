package me.tryfle.stormy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelHandlerContext;
import me.tryfle.stormy.events.EventDirection;
import me.tryfle.stormy.events.PacketEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.weavemc.loader.api.event.EventBus;

@Mixin(priority = 995, value = NetworkManager.class)
public class NetworkManagerMixin {
	@Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
	public void sendPacket(Packet<?> p_sendPacket_1_, CallbackInfo ci) {
		PacketEvent e = new PacketEvent(p_sendPacket_1_, EventDirection.OUTGOING);

		EventBus.callEvent(e);

		p_sendPacket_1_ = e.getPacket();
		if (e.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
	public void receivePacket(ChannelHandlerContext p_channelRead0_1_, Packet<?> p_channelRead0_2_, CallbackInfo ci) {
		PacketEvent e = new PacketEvent(p_channelRead0_2_, EventDirection.INCOMING);

		EventBus.callEvent(e);

		p_channelRead0_2_ = e.getPacket();
		if (e.isCancelled()) {
			ci.cancel();
		}
	}
}