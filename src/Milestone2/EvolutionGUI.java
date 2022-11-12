package Milestone2;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mainApp.InvalidChromosomeFormatException;

/**
 * 
 * Class: EvolutionGUI
 * @author F23-R-401
 * <br>Purpose: Used to create and control an evolution simulation and display the results to a GUI
 * <br>For example: 
 * <pre>
 *    EvolutionGUI gui = new EvolutionGUI("EvolutionGUI");
 * </pre>
 */

public class EvolutionGUI extends JFrame {

	private static final int SETTING_ROWS = 3; // Can change when new setting buttons are added
	private static final int SETTING_COLS = 7;
	private static final String INIT_TEXT = "Start";
	private static final String RUNNING_TEXT = "Pause";
	private static final String PAUSED_TEXT = "Run";
	private static final double NO_QUESTION_MARK_RATE = 0.0;
	private static final double QUESTION_MARK_RATE = 0.5;

	private StatsComponent statsComponent;
	private JTextField chromosomeLengthField;
	private JTextField mutationRateField;
	private JCheckBox crossoverSelectionBox;
	private JComboBox<String> selectionComboBox;
	private JComboBox<String> fitnessComboBox;
	private JComboBox<String> diversityComboBox;
	private JTextField elitismField;
	private JTextField populationSizeField;
	private JTextField iterationsField;
	private JTextField terminationField;
	private JCheckBox terminationCheckBox;
	private JButton ppButton;
	private EvolutionSim evolutionSim;
	private int numIterations;
	private double termination;
	private BestFitViewer bestFitViewer;
	private PopulationViewer populationViewer;
	private JButton bestViewerButton;
	private JButton popViewerButton;
	private JPanel settingsPanel;
	private EvolutionThread simThread;

	
	/**
	 * ensures: creates an instance of EvolutionGUI, initializes its title, and creates its settings panel and play/pause button
	 * @param title the title of the window containing the GUI
	 */
	public EvolutionGUI(String title) {
		super(title);
		statsComponent = new StatsComponent();
		this.add(statsComponent, BorderLayout.CENTER);

		this.createSettingsPanel();
		this.createPlayPauseButton();
	}

