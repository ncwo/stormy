package me.tryfle.stormy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import me.tryfle.stormy.events.SlowdownEvent;
import me.tryfle.stormy.events.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.weavemc.loader.api.event.EventBus;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends AbstractClientPlayer {
	public EntityPlayerSPMixin(World world, GameProfile gameProfile) {
		super(world, gameProfile);
	}

	@Shadow
	public int sprintingTicksLeft;

	@Override
	@Shadow
	public abstract void setSprinting(boolean p_setSprinting_1_);

	@Shadow
	public int sprintToggleTimer;
	@Shadow
	public float prevTimeInPortal;
	@Shadow
	public float timeInPortal;
	@Shadow
	public Minecraft mc;
	@Shadow
	public MovementInput movementInput;

	@Override
	@Shadow
	public abstract boolean pushOutOfBlocks(double p_pushOutOfBlocks_1_, double p_pushOutOfBlocks_3_, double p_pushOutOfBlocks_5_);

	@Override
	@Shadow
	public abstract void sendPlayerAbilities();

	@Shadow
	public abstract boolean isCurrentViewEntity();

	@Shadow
	public abstract boolean isRidingHorse();

	@Shadow
	public int horseJumpPowerCounter;
	@Shadow
	public float horseJumpPower;

	@Shadow
	public abstract void sendHorseJump();

	@Override
	@Shadow
	public abstract boolean isSneaking();

	@Unique
	private double cachedX;
	@Unique
	private double cachedY;
	@Unique
	private double cachedZ;
	@Unique
	private boolean cachedOnGround;
	@Unique
	private float cachedRotationPitch;
	@Unique
	private float cachedRotationYaw;

	/**
	 * @author mc author
	 * @reason noslow
	 */
	@Override
	@Overwrite
	public void onLivingUpdate() {
		if (this.sprintingTicksLeft > 0) {
			--this.sprintingTicksLeft;

			if (this.sprintingTicksLeft == 0) {
				this.setSprinting(false);
			}
		}

		if (this.sprintToggleTimer > 0) {
			--this.sprintToggleTimer;
		}

		this.prevTimeInPortal = this.timeInPortal;

		if (this.inPortal) {
			if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
				this.mc.displayGuiScreen(null);
			}

			if (this.timeInPortal == 0.0F) {
				this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
			}

			this.timeInPortal += 0.0125F;

			if (this.timeInPortal >= 1.0F) {
				this.timeInPortal = 1.0F;
			}

			this.inPortal = false;
		} else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
			this.timeInPortal += 0.006666667F;

			if (this.timeInPortal > 1.0F) {
				this.timeInPortal = 1.0F;
			}
		} else {
			if (this.timeInPortal > 0.0F) {
				this.timeInPortal -= 0.05F;
			}

			if (this.timeInPortal < 0.0F) {
				this.timeInPortal = 0.0F;
			}
		}

		if (this.timeUntilPortal > 0) {
			--this.timeUntilPortal;
		}

		boolean flag = this.movementInput.jump;
		boolean flag1 = this.movementInput.sneak;
		float f = 0.8F;
		boolean flag2 = this.movementInput.moveForward >= f;
		this.movementInput.updatePlayerMoveState();

		if (this.isUsingItem() && !this.isRiding()) {
			SlowdownEvent e = new SlowdownEvent();
			EventBus.callEvent(e);
			if (!e.isCancelled()) {
				this.movementInput.moveStrafe *= 0.2F;
				this.movementInput.moveForward *= 0.2F;
				this.sprintToggleTimer = 0;
			}
		}

		this.pushOutOfBlocks(this.posX - this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + this.width * 0.35D);
		this.pushOutOfBlocks(this.posX - this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - this.width * 0.35D);
		this.pushOutOfBlocks(this.posX + this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - this.width * 0.35D);
		this.pushOutOfBlocks(this.posX + this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + this.width * 0.35D);
		boolean flag3 = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;

		if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= f && !this.isSprinting() && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
			if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
				this.sprintToggleTimer = 7;
			} else {
				this.setSprinting(true);
			}
		}

		if (!this.isSprinting() && this.movementInput.moveForward >= f && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
			this.setSprinting(true);
		}

		if (this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag3)) {
			this.setSprinting(false);
		}

		if (this.capabilities.allowFlying) {
			if (this.mc.playerController.isSpectatorMode()) {
				if (!this.capabilities.isFlying) {
					this.capabilities.isFlying = true;
					this.sendPlayerAbilities();
				}
			} else if (!flag && this.movementInput.jump) {
				if (this.flyToggleTimer == 0) {
					this.flyToggleTimer = 7;
				} else {
					this.capabilities.isFlying = !this.capabilities.isFlying;
					this.sendPlayerAbilities();
					this.flyToggleTimer = 0;
				}
			}
		}

		if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
			if (this.movementInput.sneak) {
				this.motionY -= this.capabilities.getFlySpeed() * 3.0F;
			}

			if (this.movementInput.jump) {
				this.motionY += this.capabilities.getFlySpeed() * 3.0F;
			}
		}

		if (this.isRidingHorse()) {
			if (this.horseJumpPowerCounter < 0) {
				++this.horseJumpPowerCounter;

				if (this.horseJumpPowerCounter == 0) {
					this.horseJumpPower = 0.0F;
				}
			}

			if (flag && !this.movementInput.jump) {
				this.horseJumpPowerCounter = -10;
				this.sendHorseJump();
			} else if (!flag && this.movementInput.jump) {
				this.horseJumpPowerCounter = 0;
				this.horseJumpPower = 0.0F;
			} else if (flag) {
				++this.horseJumpPowerCounter;

				if (this.horseJumpPowerCounter < 10) {
					this.horseJumpPower = this.horseJumpPowerCounter * 0.1F;
				} else {
					this.horseJumpPower = 0.8F + 2.0F / (this.horseJumpPowerCounter - 9) * 0.1F;
				}
			}
		} else {
			this.horseJumpPower = 0.0F;
		}

		super.onLivingUpdate();

		if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
			this.capabilities.isFlying = false;
			this.sendPlayerAbilities();
		}
	}

	@Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
	private void onUpdateWalkingPlayerPre(CallbackInfo ci) {
		cachedX = posX;
		cachedY = posY;
		cachedZ = posZ;

		cachedOnGround = onGround;

		cachedRotationYaw = rotationYaw;
		cachedRotationPitch = rotationPitch;

		UpdateEvent event = new UpdateEvent.Pre(posX, posY, posZ, rotationYaw, rotationPitch, onGround);
		EventBus.callEvent(event);
		if (event.isCancelled()) {
			ci.cancel();
			return;
		}

		posX = event.getX();
		posY = event.getY();
		posZ = event.getZ();

		onGround = event.isOnGround();

		rotationYaw = event.getYaw();
		rotationPitch = event.getPitch();
	}
}
