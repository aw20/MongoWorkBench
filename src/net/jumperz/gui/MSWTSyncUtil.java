package net.jumperz.gui;


public abstract class MSWTSyncUtil implements Runnable {
	public void run() {
		updateSWT();
	}

	public abstract void updateSWT();
}
