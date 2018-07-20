package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DrawJFrame extends JFrame {
	private static final long serialVersionUID = 9164108149837840283L;

	int xTop = -1;
	int yTop = -1;

	int xBot = -1;
	int yBot = -1;

	int newTopX = 0;
	int newTopY = 0;
	int width   = 0;
	int height  = 0;

	public JPanel panel = new JPanel() {
		private static final long serialVersionUID = 8197463106772733806L;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			int topX;
			int topY;
			int botX;
			int botY;

			if(xTop < xBot) {
				topX = xTop;
				botX = xBot;
			} else {
				topX = xBot;
				botX = xTop;
			}

			if(yTop < yBot) {
				topY = yTop;
				botY = yBot;
			} else {
				topY = yBot;
				botY = yTop;
			}

			width   = botX-topX;
			height  = botY-topY;
			newTopX = topX;
			newTopY = topY;

			g.setColor(Color.BLUE);
			g.drawRect(newTopX, newTopY, width, height);
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), newTopY);
			g.fillRect(0, newTopY+height, getWidth(), newTopY+getHeight());
			g.fillRect(0, 0, newTopX, getHeight());
			g.fillRect(newTopX+width, 0, getWidth(), getHeight());
		}
	};
	
	public void reset() {
		xTop = -1;
		yTop = -1;

		xBot = -1;
		yBot = -1;

		newTopX = 0;
		newTopY = 0;
		width   = 0;
		height  = 0;
		
		panel.repaint();
	}

	public DrawJFrame() {
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setOpacity(.3f);

		add(panel, BorderLayout.CENTER);
		panel.setOpaque(false);

		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					xTop = e.getX();
					yTop = e.getY();
				} else {
					reset();
				}
			}
		});

		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				xBot = e.getX();
				yBot = e.getY();

				panel.repaint();
			}
		});
	}

	public static void main(String[] args) {
		new DrawJFrame().setVisible(true);
	}
}
