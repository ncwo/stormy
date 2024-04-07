package me.tryfle.stormy.utils;

public class CPSHandler {
	public int leftCps = 0;

	public int rightCps = 0;

	public int getLeftCps() {
		return this.leftCps;
	}

	public int getRightCps() {
		return this.rightCps;
	}

	public static CPSHandler INSTANCE = new CPSHandler();
}
