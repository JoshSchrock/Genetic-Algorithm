package Milestone2;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.util.Scanner;

import mainApp.InvalidChromosomeFormatException;

/**
 * 
 * Class: EvolutionInterface
 * @author F23-R-401
 * <br>Purpose: Runs the main method for the genetic algorithm's evolution loop
 */

public class EvolutionInterface {
	
	public static final String[] options = {"No", "Yes", "Cancel"};
	
	public static final double DEFAULT_TERMINATION_CONDITION = 100.0;
	
	/**
	 * ensures: runs the Genetic Algorithm
	 * @param args
	 */
	public static void main(String[] args) {
		
		int choice = JOptionPane.showOptionDialog(null, //Component parentComponent
                "Run EvolutionSim in console?", //Object message,
                "Choose an option", //String title
                JOptionPane.YES_NO_CANCEL_OPTION, //int optionType
                JOptionPane.YES_NO_CANCEL_OPTION, //int messageType
                null, //Icon icon,
                options, //Object[] options,
                "No");//Object initialValue

		if (choice == 0) {
//			Only needed for viewing on macOS:
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
//			end mac specific code
			
			EvolutionGUI GUI = new EvolutionGUI("EvolutionSim");
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			GUI.setSize((int)screenSize.getWidth(), (int)screenSize.getHeight()/2);			
			
			GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			GUI.setVisible(true);
			
		} else if (choice == 1) {
			Scanner inputScanner = new Scanner(System.in);
			
			boolean allFieldsFilled = false;
			while(!allFieldsFilled) {
				boolean validPopSizeEntry = false;
				int popSize = 0;
				while(!validPopSizeEntry) {
					System.out.print("Enter population size: ");
					popSize = inputScanner.nextInt();
					if(popSize > 0) {
						validPopSizeEntry = true;
					} else {
						System.out.println("Invalid population size entered, please try again (number must be greater than 0)");
					}
				}
				
				boolean validLengthEntry = false;
				int chromosomeLength = 0;
				while(!validLengthEntry) {
					System.out.print("Enter chromosome length: ");
					chromosomeLength = inputScanner.nextInt();
					if(chromosomeLength > 0) {
						validLengthEntry = true;
					} else {
						System.out.println("Invalid chromosome length entered, please try again (number must be greater than 0)");
					}
				}
				
				boolean validMutationRateEntry = false;
				double mutationRate = 0.0;
				while(!validMutationRateEntry) {
					System.out.print("Enter mutation rate (0.0 - 1.0): ");
					mutationRate = inputScanner.nextDouble();
					if(mutationRate >= 0.0 && mutationRate <= 1.0) {
						validMutationRateEntry = true;
					} else {
						System.out.println("Invalid mutation rate entered, please try again (number must be between 0.0 and 1.0)");
					}
				}
				
				boolean validFitnessEntry = false;
				String fitnessMode = "Simple";
				while(!validFitnessEntry) {
					System.out.print("Enter fitness mode (Simple, Target, Consecutive, or Learning): ");
					fitnessMode = inputScanner.next();
					if(fitnessMode.equals("Simple") || fitnessMode.equals("Target") || fitnessMode.equals("Consecutive") || fitnessMode.equals("Learning")) {
						validFitnessEntry = true;	
					} else {
						System.out.println("Invalid fitness type entered, please try again");
					}
				}
				
				boolean validSelectionEntry = false;
				String selectionMode = "Truncation";
				while(!validSelectionEntry) {
					System.out.print("Enter selection mode (Truncation, Roulette, or Rank): ");
					selectionMode = inputScanner.next();
					if(selectionMode.equals("Truncation") || selectionMode.equals("Roulette") || selectionMode.equals("Rank")) {
						validSelectionEntry = true;
					} else {
						System.out.println("Invalid selection type entered, please try again");
					}
				}
				
				boolean validDiversityEntry = false;
				String diversityMode = "Hamming";
				while(!validDiversityEntry) {
					System.out.print("Enter diversity measure type (Hamming or Uniqueness): ");
					diversityMode = inputScanner.next();
					if(diversityMode.equals("Hamming") || diversityMode.equals("Uniqueness")) {
						validDiversityEntry = true;
					} else {
						System.out.println("Invalid diversity measure type entered, please try again");
					}
				}
				
				boolean validElitismEntry = false;
				int elitismNum = 0;
				while(!validElitismEntry) {
					System.out.print("Enter elitism number (number of top-fitting chromosomes to be preserved unmutated between generations): ");
					elitismNum = inputScanner.nextInt();
					if(elitismNum >= 0 && elitismNum <= popSize) {
						validElitismEntry = true;
					} else {
						System.out.println("Invalid elitism number entered, please try again (number must be between 0 and the size of the population)");
					}
				}
				
				boolean validCrossoverEntry = false;
				boolean crossoverMode = false;
				while(!validCrossoverEntry) {
					System.out.print("Enable crossover? (y/n): ");
					String crossoverChoice = inputScanner.next();
					if(crossoverChoice.equals("y")) {
						validCrossoverEntry = true;
						crossoverMode = true;
					} else if(crossoverChoice.equals("n")) {
						validCrossoverEntry = true;
						crossoverMode = false;
					} else {
						System.out.println("Invalid response entered, please try again (options are y or n)");
					}
				}
				
				boolean validTerminationEntry = false;
				boolean terminationMode = false;
				double terminationCondition = DEFAULT_TERMINATION_CONDITION;
				while(!validTerminationEntry) {
					System.out.print("Enable a termination condition? (y/n): ");
					String terminationChoice = inputScanner.next();
					if(terminationChoice.equals("y")) {
						validTerminationEntry = true;
						terminationMode = true;
						System.out.print("Enter chromosome fitness to terminate at (0.0 - 100.0): ");
						terminationCondition = inputScanner.nextDouble();
					} else if(terminationChoice.equals("n")) {
						validTerminationEntry = true;
						terminationMode = false;
					} else {
						System.out.println("Invalid response entered, please try again (options are y or n)");
					}
				}
				
				try {
					EvolutionSim sim = new EvolutionSim(popSize, chromosomeLength, mutationRate, fitnessMode, selectionMode, diversityMode, elitismNum, crossoverMode, 0.5);
//					sim.printChromosomes();
					
					System.out.print("Enter number of iterations: ");
					int iterations = inputScanner.nextInt();
					
					int numLoops = 0;
					while(numLoops < iterations || (terminationMode && sim.getBestFit() < terminationCondition)) {
						sim.evolutionLoop();
						numLoops++;
					}
					allFieldsFilled = true;
					
					System.out.println();
					System.out.println("Results:");
					System.out.println("Average Fit: " + sim.getAvgFit() + " Best Fit: " + sim.getBestFit() + " Worst Fit: " + sim.getWorstFit() + " Final Diversity: " + sim.getDiversity());
					System.out.println("-------------------------------------------------------");
					System.out.println();
					
				} catch (InvalidChromosomeFormatException e) {
					JOptionPane.showMessageDialog(new JFrame(), "Selected File Has Incorrect Number or Format of Genes");
				} catch (FileNotFoundException fnfe) {
					JOptionPane.showMessageDialog(new JFrame(), "File Not Found");
				}
				
				// Asks the user if they would like to run the program again (occurs after either finishing the evolution loop or selecting an incorrect target file)
				boolean validRestartEntry = false;
				while(!validRestartEntry) {
					System.out.print("Would you like to run the program again? (y/n): ");
					String restartChoice = inputScanner.next();
					if(restartChoice.equals("y")) {
						validRestartEntry = true;
						allFieldsFilled = false;
					} else if(restartChoice.equals("n")) {
						System.out.println("Goodbye!");
						validRestartEntry = true;
						inputScanner.close();
						System.exit(0);
					} else {
						System.out.println("Invalid response entered, please try again (options are y or n)");
					}
				}
			}

		} else {
			System.exit(0);
		}
	}

}
