package main;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Screenshot extends JFrame {
	private static final long serialVersionUID = 3257860364347012082L;

	int xTop = -1;
	int yTop = -1;

	int xBot = -1;
	int yBot = -1;

	JPanel drawPanel;

	File defaultLocal = new File("C:\\Users\\porrello\\Pictures\\ScreenGrabs");

	public Screenshot() {
		setLookAndFeel();

		JDialog dialog = new JDialog();
		dialog.setTitle("SimpleScreenGrab");
		dialog.setAlwaysOnTop(true);
		dialog.setSize(new Dimension(200, 100));
		dialog.setIconImages(Screenshot.imageIcon());
		dialog.setLocationByPlatform(true);

		JTextField field = new JTextField("");
		dialog.add(field, BorderLayout.NORTH);

		JButton capture = new JButton("Grab");
		dialog.add(capture, BorderLayout.CENTER);

		JButton captureRect = new JButton("Region");
		dialog.add(captureRect, BorderLayout.EAST);

		JLabel status = new JLabel("Press 'Grab' to Capture.");
		dialog.add(status, BorderLayout.SOUTH);

		capture.addActionListener(e -> {
			try {
				dialog.setVisible(false);

				writeBufferedImage(new Robot().createScreenCapture(new Rectangle(0, 93, 1920, 955)), 
						defaultLocal.getAbsolutePath() + "\\" + field.getText() + ".png");

				status.setText("Saved as " + field.getText() + ".png");
				dialog.setVisible(true);
			} catch (IOException | AWTException f) {
				f.printStackTrace();
			}
		});
		captureRect.addActionListener(e -> {
			DrawJFrame toDraw = new DrawJFrame();
			toDraw.setVisible(true);

			toDraw.panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {					
					if(SwingUtilities.isLeftMouseButton(e)) {
						try {									
							dialog.setVisible(false);
							toDraw.setVisible(false);

							writeBufferedImage(new Robot().createScreenCapture(new Rectangle(toDraw.newTopX+1, toDraw.newTopY+1, toDraw.width-1, toDraw.height-1)), 
									defaultLocal.getAbsolutePath() + "\\" + field.getText() + ".png");

							status.setText("Saved as " + field.getText() + ".png");

							toDraw.dispose();
							dialog.setVisible(true);
						} catch (IOException | AWTException f) {
							f.printStackTrace();
						}
					} else {
						toDraw.dispose();
					}
				}
			});
		});

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	public Screenshot(Boolean because) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setOpacity(.9f);

		drawPanel = new JPanel() {
			private static final long serialVersionUID = -4962778622132458985L;

			@Override
			public void paintComponents(Graphics g) {
				super.paintComponents(g);

				if(xTop != -1 && yTop != -1) {
					g.setColor(Color.RED);
					g.fillRect(xTop, yTop, xBot-xTop, yBot-yTop);
				}
			}
		};
		add(drawPanel, BorderLayout.CENTER);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					setOpacity(0f);

					writeBufferedImage(new Robot().createScreenCapture (
							new Rectangle(xTop, yTop, arg0.getX()-xTop, arg0.getY()-yTop)),
							"C:\\Users\\porrello\\Desktop\\shot.png");

					xTop = -1;
					yTop = -1;

					System.out.println(xTop + ", " + yTop + ", " + (arg0.getX()-xTop) + ", " + (arg0.getY()-yTop));

					dispose();
				} catch (IOException | AWTException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				xTop = arg0.getX();
				yTop = arg0.getY();

				System.out.println(arg0.getX() + " ," + arg0.getY());

				drawPanel.repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				xBot = arg0.getX();
				yBot = arg0.getY();

				drawPanel.repaint();
			}
		});

		setVisible(true);
	}

	public static void writeBufferedImage(BufferedImage image, String path) throws FileNotFoundException, IOException {
		JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpegParams.setCompressionQuality(1f);

		final ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
		writer.setOutput(new FileImageOutputStream(new File(path)));
		writer.write(null, new IIOImage(image, null, null), jpegParams);
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Screenshot();
	}

	/** Creates the image icons that will be displayed on the app's taskbar **/
	public static ArrayList<Image> imageIcon() {
		ArrayList<Image> icons = new ArrayList<Image>();

		icons.add(loadImage("257251_1450939297_9_256x256.png"));
		icons.add(loadImage("257251_1450939297_9_128x128.png"));
		icons.add(loadImage("257251_1450939297_9_48x48.png"));
		icons.add(loadImage("257251_1450939297_9_32x32.png"));
		icons.add(loadImage("257251_1450939297_9_16x16.png"));

		return icons;
	}

	private static Image loadImage(String url) {		
		return new ImageIcon(Screenshot.class.getClassLoader().getResource(url)).getImage();
	}

}