	/**
	 * ensures: creates the settings panel for the GUI with labels and fields in a grid layout
	 */
	private void createSettingsPanel() {
		settingsPanel = new JPanel();

		settingsPanel.setLayout(new GridLayout(SETTING_ROWS, SETTING_COLS));

		JLabel populationSizeLabel = new JLabel("Population Size: ");
		populationSizeField = new JTextField("");
		JLabel iterationsLabel = new JLabel("Number of iterations: ");
		iterationsField = new JTextField("");
		JLabel chromosomeLengthLabel = new JLabel("Chromosome Length: ");
		chromosomeLengthField = new JTextField("");

		bestViewerButton = new JButton("View Best Fit");
		popViewerButton = new JButton("View Population");
		this.bestViewerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (evolutionSim != null) {
					if (bestFitViewer != null) bestFitViewer.dispose();
					bestFitViewer = new BestFitViewer(evolutionSim.getPopulation().get(0).getId(), evolutionSim.getPopulation().get(0).getChromosomeData().clone());
					simThread.setBestFitViewer(bestFitViewer);
				}
			}
		});
		this.popViewerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (evolutionSim != null) {
					if (populationViewer != null) populationViewer.dispose();
					ArrayList<char[]> list = new ArrayList<char[]>();
					ArrayList<Integer> list2 = new ArrayList<Integer>();
					for (Chromosome chromosome : evolutionSim.getPopulation()) {
						list.add(chromosome.getChromosomeData().clone());
						list2.add(chromosome.getId());
					}
					populationViewer = new PopulationViewer(list2, list);
					simThread.setPopulationViewer(populationViewer);
				}
			}
		});

		settingsPanel.add(populationSizeLabel);
		settingsPanel.add(populationSizeField);
		settingsPanel.add(chromosomeLengthLabel);
		settingsPanel.add(chromosomeLengthField);
		settingsPanel.add(iterationsLabel);
		settingsPanel.add(iterationsField);
		settingsPanel.add(bestViewerButton);

		JLabel mutationRateLabel = new JLabel("Mutation Rate (0.0 - 1.0): ");
		mutationRateField = new JTextField("");
		crossoverSelectionBox = new JCheckBox("Crossover");
		JLabel terminationLabel = new JLabel("Termination Condition (0.0 - 100.0): ");
		terminationField = new JTextField("100.0");
		terminationCheckBox = new JCheckBox("Termination Active");

		settingsPanel.add(mutationRateLabel);
		settingsPanel.add(mutationRateField);
		settingsPanel.add(terminationLabel);
		settingsPanel.add(terminationField);
		settingsPanel.add(terminationCheckBox);
		settingsPanel.add(crossoverSelectionBox);
		settingsPanel.add(popViewerButton);

		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridLayout(1, 2));
		JLabel selectionLabel = new JLabel("Selection Type: ");
		String[] selectionChoices = { "Truncation", "Roulette", "Rank" };
		selectionComboBox = new JComboBox<String>(selectionChoices);
		selectionPanel.add(selectionLabel, BorderLayout.WEST);
		selectionPanel.add(selectionComboBox, BorderLayout.EAST);
		
		JPanel fitnessPanel = new JPanel();
		fitnessPanel.setLayout(new GridLayout(1, 2));
		JLabel fitnessLabel = new JLabel("Fitness Type: ");
		String[] fitnessChoices = { "Simple", "Target", "Consecutive", "Learning", "Phenotype" };
		fitnessComboBox = new JComboBox<String>(fitnessChoices);
		fitnessPanel.add(fitnessLabel, BorderLayout.WEST);
		fitnessPanel.add(fitnessComboBox, BorderLayout.EAST);
		
		JPanel diversityPanel = new JPanel();
		diversityPanel.setLayout(new GridLayout(1, 2));
		JLabel diversityLabel = new JLabel("Diversity Type: ");
		String[] diversityChoices = {"Hamming", "Uniqueness", "Sorensen-Dice"};
		diversityComboBox = new JComboBox<String>(diversityChoices);
		diversityPanel.add(diversityLabel, BorderLayout.WEST);
		diversityPanel.add(diversityComboBox, BorderLayout.EAST);
		JLabel elitismLabel = new JLabel("Elitism (0 - N): ");
		elitismField = new JTextField("0");

		settingsPanel.add(selectionPanel);
		settingsPanel.add(fitnessPanel);
		settingsPanel.add(diversityPanel);
		settingsPanel.add(elitismLabel);
		settingsPanel.add(elitismField);
		settingsPanel.add(new JPanel());

		this.createPlayPauseButton();
		settingsPanel.add(this.ppButton);

		this.add(settingsPanel, BorderLayout.SOUTH);
	}

	/**
	 * ensures: sets up the play and pause button
	 */
	private void createPlayPauseButton() {
		this.ppButton = new JButton(INIT_TEXT);
		this.ppButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ppButton.getText().equals(INIT_TEXT)) {
					if (bestFitViewer != null) bestFitViewer.dispose();
					bestFitViewer = null;
					if (populationViewer != null) populationViewer.dispose();
					populationViewer = null;
					int popSize;
					int chromosomeLength;
					double mutationRate;
					int elitism;
					try {
						popSize = Integer.parseInt(populationSizeField.getText());
					} catch (NumberFormatException nfe) {
						populationSizeField.setText("");
						JOptionPane.showMessageDialog(new JFrame(), "Only input an integer for population size!");
						System.err.println(nfe);
						return;
					} 
					try {
						chromosomeLength = Integer.parseInt(chromosomeLengthField.getText());
					} catch (NumberFormatException nfe) {
						chromosomeLengthField.setText("");
						JOptionPane.showMessageDialog(new JFrame(), "Only input an integer for chromosome length!");
						System.err.println(nfe);
						return;
					}
					try {
						mutationRate = Double.parseDouble(mutationRateField.getText());
					} catch (NumberFormatException nfe) {
						mutationRateField.setText("");
						JOptionPane.showMessageDialog(new JFrame(), "Only input a double for mutation!");
						System.err.println(nfe);
						return;
					}
					try {
						numIterations = Integer.parseInt(iterationsField.getText());
					} catch (NumberFormatException nfe) {
						iterationsField.setText("");
						JOptionPane.showMessageDialog(new JFrame(), "Only input an integer for the number of iterations!");
						System.err.println(nfe);
						return;
					}
					try {
						elitism = Integer.parseInt(elitismField.getText());
					} catch (NumberFormatException nfe) {
						elitismField.setText("");
						JOptionPane.showMessageDialog(new JFrame(), "Only input an integer for elitism!");
						System.err.println(nfe);
						return;
					}
					try {
						termination = Double.parseDouble(terminationField.getText());
					} catch (NumberFormatException nfe) {
						terminationField.setText("");
						JOptionPane.showMessageDialog(new JFrame(), "Only input a double for termination condition!");
						System.err.println(nfe);
						return;
					}
					
					double questionRate = NO_QUESTION_MARK_RATE;
					if (fitnessComboBox.getSelectedItem().equals("Learning")) {
						questionRate = QUESTION_MARK_RATE;
					}
					
					statsComponent.reset();
					statsComponent.setIteration(numIterations);
					try {
						evolutionSim = new EvolutionSim(popSize, chromosomeLength, mutationRate,
							(String) fitnessComboBox.getSelectedItem(),(String) selectionComboBox.getSelectedItem(),
							(String) diversityComboBox.getSelectedItem(), elitism, crossoverSelectionBox.isSelected(), questionRate);
					} catch (InvalidChromosomeFormatException icfe) {
						JOptionPane.showMessageDialog(new JFrame(),
								"Selected File Has Incorrect Number or Format of Genes");
						System.err.println(icfe.getMessage());
					} catch (FileNotFoundException fnfe) {
						JOptionPane.showMessageDialog(new JFrame(), "File Not Found");
						System.err.println(fnfe.getMessage());
					}
					simThread = new EvolutionThread(ppButton, statsComponent, bestViewerButton, popViewerButton, numIterations, termination, terminationCheckBox.isSelected(), evolutionSim);
					simThread.execute();					
				} else if (ppButton.getText().equals(RUNNING_TEXT)) {
					simThread.pause();
				} else {
					simThread.resume();
				}
				ppButton.revalidate();
				ppButton.repaint();
				ppButton.paintImmediately(ppButton.getVisibleRect());
				settingsPanel.revalidate();
				settingsPanel.repaint();
				revalidate();
				repaint();
			}
		});
	}

}
