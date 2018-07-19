package jm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import main.Tools;

public class JMButton extends JLabel {
	private static final long serialVersionUID = 5773449309163002302L;

	public final static int STYLE_TEXT = 0;
	public final static int STYLE_CLOSE_BUTTON = 1;

	/** Determines whether the button functions or not **/
	private Boolean enabled = true;

	/** Set to true if the mouse has been pressed; else, false. **/
	private Boolean pressed = false;

	/** The color of the button's background **/
	private Color background = JMColor.DEFAULT_BACKGROUND;

	/** The color of the button's border **/
	private Color border = JMColor.DEFAULT_BORDER_COLOR;

	/** Determines what is displayed on the button **/
	private int style = JMButton.STYLE_TEXT;

	/** The timer that controls the fade of the buttons **/
	Timer fadeTimer;

	public JMButton(String s) {
		super(s);

		setupJMButton();
	}

	/**
	 * @param style may be one of the following:
	 * <ul>
	 * 		<li> {@link #STYLE_CLOSE_BUTTON}
	 * <ul>
	 */
	public JMButton(int style) {
		this.style = style;

		setupJMButton();
	}

	public void setupJMButton() {
		setHorizontalAlignment(SwingConstants.CENTER);
		setForeground(JMColor.DEFAULT_FONT_COLOR);
		setFocusable(true);
		setOpaque(false);
		setBorder();

		addFocusListener(createFocusListener());
		addMouseListener(createMouseAdapter());
	}

	public void addActionListener(Consumer<InputEvent> listener) {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if((arg0.getKeyCode() == KeyEvent.VK_ENTER || arg0.getKeyCode() == KeyEvent.VK_SPACE) && enabled) {
					listener.accept(arg0);
					requestFocus(true);
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(contains(arg0.getPoint()) && enabled) {
					listener.accept(arg0);
					requestFocus(true);
				}
			}
		});
	}

	public FocusListener createFocusListener() {
		return new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if(enabled) {
					fadeColor(JMColor.DEFAULT_BACKGROUND, JMColor.HOVER_BORDER_COLOR, 3);
				}

				repaint();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if(enabled) {
					fadeColor(JMColor.DEFAULT_BACKGROUND, JMColor.DEFAULT_BORDER_COLOR, 7);
				}

				repaint();
			}
		};
	}

	private MouseAdapter createMouseAdapter() {
		return new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(enabled) {
					fadeColor(JMColor.HOVER_COLOR, JMColor.HOVER_BORDER_COLOR, 10);
					
					pressed = false;
				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(enabled) {
					fadeColor(JMColor.PRESS_COLOR, border, 7);
					pressed = true;
				}

				repaint();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if(enabled) {
					if(pressed) {
						fadeColor(JMColor.HOVER_COLOR, JMColor.HOVER_BORDER_COLOR, 10);
					} else if(!hasFocus()) {
						fadeColor(JMColor.DEFAULT_BACKGROUND, JMColor.DEFAULT_BORDER_COLOR, 10);
					} else if(hasFocus()) {
						fadeColor(JMColor.DEFAULT_BACKGROUND, JMColor.HOVER_BORDER_COLOR, 10);
					}
				}

				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {				
				if(enabled) {
					if(!pressed) {
						fadeColor(JMColor.HOVER_COLOR, JMColor.HOVER_BORDER_COLOR, 3);
					} else {
						fadeColor(JMColor.PRESS_COLOR, JMColor.HOVER_BORDER_COLOR, 3);
					}
				}

				repaint();
			}
		};
	}

	public void setButtonEnabled(Boolean enable) {
		this.enabled = enable;

		if(this.enabled) {
			setForeground(JMColor.DEFAULT_FONT_COLOR);

			border = JMColor.DEFAULT_BORDER_COLOR;
			background   = JMColor.DEFAULT_BACKGROUND;
		} else {
			setForeground(JMColor.DISABLED_FONT_COLOR);

			border = JMColor.DISABLED_BORDER_COLOR;
			background   = JMColor.DISABLED_BACKGROUND_COLOR;
		}

		setFocusable(this.enabled);
		repaint();
	}

	private void setBorder() {
		Tools.setBorder(this);
	}

	@Override
	public void paintComponent(Graphics g) {		
		Graphics2D gg = (Graphics2D) g;

		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		gg.setColor(background);
		gg.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);//(0, 0, getWidth()-1, getHeight()-1);

		gg.setColor(border);
		gg.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);//Oval(0, 0, getWidth()-1, getHeight()-1);

		if(style == JMButton.STYLE_CLOSE_BUTTON) {
			drawCloseButton(gg);
		} else {
			super.paintComponent(g);
		}
		
