package mainApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 * Class: GeneButton
 * @author F23-R-401
 * <br>Purpose: A button that can be tiled and clicked to change value
 * <br>For example: 
 * <pre>
 *    GeneButton button = new GeneButton(true, 32);
 * </pre>
 */
public class GeneButton extends JButton {

	public static final Color TRUE_COLOR = new Color(0, 255, 0);
	public static final Color FALSE_COLOR = new Color(255, 0, 0);
	public static final Color UNKNOWN_COLOR = new Color(0, 0, 255);
	public static final Color TEXT_COLOR = new Color(0, 0, 0);
	public static final Font TEXT_FONT = new Font("Ariel", 16, 16);
	public static final int TEXT_SIZE = 16;

	private char value;
	private int index;

	/**
	 * ensures: creates an instance of GeneButton with the given value and index
	 * @param value
	 * @param index
	 */
	public GeneButton(char value, int index) {
		super();
		this.value = value;
		this.index = index;
		this.addMouseListener (new MouseAdapter(){
		    public void mouseClicked(MouseEvent e){
		    	if (SwingUtilities.isRightMouseButton(e)) {
		    		makeAmbiguous();
		    		revalidate();
		    		repaint();
		    	}
		    }
		});
	}

	/**
	 * ensures: displays the index of the button and changes the button color based on the value of the gene
	 * @param g
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setFont(TEXT_FONT);
		g2.setColor(TEXT_COLOR);
		g2.drawString("i: " + this.index, TEXT_SIZE, TEXT_SIZE);
		if (this.value == '1') {
			this.setBackground(TRUE_COLOR);
			g2.drawString("V: 1", TEXT_SIZE, 2*TEXT_SIZE);
		} else if (this.value == '0'){
			this.setBackground(FALSE_COLOR);
			g2.drawString("V: 0", TEXT_SIZE, 2*TEXT_SIZE);
		} else {
			this.setBackground(UNKNOWN_COLOR);
			g2.drawString("V: ?", TEXT_SIZE, 2*TEXT_SIZE);
		}
	}
	
	/**
	 * ensures: changes gene value (false to true and true to false)
	 */
	public void changeGeneValue() {
		if (this.value == '1') {
			this.value = '0';
		} else if (this.value == '0') {
			this.value = '1';
		}
	}
	
	/**
	 * ensures: changes gene value to '?' or if already '?' to '0'.
	 */
	public void makeAmbiguous() {
		if (this.value != '?') {
			this.value = '?';
		} else {
			this.value = '0';
		}
	}

	/**
	 * ensures: returns this GeneButton's value
	 * @return boolean
	 */
	public char getGeneValue() {
		return this.value;
	}
	
}