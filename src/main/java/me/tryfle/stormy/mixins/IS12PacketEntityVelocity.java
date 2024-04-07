package me.tryfle.stormy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@SuppressWarnings("rawtypes")
@Mixin(S12PacketEntityVelocity.class)
public interface IS12PacketEntityVelocity extends Packet {
	@Accessor("motionX")
	void setMotionX(int motionX);

	@Accessor("motionY")
	void setMotionY(int motionY);

	@Accessor("motionZ")
	void setMotionZ(int motionZ);
}