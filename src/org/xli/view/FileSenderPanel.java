package org.xli.view;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class FileSenderPanel extends JPanel {
	private static final long serialVersionUID = 1587932386942251094L;
	
	private JLabel jlblFileSenderInfo;
	private JProgressBar jpgbFileReceiveBar;
	
	public FileSenderPanel() {
	}

	public void initPanel() {
		initPanel(null);
	}
	
	public void initPanel(String defaultText) {
		this.setLayout(null);
		Font defaultFont = new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 16);

		jlblFileSenderInfo = new JLabel(defaultText == null ? "" : defaultText);
		jlblFileSenderInfo.setFont(defaultFont);
		jlblFileSenderInfo.setHorizontalAlignment(JLabel.CENTER);
		jlblFileSenderInfo.setLocation(0, 0);
		jlblFileSenderInfo.setSize(this.getWidth(), 20);
		this.add(jlblFileSenderInfo);
		
		jpgbFileReceiveBar = new JProgressBar();
		jpgbFileReceiveBar.setStringPainted(true);
		jpgbFileReceiveBar.setBounds(0, 24, this.getWidth(), 20);
		this.add(jpgbFileReceiveBar);
	}
	
	public FileSenderPanel setFileInfo(String fileSenderInfo, String fileName) {
		String tempString = "[" + fileSenderInfo + "]:" + fileName;
		jlblFileSenderInfo.setText(tempString);
		return this;
	}
	
	public JProgressBar getJProgressBar() {
		return this.jpgbFileReceiveBar;
	}
}
