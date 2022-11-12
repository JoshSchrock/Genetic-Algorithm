package Milestone2;

import java.util.Random;

/**
 * 
 * Class: Chromosome
 * @author F23-R-401
 * <br>Purpose: Used to hold data and perform methods associated with a member of a population undergoing evolution
 * <br>For example: 
 * <pre>
 *    Chromosome chromosome = new Chromosome(1, 100, 0.0);
 * </pre>
 */

public class Chromosome implements Comparable<Chromosome>{

	private char[] chromosomeData;
	private char[] targetData;
	private Random r;
	private int length;
	private Double fitness;
	private int id;

	
	/**
	 * ensures: creates an instance of Chromosome and initializes its fields
	 * @param id the identification number of this chromosome
	 * @param length the number of alleles/genes in this chromosome
	 * @param qRate the rate of '?' alleles to be generated initially in this chromosome's list of alleles
	 */
	public Chromosome(int id, int length, double qRate) {
		this.id = id;
		this.length = length;
		this.r = new Random();
		this.r.setSeed(r.nextLong());

		generateChromosomeData(qRate);
	}

	/**
	 * ensures: creates an instance of Chromosome and initializes its fields
	 * @param id the identification number of this chromosome
	 * @param length the number of alleles/genes in this chromosome
	 * @param seed the seed with which this chromosome's alleles will be randomly generated
	 * @param qRate the rate of '?' alleles to be generated initially in this chromosome's list of alleles
	 */
	public Chromosome(int id, int length, long seed, double qRate) {
		this.id = id;
		this.length = length;
		this.r = new Random();
		this.r.setSeed(seed);

		generateChromosomeData(qRate);
	}

	/**
	 * ensures: creates an instance of Chromosome and initializes its fields
	 * @param id the identification number of this chromosome
	 * @param chromosomeData the list of alleles to be held by this chromosome
	 */
	public Chromosome(int id, char[] chromosomeData) {
		this.id = id;
		this.length = chromosomeData.length;
		this.r = new Random();
		this.chromosomeData = chromosomeData.clone();
	}
	
	/**
	 * ensures: calculates this chromosome's fitness according to the specified fitness mode
	 * @param fitnessMode a String indicating what fitness mode to use when calculating fitness
	 */
	public void calculateFit(String fitnessMode) {
		if (fitnessMode.equals("Simple")) {
			this.fitness = this.simpleFit();
		} else if (fitnessMode.equals("Target")) {
			this.fitness = this.targetFit(this.targetData);
		} else if (fitnessMode.equals("Learning")) {
			this.fitness = this.learningFit();
		} else if (fitnessMode.equals("Consecutive")) {
			this.fitness = this.consecutiveFit('1');
		} else {
			this.fitness = this.phenotypeFit(this.targetData);
		}
	}


	/**
	 * ensures: evaluates the given chromosome's fitness by counting the number of
	 * "true" values it contains
	 * 
	 * @return the count of the number of "true" genes in this chromosome relative
	 *         to the total length of the chromosome
	 */
	public Double simpleFit() {
		double count = 0.0;
		for (int i = 0; i < this.length; i++) {
			if (this.chromosomeData[i] == '1') {
				count += 1.0;
			}
		}
		return count * 100 / this.chromosomeData.length;
	}

	/**
	 * ensures: evaluates this chromosome's fitness by counting the number of gene
	 * values which are the same as the given target chromosome's gene values at the
	 * same index
	 * 
	 * @param target the target chromosome to be compared to
	 * @return the count of the number of genes which have the same value in both
	 *         this chromosome and the target relative to the total length of this
	 *         chromosome
	 */
	public Double targetFit(char[] targetData) {
		double count = 0.0;
		for (int i = 0; i < this.length; i++) {
			if (this.chromosomeData[i] == targetData[i]) {
				count += 1.0;
			}
		}
		return count * 100 / this.chromosomeData.length;
	}

	/**
	 * ensures: evaluates the given chromosome's fitness by counting the largest
	 * number of consecutive values that are the same as fitVal relative to its
	 * total size
	 * 
	 * @return the largest number of consecutive genes whose values are the same as
	 *         fitVal in this chromosome relative to the total length of the
	 *         chromosome
	 * @param fitVal the boolean value corresponding to the consecutive values to be
	 *               counted in this chromosome
	 */
	public Double consecutiveFit(char fitVal) {
		Double maxFit = 0.0;
		Double curFit = 0.0;
		for (int i = 0; i < this.chromosomeData.length - 1; i++) {
			if (this.chromosomeData[i] == fitVal) {
				curFit++;
				if (!(this.chromosomeData[i + 1] == fitVal)) {
					if (curFit > maxFit) {
						maxFit = curFit;
					}
					curFit = 0.0;
				}
			} else {
				curFit = 0.0;
			}
		}
		return maxFit * 100 / this.chromosomeData.length;
	}

