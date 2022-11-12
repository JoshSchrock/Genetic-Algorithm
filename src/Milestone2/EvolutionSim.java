package Milestone2;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JFrame;

import mainApp.ChromosomeIO;
import mainApp.InvalidChromosomeFormatException;

/**
 * 
 * Class: EvolutionSim
 * @author F23-R-401
 * <br>Purpose: Used to simulate evolution by generating a population of chromosomes and then performing an evolutionary loop on them
 * <br>For example: 
 * <pre>
 *    EvolutionSim sim = new EvolutionSim(100, 100, 0.01, "Simple", "Truncation", "Hamming", 5, true, 0.0);
 * </pre>
 */

public class EvolutionSim {

	private ChromosomeIO fileIO;
	private ArrayList<Chromosome> population;
	private ArrayList<Chromosome> elites;
	private double mutationRate;
	private String fitnessMode;
	private String selectionMode;
	private String diversityMode;
	private int elitismNum;
	private boolean crossoverMode;
	private char[] targetData;
	private Random r;
	private int chromosomeLength;
	private double bestFit;
	private double worstFit;
	private double averageFit;
	private double diversity;
	private double percentQs;
	private double percent0s;
	private double percent1s;
	
	/**
	 * ensures: creates an instance of EvolutionSim and initializes each of the parameters
	 * @param popSize the size of the population to be created
	 * @param chromosomeLength the length of the chromosomes to be created
	 * @param mutationRate the rate at which mutations will occur (from 0.00 to 1.00)
	 * @param fitnessMode the selected mode by which a chromosome's fitness will be calculated
	 * @param selectionMode the selected mode by which chromosomes will be selected from the population for reproduction
	 * @param diversityMode the selected mode by which measures of diversity within the population will be calculated
	 * @param elitismNum the number of chromosomes to be set aside as "elites" before selection and mutation occur
	 * @param crossoverMode determines whether or not crossover will occur in this simulation
	 * @param qRate the rate of '?' alleles to be generated in this simulation's population
	 */
	public EvolutionSim(int popSize, int chromosomeLength, double mutationRate, String fitnessMode,
			String selectionMode, String diversityMode, int elitismNum, boolean crossoverMode, double qRate)
			throws FileNotFoundException, InvalidChromosomeFormatException {
		this.fileIO = new ChromosomeIO(new JFrame());
		this.mutationRate = mutationRate;
		this.fitnessMode = fitnessMode;
		this.selectionMode = selectionMode;
		this.diversityMode = diversityMode;
		this.elitismNum = elitismNum;
		this.crossoverMode = crossoverMode;
		this.chromosomeLength = chromosomeLength;
		this.population = newRandomPopulation(popSize, chromosomeLength, qRate);
		this.elites = new ArrayList<Chromosome>();
		if (this.fitnessMode.equals("Target") || this.fitnessMode.equals("Phenotype")) {
			this.targetData = fileIO.loadChromosome();
			if (this.targetData.length != chromosomeLength) {
				throw new InvalidChromosomeFormatException(chromosomeLength, this.targetData.length);
			}
		}
		this.sortChromosomes(fitnessMode);
		this.r = new Random();
	}

	/**
	 * ensures: runs all actions in one evolution
	 */
	public void evolutionLoop() {
		this.selectChromosomes(this.selectionMode, this.elitismNum);
		this.mutateChromosomes(this.crossoverMode);
		this.sortChromosomes(fitnessMode);
		this.calculateDiversity(this.diversityMode);
	}

	/**
	 * ensures: sorts chromosome based on the fitness type and sets the best, worst,
	 * and average fit fields
	 * @param fitnessMode a String indicating what fitness mode to use
	 */
	public void sortChromosomes(String fitnessMode) {
		for (Chromosome chromosome : population) {
			if (fitnessMode.equals("Target") || this.fitnessMode.equals("Phenotype")) {
				chromosome.setTargetData(targetData);
			}
			chromosome.calculateFit(fitnessMode);
		}
		Collections.sort(this.population);

		this.bestFit = this.population.get(0).getFitness();
		this.worstFit = this.population.get(this.population.size() - 1).getFitness();
		this.averageFit = this.calculateAverageFit();
		double[] numberList = this.calculatePercent01Qs();
		this.percent0s = numberList[0];
		this.percent1s = numberList[1];
		this.percentQs = numberList[2];
	}

	/**
	 * ensures: chooses selection and elites
	 * @param selectionMode a String indicating what selection mode to use
	 * @param elitismNum an int indicating how many chromsomes to preserve as "elites"
	 */
	public void selectChromosomes(String selectionMode, int elitismNum) {
		for (int i = 0; i < elitismNum; i++) {
			this.elites.add(this.population.get(i));
		}
		this.population.removeAll(this.elites);

		if (selectionMode.equals("Truncation")) {
			this.truncateSelect();
		} else if (selectionMode.equals("Roulette")) {
			this.rouletteSelect();
		} else {
			this.rankSelect();
		}
	}

