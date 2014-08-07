package ffget.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;

public class DescPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3232971808636512872L;

	/**
	 * Create the panel.
	 */
	public DescPanel() {
		setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setBounds(10, 11, 46, 14);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.setBounds(10, 112, 46, 14);
		add(lblNewLabel);

	}

}
