package dev.stormy.client.module.modules.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import dev.stormy.client.module.Module;
import dev.stormy.client.module.setting.impl.ComboSetting;
import dev.stormy.client.module.setting.impl.DescriptionSetting;
import dev.stormy.client.module.setting.impl.SliderSetting;
import dev.stormy.client.utils.player.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.weavemc.loader.api.event.MouseEvent;
import net.weavemc.loader.api.event.SubscribeEvent;

public class SumoFences extends Module {
	public static DescriptionSetting a;

	public static DescriptionSetting d;

	public static SliderSetting fenceHeight;

	public static SliderSetting c;

	private final ComboSetting<SumoBlockType> sumoBlockType;

	private Timer timer;

	private static final List<BlockPos> fencePositions = Arrays.asList(new BlockPos[] { new BlockPos(9, 65, -2), new BlockPos(9, 65, -1), new BlockPos(9, 65, 0), new BlockPos(9, 65, 1), new BlockPos(9, 65, 2), new BlockPos(9, 65, 3), new BlockPos(8, 65, 3), new BlockPos(8, 65, 4), new BlockPos(8, 65, 5), new BlockPos(7, 65, 5), new BlockPos(7, 65, 6), new BlockPos(7, 65, 7), new BlockPos(6, 65, 7), new BlockPos(5, 65, 7), new BlockPos(5, 65, 8), new BlockPos(4, 65, 8), new BlockPos(3, 65, 8), new BlockPos(3, 65, 9), new BlockPos(2, 65, 9), new BlockPos(1, 65, 9), new BlockPos(0, 65, 9), new BlockPos(-1, 65, 9), new BlockPos(-2, 65, 9), new BlockPos(-3, 65, 9), new BlockPos(-3, 65, 8), new BlockPos(-4, 65, 8), new BlockPos(-5, 65, 8), new BlockPos(-5, 65, 7), new BlockPos(-6, 65, 7), new BlockPos(-7, 65, 7), new BlockPos(-7, 65, 6), new BlockPos(-7, 65, 5), new BlockPos(-8, 65, 5), new BlockPos(-8, 65, 4), new BlockPos(-8, 65, 3), new BlockPos(-9, 65, 3), new BlockPos(-9, 65, 2), new BlockPos(-9, 65, 1), new BlockPos(-9, 65, 0), new BlockPos(-9, 65, -1), new BlockPos(-9, 65, -2), new BlockPos(-9, 65, -3), new BlockPos(-8, 65, -3), new BlockPos(-8, 65, -4), new BlockPos(-8, 65, -5), new BlockPos(-7, 65, -5), new BlockPos(-7, 65, -6), new BlockPos(-7, 65, -7), new BlockPos(-6, 65, -7), new BlockPos(-5, 65, -7), new BlockPos(-5, 65, -8), new BlockPos(-4, 65, -8), new BlockPos(-3, 65, -8), new BlockPos(-3, 65, -9), new BlockPos(-2, 65, -9), new BlockPos(-1, 65, -9), new BlockPos(0, 65, -9), new BlockPos(1, 65, -9), new BlockPos(2, 65, -9), new BlockPos(3, 65, -9), new BlockPos(3, 65, -8), new BlockPos(4, 65, -8), new BlockPos(5, 65, -8), new BlockPos(5, 65, -7), new BlockPos(6, 65, -7), new BlockPos(7, 65, -7), new BlockPos(7, 65, -6), new BlockPos(7, 65, -5), new BlockPos(8, 65, -5), new BlockPos(8, 65, -4), new BlockPos(8, 65, -3), new BlockPos(9, 65, -3) });

	private final String c1;

	private int ymod;

	public SumoFences() {
		super("SumoFences", Module.ModuleCategory.Player, 0);
		this.c1 = "Mode: Sumo Duel";
		registerSetting(a = new DescriptionSetting("Fences for Hypixel sumo."));
		registerSetting(fenceHeight = new SliderSetting("Fence height", 16.0D, 1.0D, 16.0D, 1.0D));
		registerSetting(this.sumoBlockType = new ComboSetting<>("Block Type:", SumoBlockType.GLASS));
	}

	@Override
	public void onEnable() {
		(this.timer = new Timer()).scheduleAtFixedRate(t(), 0L, 500L);
	}

	@Override
	public void onDisable() {
		if (this.timer != null) {
			this.timer.cancel();
			this.timer.purge();
			this.timer = null;
		}
		if (mc.theWorld == null) {
			return;
		}
		for (BlockPos p : fencePositions) {
			for (int i = 0; i < fenceHeight.getInput(); i++) {
				BlockPos p2 = new BlockPos(p.getX(), p.getY() + i, p.getZ());
				if (mc.theWorld.getBlockState(p2).getBlock() == this.sumoBlockType.getMode().blockType) {
					mc.theWorld.setBlockState(p2, Blocks.air.getDefaultState());
				}
			}
		}
	}

