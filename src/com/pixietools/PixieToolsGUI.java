package com.pixietools;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

public class PixieToolsGUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	

	public class TabbedPanePractice extends JPanel
	{
		public TabbedPanePractice()
		{
			super(new GridLayout(1,1));
			JTabbedPane tabbedPane = new JTabbedPane();
			
			JComponent panel1 = makeTextPanel("Panel #1");
			tabbedPane.addTab("Tab 1", panel1, "Does nothing");
			tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
			
			JComponent panel2 = makeTextPanel("Panel #2");
			tabbedPane.addTab("Tab 2", panel2, "Does twice as much nothing");
			tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
			
			JComponent panel3 = makeTextPanel("Panel #3");
			tabbedPane.addTab("Tab 3", panel3, "Still does nothing");
			tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
			
			JComponent panel4 = makeTextPanel("Panel #4 (size 410 x 50)");
			panel4.setPreferredSize(new Dimension(410, 50));
			tabbedPane.addTab("Tab 4", panel4, "does nothing at all");
			tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
			
			// add tabbed pane to this panel
			add(tabbedPane);
			
			// enables use of scroll
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		}
		
		protected JComponent makeTextPanel(String text)
		{
			JPanel panel = new JPanel(false);
			JLabel filler = new JLabel(text);
			filler.setHorizontalAlignment(JLabel.CENTER);
			panel.setLayout(new GridLayout(1,1));
			panel.add(filler);
			return panel;
		}
		
	}
	
	private static void createAndShowGUI() 
	{
		// create and set up window
		JFrame frame =  new JFrame("PixieToolsWindow");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// add content to window
		frame.add(new TabbedPanePractice(), BorderLayout.CENTER);
		
		// display window
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PixieToolsGUI window = new PixieToolsGUI();
					window.frame.setVisible(true);
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PixieToolsGUI() {
		private void initialize() 
			frame = new JFrame();
			frame.setBounds(100, 100, 450, 300);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().setLayout(null);

	}

	/**
	 * Initialize the contents of the frame.
	 */

	
}
