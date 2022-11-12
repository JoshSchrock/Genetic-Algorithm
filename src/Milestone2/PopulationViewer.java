package Milestone2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import mainApp.ChromosomeEditor;
import mainApp.GeneButton;

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

public class PopulationViewer extends JFrame {
	private ArrayList<char[]> population;
	private JPanel populationPanel;
	private ArrayList<Integer> ids;

	public static final int WINDOW_WIDTH = 1200;
	public static final int WINDOW_HEIGHT = 1200;
	private static final int GRID_ROWS = 0;
	private int gridCols;

	/**
	 * ensures: creates the panel for visualization of the chromosome
	 */
	public PopulationViewer(ArrayList<Integer> ids, ArrayList<char[]> population) {
		this.population = population;
		this.ids = ids;

		// Only needed for viewing on macOS:
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		end mac specific code

		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		this.populationPanel = new JPanel();
		// Size grid appropriately according to the number of genes in the loaded
		// chromosome
		if (Math.sqrt(this.population.size()) % 1 == 0) {
			gridCols = (int) Math.sqrt(this.population.size());
		} else {
			gridCols = (int) Math.sqrt(this.population.size()) + 1;
		}
		this.populationPanel.setLayout(new GridLayout(GRID_ROWS, gridCols, 1, 1));
		
		if (Math.sqrt(this.population.get(0).length) % 1 == 0) {
			gridCols = (int) Math.sqrt(this.population.get(0).length);
		} else {
			gridCols = (int) Math.sqrt(this.population.get(0).length) + 1;
		}
		
		createPopulationPanel();
		
		this.add(this.populationPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * ensures: Create smaller JPanels to represent individuals and populate them to the grid on the JPanel
	 */
	private void createPopulationPanel() {
		for (int i = 0; i < this.population.size(); i++) {
			JPanel chromosomePanel = new JPanel();
			createChromosomePanel(chromosomePanel, i);
			populationPanel.add(chromosomePanel);
		}
	}

	/**
	 * ensures: The jPanel passed in gets populated with the genes to represent a chromosome
	 * @param jPanel The panel to be populated
	 * @param index The index of the chromosome
	 */
	private void createChromosomePanel(JPanel jPanel, int index) {
		jPanel.setLayout(new GridLayout(GRID_ROWS, gridCols));
		for (int i = 0; i < this.population.get(index).length; i++) {
			char geneValue = this.population.get(index)[i];
			JLabel gene = new JLabel();
			gene.setOpaque(true);
			if(geneValue == '1') {
				gene.setBackground(Color.GREEN);
			} else if (geneValue == '0') {
				gene.setBackground(Color.RED);
			} else {
				gene.setBackground(Color.BLUE);
			}
			jPanel.add(gene);
		}
	}

	/**
	 * ensures: Setter for the IDs and population, recreates and refreshes the panel
	 * @param ids The ids of chromosomes
	 * @param population The population data
	 */
	public void setPopulation(ArrayList<Integer> ids, ArrayList<char[]> population) {
		this.population = population;
		this.ids = ids;
		populationPanel.removeAll();
		createPopulationPanel();
		populationPanel.revalidate();
		populationPanel.repaint();
	}
}
