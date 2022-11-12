package Milestone2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * 
 * Class: BestChromosomeViewer <br>
 * <br>
 * Purpose: Visualizes the best fit chromosome <br>
 * <br>
 * 
 * @author F23-R-401
 * 
 */

public class BestFitViewer extends JFrame {
	private char[] bestChromosome;
	private JPanel chromosomePanel;

	public static final int WINDOW_WIDTH = 600;
	public static final int WINDOW_HEIGHT = 600;
	private static final int GRID_ROWS = 0;
	private int gridCols;
 
	/**
	 * ensures: creates the panel for visualization of the chromosome
	 */
	public BestFitViewer(Integer id, char[] bestChromosome) {
		this.bestChromosome = bestChromosome;

		// Only needed for viewing on macOS:
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		end mac specific code

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		this.chromosomePanel = new JPanel();
		// Size grid appropriately according to the number of genes in the loaded
		// chromosome
		if (Math.sqrt(this.bestChromosome.length) % 1 == 0) {
			gridCols = (int) Math.sqrt(this.bestChromosome.length);
		} else {
			gridCols = (int) Math.sqrt(this.bestChromosome.length) + 1;
		}
		this.chromosomePanel.setLayout(new GridLayout(GRID_ROWS, gridCols));
		createChromosomePanel();
		this.add(this.chromosomePanel, BorderLayout.CENTER);

		setTitle("Chromosome ID: " + id);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * ensures: The gridlayout panel gets populated with proper color
	 */
	private void createChromosomePanel() {
		// Create a list of JLables and populate them to the grid on the JPanel
		for (int i = 0; i < this.bestChromosome.length; i++) {
			char geneValue = this.bestChromosome[i];
			JLabel gene = new JLabel("" + i);
			gene.setOpaque(true);
			if (geneValue == '1') {
				gene.setBackground(Color.GREEN);
			} else if (geneValue == '0') {
				gene.setBackground(Color.RED);
			} else {
				gene.setBackground(Color.BLUE);
			}
			this.chromosomePanel.add(gene);
		}
	}

	/**
	 * ensures: Setter for best chromosome and recreates and refreshes the panel
	 * @param bestChromosome the bestChromosome to set
	 */
	public void setBestChromosome(Integer id, char[] bestChromosome) {
		this.bestChromosome = bestChromosome;
		chromosomePanel.removeAll();
		createChromosomePanel();
		setTitle("Chromosome ID: " + id);
		chromosomePanel.revalidate();
		chromosomePanel.repaint();
	}

}