	/**
	 * ensures: mutates the chromosomes and performs crossover if selected
	 * @param crossoverMode a boolean indicating whether or not to enable crossover
	 */
	public void mutateChromosomes(boolean crossoverMode) {
		if (crossoverMode) {
			// perform crossover between the chromosomes at index 0 and 1 in
			// this.chromosomes
			for (int i = 0; i < population.size() / 2; i += 2) {
				char[] parent1 = this.population.get(i).getChromosomeData().clone();
				char[] parent2 = this.population.get(i + 1).getChromosomeData().clone();
				char[] child1 = this.population.get(i).getChromosomeData().clone();
				char[] child2 = this.population.get(i + 1).getChromosomeData().clone();
				int par1id = this.population.get(i).getId();
				int par2id = this.population.get(i + 1).getId();
				int crosspoint = this.r.nextInt(this.chromosomeLength);

				for (int j = crosspoint; j < this.chromosomeLength; j++) {
					child1[j] = parent2[j];
					child2[j] = parent1[j];
				}
				this.population.set(i, new Chromosome(par1id, child1));
				this.population.set(i + 1, new Chromosome(par2id, child2));
			}
		}

		// have the chromosomes mutate themselves
		for (int i = 0; i < this.population.size(); i++) {
			this.population.get(i).mutate(this.mutationRate);
		}

		// return the selected elite chromosomes to this.chromosomes and emptyout
		// this.elites to complete the evolutionary loop
		this.population.addAll(this.elites);
		this.elites.clear();
	}

	/**
	 * ensures: generates a new pseudorandom population of size popSize comprised of
	 * chromosomes of size chromosomeLength, all using the given seed
	 * 
	 * @param popSize          the number of chromosomes in the population
	 * @param chromosomeLength the length of the chromosomes in the population
	 * @param seed             the seed to be used by the random generator to
	 *                         produce the requested population
	 */
	public ArrayList<Chromosome> newRandomPopulation(int popSize, int chromosomeLength, double qRate) {
		ArrayList<Chromosome> randPop = new ArrayList<Chromosome>();
		for (int i = 0; i < popSize; i++) {
			randPop.add(new Chromosome(i, chromosomeLength, qRate));
		}
		return randPop;
	}

	/**
	 * ensures: prints the chromosomes in the population
	 */
	public void printChromosomes() {
		for (Chromosome chromosome : this.population) {
			chromosome.printChromosome();
			System.out.println(chromosome.getFitness());
		}
	}

	/**
	 * ensures: removes chromosomes in bottom of the list in terms of fitness
	 */
	public void truncateSelect() {
		// remove half the population
		int start = this.population.size() - 1;
		int stop = (this.population.size() / 2) - 1;
		for (int i = start; i > stop; i--) {
			this.population.remove(i);
		}

		// refill the empty half of the population with copies of the selected half
		stop = this.population.size();
		for (int i = 0; i < stop; i++) {
			this.population.add(new Chromosome(population.get(i).getId(), population.get(i).getChromosomeData()));
		}
	}

	/**
	 * ensures: removes chromosomes based on a chance with higher fitness
	 * chromosomes having a higher chance of being selected
	 */
	public void rouletteSelect() {
		ArrayList<Chromosome> newGeneration = new ArrayList<>();
		Random r = new Random();
		Double totalFit = 0.0;

		for (int i = 0; i < population.size(); i++) {
			totalFit += population.get(i).getFitness();
		}

		for (int i = 0; i < population.size(); i++) {
			Double partialSum = r.nextDouble(totalFit);
			int chromosomeIndex = -1; // first while iteration at index 0 sets this to 0
			while (partialSum < totalFit) {
				chromosomeIndex++;
				partialSum += population.get(chromosomeIndex).getFitness();
			}
			newGeneration.add(new Chromosome(population.get(chromosomeIndex).getId(), population.get(chromosomeIndex).getChromosomeData()));
		}

		this.population = newGeneration;
	}

	/**
	 * ensures: selects the population to delete based on rank
	 */
	public void rankSelect() {
		ArrayList<Chromosome> newGeneration = new ArrayList<>();
		Random r = new Random();
		int totalFit = (population.size() * (population.size() + 1)) / 2;

		for (int i = 0; i < population.size(); i++) {
			int partialSum = r.nextInt(totalFit);
			int chromosomeIndex = -1; // first while iteration at index 0 sets this to 0
			int rankFit = population.size();
			while (partialSum < totalFit) {
				partialSum += rankFit--;
				chromosomeIndex++;
			}
			newGeneration.add(new Chromosome(population.get(chromosomeIndex).getId(), population.get(chromosomeIndex).getChromosomeData()));
		}

		this.population = newGeneration;
	}

	/**
	 * ensures: bestFit is returned
	 * @return bestFit the fitness value of the best-fitting chromosome
	 */
	public double getBestFit() {
		return this.bestFit;
	}

	/**
	 * ensures: averageFit is returned
	 * @return averageFit the average of all of the fitness values of of the chromosomes in the population
	 */
	public double getAvgFit() {
		return this.averageFit;
	}

	/**
	 * ensures: worstFit is returned
	 * @return worstFit the fitness value of the worst-fitting chromosome
	 */
	public double getWorstFit() {
		return this.worstFit;
	}