	@SubscribeEvent
	public void onForgeEvent(MouseEvent e) {
		if (!this.enabled) {
			return;
		}
		if (e.getButtonState() && (e.getButton() == 0 || e.getButton() == 1) && PlayerUtils.isPlayerInGame() && shouldPlaceFences()) {
			MovingObjectPosition mop = mc.objectMouseOver;
			if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int x = mop.getBlockPos().getX();
				int z = mop.getBlockPos().getZ();
				for (BlockPos pos : fencePositions) {
					if (pos.getX() == x && pos.getZ() == z) {
						e.setCancelled(true);
						if (e.getButton() == 0) {
							EntityPlayerSP p = mc.thePlayer;
							int armSwingEnd = p.isPotionActive(Potion.digSpeed) ? (6 - 1 + p.getActivePotionEffect(Potion.digSpeed).getAmplifier()) : (p.isPotionActive(Potion.digSlowdown) ? (6 + (1 + p.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2) : 6);
							if (!p.isSwingInProgress || p.swingProgressInt >= armSwingEnd / 2 || p.swingProgressInt < 0) {
								p.swingProgressInt = -1;
								p.isSwingInProgress = true;
							}
						}
						Mouse.poll();
						break;
					}
				}
			}
		}
	}

	public TimerTask t() {
		return new TimerTask() {
			@Override
			public void run() {
				if (SumoFences.this.shouldPlaceFences()) {
					for (BlockPos p : SumoFences.fencePositions) {
						for (int i = 0; i < SumoFences.fenceHeight.getInput(); i++) {
							BlockPos p2 = new BlockPos(p.getX(), p.getY() + i + SumoFences.this.ymod, p.getZ());
							if (Module.mc.theWorld.getBlockState(p2).getBlock() == Blocks.air) {
								Module.mc.theWorld.setBlockState(p2, SumoFences.this.sumoBlockType.getMode().blockType.getDefaultState());
							}
						}
					}
				}
			}
		};
	}

	private boolean shouldPlaceFences() {
		boolean isSumo = false;
		this.ymod = 0;
		if (isHyp()) {
			for (String l : getPlayersFromScoreboard()) {
				String s = str(l);
				if (s.startsWith("Map:") && s.contains("Fort Royale")) {
					this.ymod = 7;
				}
				if (!isSumo && s.equals(this.c1)) {
					isSumo = true;
				}
			}
		}
		return isSumo;
	}

	public static boolean isHyp() {
		if (!PlayerUtils.isPlayerInGame()) {
			return false;
		}
		try {
			return (!mc.isSingleplayer() && ((mc.getCurrentServerData()).serverIP.toLowerCase().contains("hypixel.net") || (mc.getCurrentServerData()).serverIP.toLowerCase().contains("localhost")));
		} catch (Exception welpBruh) {
			welpBruh.printStackTrace();
			return false;
		}
	}

	public static List<String> getPlayersFromScoreboard() {
		List<String> lines = new ArrayList<>();
		if (mc.theWorld == null) {
			return lines;
		}
		Scoreboard scoreboard = mc.theWorld.getScoreboard();
		if (scoreboard != null) {
			ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
			if (objective != null) {
				Collection<Score> scores = scoreboard.getSortedScores(objective);
				List<Score> list = new ArrayList<>();
				Iterator<Score> var5 = scores.iterator();
				while (var5.hasNext()) {
					Score score = var5.next();
					if (score != null && score.getPlayerName() != null && !score.getPlayerName().startsWith("#")) {
						list.add(score);
					}
				}
				if (list.size() > 15) {
					scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
				} else {
					scores = list;
				}
				var5 = scores.iterator();
				while (var5.hasNext()) {
					Score score = var5.next();
					ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
					lines.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
				}
			}
		}
		return lines;
	}

	public static String str(String s) {
		char[] n = StringUtils.stripControlCodes(s).toCharArray();
		StringBuilder v = new StringBuilder();
		for (char c : n) {
			if (c < '' && c > '\024') {
				v.append(c);
			}
		}
		return v.toString();
	}

	public enum SumoBlockType {
		GLASS(Blocks.glass), BARRIER(Blocks.barrier), FENCE(Blocks.oak_fence);

		public Block blockType;

		SumoBlockType(Block blockType) {
			this.blockType = blockType;
		}
	}
}