//		TODO Possible new method for fading colors without discoloration.
//		float opacity = 0.0f;
//		gg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
//		gg.setColor(Color.GREEN);
//		gg.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);
	}

	/**
	 * Draws the close button if the current style is set to {@link #STYLE_CLOSE_BUTTON}.
	 */
	private void drawCloseButton(Graphics2D gg) {
		gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gg.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		gg.setColor(Color.RED);

		int border = getHeight()/4;

		gg.drawLine(border, border, getWidth()-border, getHeight()-border);
		gg.drawLine(border, getWidth()-border, getHeight()-border, border);
	}
	
	/** ================================== COLOR FADING CODE ================================== **/
	
	
	private void fadeColor(Color fadeToButton, Color fadeToBorder, int fadeSpeed) {
		if(fadeTimer != null && fadeTimer.isRunning()) {
			fadeTimer.stop();
		}
		
		fadeTimer = new Timer(fadeSpeed, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				colorFader(background, fadeToButton, border, fadeToBorder);
				repaint();
			}    
		});

		fadeTimer.start();
	}

	private void colorFader(Color buttonfadeFrom, Color buttonFadeTo, Color borderFadeFrom, Color borderFadeTo) {		
		int[] buttonA = makeRGBArray(buttonfadeFrom);
		int[] buttonB = makeRGBArray(buttonFadeTo);

		int[] borderA = makeRGBArray(borderFadeFrom);
		int[] borderB = makeRGBArray(borderFadeTo);

		buttonA = fader(buttonA, buttonB);
		borderA = fader(borderA, borderB);

		background = arrayToColor(buttonA);
		border     = arrayToColor(borderA);
		
		if((buttonA[0] + "" + buttonA[1] + "" + buttonA[2]).equals(buttonB[0] + "" + buttonB[1] + "" + buttonB[2])) {
			border = borderFadeTo;
			repaint();
			fadeTimer.stop();
		}
	}

	/**
	 * Adds or subtracts 1 so that values of a will equal values of b.
	 * @param a the array whose values are changed
	 * @param b the array whose values are static
	 * @return an updated
	 */
	private int[] fader(int[] a, int[] b) {
		for(int i = 0; i < 3; i++) {
			if(a[i] > b[i]) {
				a[i] = a[i]-1;
			} else if(a[i] < b[i]) {
				a[i] = a[i]+1;
			}
		}

		return a;
	}

	/**
	 * Receives a color and returns the color represented as an array of integers.
	 * @param color the color to be converted
	 * @return an array of size three where...
	 * <ul>
	 * 		<li>0: Red</li>
	 *      <li>1: Green</li>
	 *      <li>2: Blue</li>
	 * </ul>
	 */
	private int[] makeRGBArray(Color color) {
		return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
	}

	/**
	 * Receives an array representation of a color and returns a color.
	 * @param rgb accepts array of integers made in {@link #makeRGBArray(Color)}.
	 * @return the Color represented by rgb.
	 */
	private Color arrayToColor(int[] rgb) {
		return new Color(rgb[0], rgb[1], rgb[2]);
	}
}