	/**
	 * ensures: evaluates the given chromosome's fitness by comparing its alleles' weights against its target's
	 * @param targetData the data of the target chromosome being compared against
	 * @return the resulting fitness value of the phenotype fit
	 */
	public Double phenotypeFit(char[] targetData) {
		double fit = 0.0;
		double weight = Math.pow(2, targetData.length - 1);
		for (int i = 0; i < this.length; i++) {
			if (this.chromosomeData[i] == targetData[i]) {
				fit += weight;
			}
			weight /= 2;
			System.out.println(weight);
		}
		return fit / (Math.pow(2, targetData.length) - 1) * 100;
	}

	/**
	 * ensures: chromosomeData is returned
	 * @return chromosomeData the list of alleles associated with this chromosome
	 */
	public char[] getChromosomeData() {
		return this.chromosomeData;
	}

	/**
	 * ensures: the given array of characters is stored in chromosomeData
	 * @param chromosomeData the list of alleles to be stored by this chromosome
	 */
	public void setChromosomeData(char[] newData) {
		this.chromosomeData = newData;
	}

	/**
	 * ensures: the given array of characters is stored in chromosomeData
	 * @param chromosomeData the list of alleles to be stored by this chromosome
	 */
	public void setSeed(long newSeed) {
		this.r.setSeed(newSeed);
	}

	/**
	 * ensures: this chromosome's chromosomeData is converted to a string representation and printed
	 */
	public void printChromosome() {
		System.out.println(this.toString());
	}

	/**
	 * ensures: mutates the entire chromosome, will get moved to EvolutionSim if
	 * needed
	 * 
	 * @param mutationRate
	 */
	public void mutate(double mutationRate) {
		for (int i = 0; i < this.chromosomeData.length; i++) {
			if (Math.random() <= mutationRate) {
				if (this.chromosomeData[i] == '1') {
					this.chromosomeData[i] = '0';
				} else {
					this.chromosomeData[i] = '1';
				}
			}
		}

	}

	/**
	 * ensures: this chromosome's chromosomeData is converted to a string representation and returned
	 * @return string representation of this chromosome's chromosomeData
	 */
	public String toString() {
		String chromosomeAsString = "";
		for (char gene : this.chromosomeData) {
			chromosomeAsString += gene;
		}
		return chromosomeAsString;
	}

	/**
	 * ensures: this chromosome's chromosomeData is stored with random '?' alleles randomly inserted according to the given qRate
	 * @param qRate the rate at which '?' alleles will be randomly inserted
	 */
	private void generateChromosomeData(double qRate) {
		this.chromosomeData = new char[this.length];
		for (int i = 0; i < length; i++) {
			if (this.r.nextBoolean()) {
				this.chromosomeData[i] = '1';
			} else {
				this.chromosomeData[i] = '0';
			}

			if (Math.random() <= qRate) {
				this.chromosomeData[i] = '?';
			}
		}
	}

	/**
	 * ensures: returns length
	 * @return the length of this chromosome
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * ensures: computes this chromosome's fitness according to the requirements of the learning fitness mode
	 * @return this chromosome's learning fitness
	 */
	public Double learningFit() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 1000; i++) {
			boolean flag = true;
			char[] tempData = chromosomeData.clone();
			for (int j = 0; j < tempData.length; j++) {
				if (tempData[j] == '?') {
					if (this.r.nextBoolean()) {
						tempData[j] = '1';
					} else {
						tempData[j] = '0';
					}
				}
			}
			for (char gene : tempData) {
				if (gene == '0') {
					flag = false;
				}
			}
			if (flag) {
				return ((1.0 + ((19.0 * (1000.0 - (i + 1.0))) / 1000.0)) / 20.0) * 100.0;
			}
		}
		return (1.0 / 20.0) * 100.0;
	}

	/**
	 * ensures: counts the number of times the parameter lookFor occurs in the given list
	 * @param list the list to be searched
	 * @param lookFor the item to be searched for in the given list
	 * @return the number of times lookFor appears in list
	 */
	private int count(char[] list, char lookFor) {
		int count = 0;
		for (char character : list) {
			if (character == lookFor) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * ensures: the number of '0' alleles, '1' alleles, and '?' alleles in chromosomeData are counted and returned as a 3-item list
	 * @return 3-item list containing the number of '0' alleles, '1' alleles, and '?' alleles in chromosomeData
	 */
	public int[] get01Q() {
		int[] list = {count(chromosomeData, '0'), count(chromosomeData, '1'), count(chromosomeData, '?')};
		return list;
	}
	
	/**
	 * ensures: returns this chromosome's fitness
	 * @return this chromosome's fitness value
	 */
	public Double getFitness() {
		return this.fitness;
	}
	
	/**
	 * ensures: sets this chromosome's target data to the array of chars in targetData
	 * @param targetData the data to which this chromosome's target data is to be set
	 */
	public void setTargetData(char[] targetData) {
		this.targetData = targetData;
	}

	/**
	 * ensures: returns this chromosome's relative comparison to the given chromosome
	 * @param chromosome the chromosome ot be compared to
	 * @return this chromosome's comparison relative to the given chromosome
	 */
	@Override
	public int compareTo(Chromosome chromosome) {
		return chromosome.getFitness().compareTo(this.fitness);
	}
	
	/**
	 * ensures: returns this chromosome's ID
	 * @return this chromosome's ID
	 */
	public int getId() {
		return this.id;
	}

}
