package org.xli.control;

import javax.swing.JProgressBar;

public class ProgressShowThread extends Thread {
	private volatile int percentCount;
	private JProgressBar bar;

	public ProgressShowThread(JProgressBar bar) {
		this.bar = bar;
	}
	
	@Override
	public void run() {
		while (true) {
			bar.setValue(percentCount);			
		}
	}
	
	public ProgressShowThread setPercentCount(int percentCount) {
		this.percentCount = percentCount;
		return this;
	}
}
