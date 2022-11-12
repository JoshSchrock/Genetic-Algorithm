package mainApp;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Class: ChromosomeEditor
 * <br>
 * <br>Purpose: Contains the panels and buttons for the editor interactions. Saves a
 * copy of the current chromosome in memory as geneButtons.
 * <br>
 * <br>Temporary: Saves the mutation() method, will be moved to EvolutionSim in the
 * future.
 * 
 * @author F23-R-401
 * 
 * 
 */

public class ChromosomeEditor extends JFrame {
	private ChromosomeIO fileIO;
	private JPanel chromosomePanel;
	private JPanel settingsPanel;
	private GeneButton[] geneButtons;

	private static final int GRID_ROWS = 0;
	private int gridCols;

	private static final int SETTING_ROWS = 2; // Can change when new setting buttons are added
	private static final int SETTING_COLS = 3;

	/**
	 * ensures: creates two panels for visualization of and interaction with chromosomes
	 */
	public ChromosomeEditor() {
		this.chromosomePanel = new JPanel();
		this.settingsPanel = new JPanel();
		this.fileIO = new ChromosomeIO(this);
		this.add(this.chromosomePanel, BorderLayout.CENTER);
		createChromosomePanel();
		createSettingsPanel();
	}

	/**
	 * ensures: visualizes the chromosome and makes them intractable
	 */
	private void createChromosomePanel() {
		char[] chromosome;
		
		// Try to load a chromosome from a file, and if it is the wrong format, report that to user
		try {
			chromosome = fileIO.loadChromosome();
			this.setTitle(fileIO.getCurFileName());
		} catch (FileNotFoundException fnfe) {
			JOptionPane.showMessageDialog(this, "File Not Found");
			return;
		} catch (InvalidChromosomeFormatException e) {
			JOptionPane.showMessageDialog(this, "Selected File Has Wrong Format Of Genes");
			return;
		}
		
		geneButtons = new GeneButton[chromosome.length];

		// Size grid appropriately according to the number of genes in the loaded chromosome
		if (Math.sqrt(chromosome.length) % 1 == 0) {
			gridCols = (int) Math.sqrt(chromosome.length);
		} else {
			gridCols = (int) Math.sqrt(chromosome.length) + 1;
		}
		chromosomePanel.setLayout(new GridLayout(GRID_ROWS, gridCols));

		// Create a list of gene buttons and populate them to the grid on the JPanel
		for (int i = 0; i < chromosome.length; i++) {
			geneButtons[i] = new GeneButton(chromosome[i], i);
			GeneButton currentGeneButton = geneButtons[i]; // Workaround for anonymous class index
			geneButtons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentGeneButton.changeGeneValue();
				}
			});
			chromosomePanel.add(geneButtons[i]);
		}
	}

	/**
	 * ensures: fills the panel that has all the settings and IO interactions
	 */
	private void createSettingsPanel() {
		settingsPanel.setLayout(new GridLayout(SETTING_ROWS, SETTING_COLS));

		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			
			/**
			 *  ensures: implements the actionPerformed() method of ActionListener so that it clears, loads genes for, and repopulates the chromosomePanel
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				chromosomePanel.removeAll();
				createChromosomePanel();
				chromosomePanel.revalidate();
				chromosomePanel.repaint();
			}
			
		});

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			/**
			 *  ensures: implements the actionPerformed() method of ActionListener so that it saves the genes currently in the geneButtons to a .txt file
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				fileIO.saveChromosome(buttonToBoolArray());
			}
			
		});

		JLabel mutationRateLabel = new JLabel("Mutation Rate: _/N");

		JTextField mutationRateField = new JTextField("Input Double");

		JButton mutateButton = new JButton("Mutate");
		mutateButton.addActionListener(new ActionListener() {

			/**
			 *  ensures: implements the actionPerformed() method of ActionListener so that it mutates the currently loaded genes according to the given mutation rate
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				double inputRate;
				try {
					inputRate = Double.parseDouble(mutationRateField.getText());
					mutate(inputRate / geneButtons.length);
				} catch (NumberFormatException nfe) {
					mutationRateField.setText("");
					System.out.println("Only input a double!");
				}
			}
		});

		settingsPanel.add(mutationRateLabel);
		settingsPanel.add(mutationRateField);
		settingsPanel.add(mutateButton);
		settingsPanel.add(saveButton);
		settingsPanel.add(loadButton);
		this.add(settingsPanel, BorderLayout.SOUTH);
	}

	/**
	 * ensures: mutates the entire chromosome, will get moved to EvolutionSim if needed
	 * @param mutationRate
	 */
	private void mutate(double mutationRate) {
		for (int i = 0; i < geneButtons.length; i++) {
			if (Math.random() <= mutationRate) {
				geneButtons[i].changeGeneValue();
			}
		}
		chromosomePanel.revalidate();
		chromosomePanel.repaint();
	}

	/**
	 *  ensures: converts the currently loaded array of buttons to an array of boolean values
	 *  @return array of type boolean corresponding to the boolean values of the currently loaded array of buttons
	 */
	private char[] buttonToBoolArray() {
		char[] chromosome = new char[geneButtons.length];
		for (int i = 0; i < geneButtons.length; i++) {
			chromosome[i] = geneButtons[i].getGeneValue();
		}
		return chromosome;
	}
}
