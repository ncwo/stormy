package dev.stormy.client.module.modules.player;

import java.util.ArrayList;
import java.util.Arrays;
import dev.stormy.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.weavemc.loader.api.event.SubscribeEvent;
import net.weavemc.loader.api.event.RenderWorldEvent;
import org.lwjgl.input.Keyboard;

public class GhostBlocks extends Module {
	public GhostBlocks() {
		super("GhostBlocks", ModuleCategory.Player, 0);
	}
	@SubscribeEvent
	public void onRender(RenderWorldEvent event) {
		MovingObjectPosition object = mc.thePlayer.rayTrace(mc.playerController.getBlockReachDistance(), 1);
		// redundancy bc of clickgui
		if(!Keyboard.isKeyDown(this.keycode) || object == null || object.getBlockPos() == null || this.interactable(mc.theWorld.getBlockState(object.getBlockPos()).getBlock())) return;
		mc.theWorld.setBlockToAir(object.getBlockPos());
	}
	public static boolean interactable(Block block) {
		return new ArrayList<>(Arrays.asList(
				Blocks.chest,
				Blocks.lever,
				Blocks.trapped_chest,
				Blocks.wooden_button,
				Blocks.stone_button,
				Blocks.standing_sign,
				Blocks.wall_sign,
				Blocks.air
			)).contains(block);
	}

	public void keybind() {
		if (this.keycode != 0 && this.canBeEnabled() && Keyboard.isKeyDown(this.keycode)) {
			this.enable();
		} else {
			this.disable();
		}
	}
}
