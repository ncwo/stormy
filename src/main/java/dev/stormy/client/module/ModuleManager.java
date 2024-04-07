package dev.stormy.client.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import dev.stormy.client.module.modules.client.AntiBot;
import dev.stormy.client.module.modules.client.ArrayListModule;
import dev.stormy.client.module.modules.client.ClickGuiModule;
import dev.stormy.client.module.modules.combat.AimAssist;
import dev.stormy.client.module.modules.combat.AutoBlock;
import dev.stormy.client.module.modules.combat.AutoClicker;
import dev.stormy.client.module.modules.combat.Backtrack;
import dev.stormy.client.module.modules.combat.ClickAssist;
import dev.stormy.client.module.modules.combat.Criticals;
import dev.stormy.client.module.modules.combat.Killaura;
import dev.stormy.client.module.modules.combat.NoHitDelay;
import dev.stormy.client.module.modules.combat.Reach;
import dev.stormy.client.module.modules.combat.Velocity;
import dev.stormy.client.module.modules.combat.WTap;
import dev.stormy.client.module.modules.movement.AntiVoid;
import dev.stormy.client.module.modules.movement.Bhop;
import dev.stormy.client.module.modules.movement.ClosetSpeed;
import dev.stormy.client.module.modules.movement.Flight;
import dev.stormy.client.module.modules.movement.InvMove;
import dev.stormy.client.module.modules.movement.KeepSprint;
import dev.stormy.client.module.modules.movement.NoSlow;
import dev.stormy.client.module.modules.movement.Sprint;
import dev.stormy.client.module.modules.movement.Strafe;
import dev.stormy.client.module.modules.movement.Timer;
import dev.stormy.client.module.modules.player.AutoPlace;
import dev.stormy.client.module.modules.player.BedNuker;
import dev.stormy.client.module.modules.player.Blink;
import dev.stormy.client.module.modules.player.FastPlace;
import dev.stormy.client.module.modules.player.Manager;
import dev.stormy.client.module.modules.player.NoRotate;
import dev.stormy.client.module.modules.player.SafeWalk;
import dev.stormy.client.module.modules.player.Stealer;
import dev.stormy.client.module.modules.player.SumoFences;
import dev.stormy.client.module.modules.render.Chams;
import dev.stormy.client.module.modules.render.ChestESP;
import dev.stormy.client.module.modules.render.Nametags;
import dev.stormy.client.module.modules.render.PlayerESP;
import dev.stormy.client.utils.Utils;
import net.minecraft.client.gui.FontRenderer;

public class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	public static boolean initialized = false;

	public ModuleManager() {
		if (initialized) {
			return;
		}
		addModule(new AutoClicker());
		addModule(new AimAssist());
		addModule(new ClickAssist());
		addModule(new Reach());
		addModule(new Velocity());
		addModule(new InvMove());
		addModule(new NoHitDelay());
		addModule(new Backtrack());
		addModule(new KeepSprint());
		addModule(new NoSlow());
		addModule(new Timer());
		addModule(new AutoPlace());
		addModule(new BedNuker());
		addModule(new FastPlace());
		addModule(new SafeWalk());
		addModule(new AntiBot());
		addModule(new Chams());
		addModule(new ChestESP());
		addModule(new Nametags());
		addModule(new PlayerESP());
		addModule(new ArrayListModule());
		addModule(new ClickGuiModule());
		addModule(new ClosetSpeed());
		addModule(new Blink());
		addModule(new NoRotate());
		addModule(new Bhop());
		addModule(new Killaura());
		addModule(new AntiVoid());
		addModule(new Sprint());
		addModule(new Flight());
		addModule(new AutoBlock());
		addModule(new Strafe());
		addModule(new Criticals());
		addModule(new Stealer());
		addModule(new Manager());
		addModule(new WTap());
		addModule(new SumoFences());
		initialized = true;
	}

	private void addModule(Module m) {
		this.modules.add(m);
		this.modules.sort(Comparator.comparing(module -> m.getName().toLowerCase()));
	}

	public Module getModuleByClazz(Class<? extends Module> c) {
		if (!initialized) {
			return null;
		}
		for (Module module : this.modules) {
			if (module.getClass().equals(c)) {
				return module;
			}
		}
		return null;
	}

	public List<Module> getModules() {
		return this.modules;
	}

	public List<Module> getModulesInCategory(Module.ModuleCategory categ) {
		ArrayList<Module> modulesOfCat = new ArrayList<>();
		for (Module mod : this.modules) {
			if (mod.moduleCategory().equals(categ)) {
				modulesOfCat.add(mod);
			}
		}
		return modulesOfCat;
	}

	public void sort() {
		if (ArrayListModule.alphabeticalSort.isToggled()) {
			this.modules.sort(Comparator.comparing(Module::getName));
		} else {
			this.modules.sort((o1, o2) -> Utils.mc.fontRendererObj.getStringWidth(o2.getName()) - Utils.mc.fontRendererObj.getStringWidth(o1.getName()));
		}
	}

	public void sortLongShort() {
		this.modules.sort(Comparator.comparingInt(o2 -> Utils.mc.fontRendererObj.getStringWidth(o2.getName())));
	}

	public void sortShortLong() {
		this.modules.sort((o1, o2) -> Utils.mc.fontRendererObj.getStringWidth(o2.getName()) - Utils.mc.fontRendererObj.getStringWidth(o1.getName()));
	}

	public int getLongestActiveModule(FontRenderer fr) {
		int length = 0;
		for (Module mod : this.modules) {
			if (mod.isEnabled() && fr.getStringWidth(mod.getName()) > length) {
				length = fr.getStringWidth(mod.getName());
			}
		}
		return length;
	}

	public int getBoxHeight(FontRenderer fr, int margin) {
		int length = 0;
		for (Module mod : this.modules) {
			if (mod.isEnabled()) {
				length += fr.FONT_HEIGHT + margin;
			}
		}
		return length;
	}
}
