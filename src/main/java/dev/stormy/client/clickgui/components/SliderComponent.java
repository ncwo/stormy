package dev.stormy.client.clickgui.components;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.opengl.GL11;

import dev.stormy.client.clickgui.Component;
import dev.stormy.client.module.setting.impl.SliderSetting;
import net.minecraft.client.Minecraft;

public class SliderComponent implements Component {
	private final SliderSetting v;
	private final ModuleComponent p;
	private int o;
	private int x;
	private int y;
	private boolean d = false;
	private double w;

	public SliderComponent(SliderSetting v, ModuleComponent b, int o) {
		this.v = v;
		this.p = b;
		this.x = b.category.getX() + b.category.getWidth();
		this.y = b.category.getY() + b.o;
		this.o = o;
	}

	@Override
	public void draw() {
		net.minecraft.client.gui.Gui.drawRect(this.p.category.getX() + 4, this.p.category.getY() + this.o + 11, this.p.category.getX() + 4 + this.p.category.getWidth() - 8, this.p.category.getY() + this.o + 15, -12302777);
		int l = this.p.category.getX() + 4;
		int r = this.p.category.getX() + 4 + (int) this.w;
		if (r - l > 84) {
			r = l + 84;
		}

		net.minecraft.client.gui.Gui.drawRect(l, this.p.category.getY() + this.o + 11, r, this.p.category.getY() + this.o + 15, Color.white.getRGB());
		GL11.glPushMatrix();
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.v.getName() + ": " + this.v.getInput(), ((int) ((this.p.category.getX() + 4) * 2.0F)), ((int) ((this.p.category.getY() + this.o + 3) * 2.0F)), -1);
		GL11.glPopMatrix();
	}

	@Override
	public void setComponentStartAt(int n) {
		this.o = n;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void update(int mousePosX, int mousePosY) {
		this.y = this.p.category.getY() + this.o;
		this.x = this.p.category.getX();
		double d = Math.min(this.p.category.getWidth() - 8, Math.max(0, mousePosX - this.x));
		this.w = (this.p.category.getWidth() - 8) * (this.v.getInput() - this.v.getMin()) / (this.v.getMax() - this.v.getMin());
		if (this.d) {
			if (d == 0.0D) {
				this.v.setValue(this.v.getMin());
			} else {
				double n = r(d / (this.p.category.getWidth() - 8) * (this.v.getMax() - this.v.getMin()) + this.v.getMin(), 2);
				this.v.setValue(n);
			}
		}

	}

	private static double r(double v, int p) {
		if (p < 0) {
			return 0.0D;
		} else {
			BigDecimal bd = new BigDecimal(v);
			bd = bd.setScale(p, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}

	@Override
	public void mouseDown(int x, int y, int b) {
		if (this.u(x, y) && b == 0 && this.p.po) {
			this.d = true;
		}

		if (this.i(x, y) && b == 0 && this.p.po) {
			this.d = true;
		}

	}

	@Override
	public void mouseReleased(int x, int y, int m) {
		this.d = false;
	}

	@Override
	public void keyTyped(char t, int k) {

	}

	public boolean u(int x, int y) {
		return x > this.x && x < this.x + this.p.category.getWidth() / 2 + 1 && y > this.y && y < this.y + 16;
	}

	public boolean i(int x, int y) {
		return x > this.x + this.p.category.getWidth() / 2 && x < this.x + this.p.category.getWidth() && y > this.y && y < this.y + 16;
	}
}
