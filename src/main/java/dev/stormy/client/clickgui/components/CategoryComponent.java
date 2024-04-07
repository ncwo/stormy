package dev.stormy.client.clickgui.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import dev.stormy.client.clickgui.Component;
import dev.stormy.client.clickgui.Theme;
import dev.stormy.client.main.Stormy;
import dev.stormy.client.module.Module;
import dev.stormy.client.utils.render.RenderUtils;
import net.minecraft.client.gui.FontRenderer;

public class CategoryComponent {
	public ArrayList<ModuleComponent> modulesInCategory = new ArrayList<>();
	public Module.ModuleCategory categoryName;
	private boolean categoryOpened;
	private int width;
	private int y;
	private int x;
	private final int bh;
	public boolean inUse;
	public int xx;
	public int yy;
	public boolean n4m = false;
	public String pvp;
	public boolean pin = false;
	private final double marginY, marginX;

	public CategoryComponent(Module.ModuleCategory category) {
		this.categoryName = category;
		this.width = 92;
		this.x = 5;
		this.y = 5;
		this.bh = 13;
		this.xx = 0;
		this.categoryOpened = false;
		this.inUse = false;
		int tY = this.bh + 3;
		this.marginX = 80;
		this.marginY = 4.5;

		for (Iterator<Module> var3 = Stormy.moduleManager.getModulesInCategory(this.categoryName).iterator(); var3.hasNext(); tY += 16) {
			Module mod = var3.next();
			ModuleComponent b = new ModuleComponent(mod, this, tY);
			this.modulesInCategory.add(b);
		}

	}

	public ArrayList<ModuleComponent> getModules() {
		return this.modulesInCategory;
	}

	public void setX(int n) {
		this.x = n;
		if (Stormy.clientConfig != null) {
			Stormy.clientConfig.saveConfig();
		}
	}

	public void setY(int y) {
		this.y = y;
		if (Stormy.clientConfig != null) {
			Stormy.clientConfig.saveConfig();
		}
	}

	public void mousePressed(boolean d) {
		this.inUse = d;
	}

	public boolean p() {
		return this.pin;
	}

	public void cv(boolean on) {
		this.pin = on;
	}

	public boolean isOpened() {
		return this.categoryOpened;
	}

	public void setOpened(boolean on) {
		this.categoryOpened = on;
		if (Stormy.clientConfig != null) {
			Stormy.clientConfig.saveConfig();
		}
	}

	public void rf(FontRenderer renderer) {
		this.width = 92;
		if (!this.modulesInCategory.isEmpty() && this.categoryOpened) {
			int categoryHeight = 0;

			Component moduleRenderManager;
			for (Iterator<ModuleComponent> moduleInCategoryIterator = this.modulesInCategory.iterator(); moduleInCategoryIterator.hasNext(); categoryHeight += moduleRenderManager.getHeight()) {
				moduleRenderManager = moduleInCategoryIterator.next();
			}

			RenderUtils.drawBorderedRoundedRect1(this.x - 1, this.y, this.x + this.width + 1, this.y + this.bh + categoryHeight + 4, 10, 1, Theme.getMainColor().getRGB(), Theme.getBackColor().getRGB());
		}

		renderer.drawStringWithShadow(this.n4m ? this.pvp : this.categoryName.name(), this.x + 2, this.y + 4, Theme.getMainColor().getRGB());
		if (!this.n4m) {
			GL11.glPushMatrix();
			renderer.drawStringWithShadow(this.categoryOpened ? "-" : "+", (float) (this.x + marginX), (float) (this.y + marginY), Color.white.getRGB());
			GL11.glPopMatrix();
			if (this.categoryOpened && !this.modulesInCategory.isEmpty()) {
				for (Component c2 : this.modulesInCategory) {
					c2.draw();
				}
			}
		}
	}

	public void r3nd3r() {
		int o = this.bh + 3;

		Component c;
		for (Iterator<ModuleComponent> var2 = this.modulesInCategory.iterator(); var2.hasNext(); o += c.getHeight()) {
			c = var2.next();
			c.setComponentStartAt(o);
		}

	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public void up(int x, int y) {
		if (this.inUse) {
			this.setX(x - this.xx);
			this.setY(y - this.yy);
		}

	}

	public boolean i(int x, int y) {
		return x >= this.x + 92 - 13 && x <= this.x + this.width && y >= this.y + 2.0F && y <= this.y + this.bh + 1;
	}

	public boolean mousePressed(int x, int y) {
		return x >= this.x + 77 && x <= this.x + this.width - 6 && y >= this.y + 2.0F && y <= this.y + this.bh + 1;
	}

	public boolean insideArea(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.bh;
	}

	public String getName() {
		return String.valueOf(modulesInCategory);
	}

}
