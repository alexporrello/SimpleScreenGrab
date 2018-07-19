package main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jm.JMButton;
import jm.JMLabel;
import jm.JMTextField;

public class Screenshot extends JDialog {
	private static final long serialVersionUID = 3257860364347012082L;

	/** The default save location of any screengrabs **/
	private File defaultLocal = new File(new JFileChooser().getFileSystemView().getDefaultDirectory() + "\\SimpleScreenGrabs");

	/** The button by which the user takes a quick screenshot of the main monitor. **/
	JMButton grab = new JMButton("Grab");
	
	/** The button by which a user takes a screenshot of a specified region of the main monitor. **/
	JMButton rect = new JMButton("  Region  ");
	
	/** Displays the status and path of a capture. **/
	JMLabel status = new JMLabel("Press 'Grab' to Capture.");
	
	/** The field where the user specifies a save name for the file. **/
	JMTextField field = new JMTextField("");

	public Screenshot() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImages(Screenshot.imageIcon());
		setSize(new Dimension(225, 125));
		setLayout(new GridBagLayout());
		setTitle("SimpleScreenGrab");
		setLocationByPlatform(true);
		setAlwaysOnTop(true);
		setResizable(false);

		if(!defaultLocal.exists()) {
			defaultLocal.mkdir();
		}

		addButtonListeners();
		
		int nw = GridBagConstraints.NORTHWEST;

		add(field,  new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, nw, GridBagConstraints.HORIZONTAL, new Insets(3,3,3,3), 0, 0));
		add(grab,   new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, nw, GridBagConstraints.BOTH,       new Insets(0,3,3,3), 0, 0));
		add(rect,   new GridBagConstraints(1, 1, 1, 1, 0.0, 1.0, nw, GridBagConstraints.BOTH,       new Insets(0,0,3,3), 0, 0));
		add(status, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, nw, GridBagConstraints.HORIZONTAL, new Insets(0,3,3,3), 0, 0));

		setVisible(true);
	}

	public void addButtonListeners() {
		field.setToolTipText("Enter the filename here.");
		grab.addActionListener(e -> {
			try {
				setVisible(false);

				writeBufferedImage(new Robot().createScreenCapture(new Rectangle(0, 93, 1920, 955)), 
						defaultLocal.getAbsolutePath() + "\\" + field.getText() + ".png");

				status.setText("Saved as " + field.getText() + ".png");
				setVisible(true);
			} catch (IOException | AWTException f) {
				f.printStackTrace();
			}
		});

		rect.addActionListener(e -> {
			DrawJFrame toDraw = new DrawJFrame();
			toDraw.setVisible(true);

			toDraw.panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {					
					if(SwingUtilities.isLeftMouseButton(e)) {
						try {									
							setVisible(false);
							toDraw.setVisible(false);

							writeBufferedImage(new Robot().createScreenCapture(
									new Rectangle(toDraw.newTopX+1, toDraw.newTopY+1, toDraw.width-1, toDraw.height-1)), 
									defaultLocal.getAbsolutePath() + "\\" + field.getText() + ".png");

							status.setText("Saved as " + field.getText() + ".png");

							toDraw.dispose();
							setVisible(true);
						} catch (IOException | AWTException f) {
							f.printStackTrace();
						}
					} else {
						toDraw.dispose();
					}
				}
			});
		});
		
		status.setToolTipText("To be taken to the last captured file, click here.");
		status.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if(status.getText().contains("Saved as ")) {
						Runtime.getRuntime().exec("explorer.exe /select," + defaultLocal.getAbsolutePath() + "\\" + status.getText().replaceAll("Saved as ", ""));
					} else {
						Runtime.getRuntime().exec("explorer.exe /open," + defaultLocal.getAbsolutePath());
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private static void writeBufferedImage(BufferedImage image, String path) throws FileNotFoundException, IOException {
		JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpegParams.setCompressionQuality(1f);

		final ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
		writer.setOutput(new FileImageOutputStream(new File(path)));
		writer.write(null, new IIOImage(image, null, null), jpegParams);
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
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

	/** Loads image for the method that loads image icons. **/
	private static Image loadImage(String url) {		
		return new ImageIcon(Screenshot.class.getClassLoader().getResource(url)).getImage();
	}
	
	public static void main(String[] args) {
		setLookAndFeel();
		new Screenshot();
	}
}
