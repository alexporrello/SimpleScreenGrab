package jm;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.SwingConstants;

public class JMLabel extends JMTextField {
	private static final long serialVersionUID = -2299250539917011267L;

	public JMLabel(String s) {
		super(s);

		setupJMLabel();
		setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public JMLabel(String s, int horizontalAlignment) {
		super(s);
		
		setupJMLabel();
		setHorizontalAlignment(horizontalAlignment);
	}
	
	private void setupJMLabel() {
		setSelectionColor(getBackground());
		setSelectedTextColor(getForeground());
		setCaretColor(getBackground());
		setEditable(false);
		setHighlighter(null);
		
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				setCaretPosition(0);
			}
		});
	}
}