	/**
	 * ensures: bestFit is returned
	 * @return bestFit the fitness value of the best-fitting chromosome
	 */
	public double getDiversity() {
		return this.diversity;
	}
	
	/**
	 * ensures: percentQs is returned
	 * @return percentQs the percentage of this population that is made up of '?' alleles
	 */
	public double getQPercent() {
		return this.percentQs;
	}
	
	/**
	 * ensures: percent1s is returned
	 * @return percent1s the percentage of this population that is made up of '1' alleles
	 */
	public double get1Percent() {
		return this.percent1s;
	}
	
	/**
	 * ensures: percent0s is returned
	 * @return percent0s the percentage of this population that is made up of '0' alleles
	 */
	public double get0Percent() {
		return this.percent0s;
	}
	
	/**
	 * ensures: population is returned
	 * @return population an ArrayList of Chromosomes that is the current population
	 */
	public ArrayList<Chromosome> getPopulation() {
		return population;
	}

	/**
	 * ensures: the average fitness of all of the chromosomes in the current population is computed and returned
	 * @return a double corresponding to the average fit of all of the chromosomes in the current population
	 */
	private double calculateAverageFit() {
		double sum = 0.0;
		for (Chromosome chromosome : this.population) {
			sum += chromosome.getFitness();
		}
		return (double) sum / this.population.size();
	}
	
	/**
	 * ensures: the percentages of '?' alleles, '1' alleles, and '0' alleles within the current population are computed and returned as an array
	 * @return an array of doubles corresponding to the percentages of '0' alleles, '1' alleles, and '?' alleles, respectively
	 */
	private double[] calculatePercent01Qs(){
		int num0s = 0;
		int num1s = 0;
		int numQs = 0;
		for (Chromosome chromosome : this.population) {
			int[] temp = chromosome.get01Q();
			num0s += temp[0];
			num1s += temp[1];
			numQs += temp[2];
		}
		int popSize = population.size();
		double[] toReturn = { (num0s / ((double) popSize * chromosomeLength)) * 100,
				(num1s / ((double) popSize * chromosomeLength)) * 100,
				(numQs / ((double) popSize * chromosomeLength)) * 100 };
		return toReturn;
	}

	/**
	 * ensures: diversityMode is returned
	 * @return diversityMode the currently selected mode by which diversity is to be calculated
	 */
	public String getDiversityMode() {
		return this.diversityMode;
	}

	/**
	 * ensures: diversityMode is set to the value of the given parameter
	 * @param diversityMode a String corresponding to the currently selected mode by which diversity is to be calculated
	 */
	public void setDiversityMode(String diversityMode) {
		this.diversityMode = diversityMode;
	}

	/**
	 * ensures: diversityMode is set to the value calculated by the selected diversity mode
	 * @param diversityMode a String corresponding to the currently selected mode by which diversity is to be calculated
	 */
	public void calculateDiversity(String diversityMode) {
		double diversity = 0;
		int popSize = this.population.size();
		if (diversityMode.equals("Hamming") || diversityMode.equals("Sorensen-Dice")) {
			// compute the Hamming distance as explained in the "Calculating Average Hamming
			// Distance" document in the project specifications
			// OR
			// compute a modified form of the Sørensen–Dice coefficient using a similar
			// manner to the simplified Hamming distance algorithm
			int totalMismatches = 0;
			int chromosomeLength = this.population.get(0).getLength();
			for (int i = 0; i < chromosomeLength; i++) {
				int numOnes = 0;
				int numZeros = 0;
				int numQuestions = 0;
				for (Chromosome chromosome : this.population) {
					char curChar = chromosome.getChromosomeData()[i];
					if (curChar == '1') {
						numOnes++;
					} else if (curChar == '0') {
						numZeros++;
					} else {
						numQuestions++;
					}
				}
				totalMismatches += ((numOnes * numZeros) + (numOnes * numQuestions) + (numZeros * numQuestions));
			}

			if (diversityMode.equals("Hamming")) {
				int totalPossiblePairs = (popSize * (popSize - 1)) / 2;
				diversity = ((totalMismatches / totalPossiblePairs) * 100) / chromosomeLength;
			} else {
				int totalElements = chromosomeLength * popSize;
				diversity = (((double) ((chromosomeLength * popSize * popSize) - totalMismatches) / totalElements)
						* 100) / popSize;
			}

		} else if(diversityMode.equals("Fitness Variance")) {
			// compute the variance of fitness values of the chromosomes in the population
			// OR 
			// compute the skewness of the fitness values of the chromosomes in the population
			
			Double fitSum = 0.0;
			for(Chromosome chromosome: this.population) {
				fitSum += chromosome.getFitness();
			}
			Double mean = (fitSum / popSize);
			
			Double scaledVariance = 0.0;
			for(Chromosome chromosome: this.population) {
				scaledVariance += (chromosome.getFitness() - mean) * (chromosome.getFitness() - mean);
			}
			
			diversity = ((scaledVariance / popSize) * 100) / popSize;
		}

		this.diversity = diversity;
	}

}
