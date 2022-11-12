package Milestone2;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import soundLib.SinSynth;

/**
 * @author schrocjq
 * This class runs the EvolutionSim in a separate thread
 */
public class EvolutionThread extends SwingWorker<Boolean, Object[]> {
	
	private static final String INIT_TEXT = "Start";
	private static final String RUNNING_TEXT = "Pause";
	private static final String PAUSED_TEXT = "Run";
	private static final int DELAY = 50;
	
	private boolean paused;
	private int iteration = 0;
	private StatsComponent statsComponent;
	private EvolutionSim evolutionSim;
	private int numIterations;
	private double termination;
	private boolean terminationOn;
	double questionRate;
	private BestFitViewer bestFitViewer;
	private PopulationViewer populationViewer;
	private JButton ppButton;

	public EvolutionThread(JButton ppButton, StatsComponent statsComponent, JButton bestViewerButton, JButton popViewerButton, 
			int numIterations, double termination, boolean terminationOn, EvolutionSim evolutionSim) {
		super();
		this.paused = false;
		this.numIterations = numIterations;
		this.ppButton = ppButton;
		this.termination = termination;
		this.terminationOn = terminationOn;
		this.statsComponent = statsComponent;
		this.evolutionSim = evolutionSim;
	}
	
	
	/**
	 * ensures: this method pauses the evolution simulation
	 */
	public void pause() {
		this.paused = true;
		ppButton.setText(PAUSED_TEXT);
	}
	
	/**
	 * ensures: this method resumes the evolution simulation
	 */
	public void resume() {
		this.paused = false;
		ppButton.setText(RUNNING_TEXT);
	}

	/**
	 * ensures: this method contains the process that the thread runs in the background, which consists of running iterations of the 
	 * evolution simulation and adding items to the publisher to be sent back to the GUI.
	 * @return returns true when the thread is done
	 */
	@Override
	protected Boolean doInBackground() throws Exception {
		// TODO Auto-generated method stub
		ppButton.setText(RUNNING_TEXT);
		this.iteration = 0;
		while (!(iteration >= numIterations
				|| ((evolutionSim.getBestFit() >= termination) && terminationOn))) {
			if (!this.paused) {
				evolutionSim.evolutionLoop();
				double[] list = {evolutionSim.getBestFit(), evolutionSim.getAvgFit(), evolutionSim.getWorstFit(), evolutionSim.getDiversity(), evolutionSim.get0Percent(), evolutionSim.get1Percent(), evolutionSim.getQPercent()};
				ArrayList<char[]> list2 = new ArrayList<char[]>();
				ArrayList<Integer> list3 = new ArrayList<Integer>();
				for (Chromosome chromosome : evolutionSim.getPopulation()) {
					list2.add(chromosome.getChromosomeData().clone());
					list3.add(chromosome.getId());
				}
				Object[] returnList = {list, list2, list3};
				publish(returnList);
				iteration++;
			}
			Thread.sleep(DELAY);
		}
		return true;
	}
	
	/**
	 * ensures: this method sets the button text to start once the thread is done. This method is called automatically
	 */
	@Override
	protected void done() {
		ppButton.setText(INIT_TEXT);
		super.done();
	}
	
	/**
	 * ensures: this method handles updating the GUI when the thread outputs information (updates the graph data, best chromosome viewer, and populaiton).
	 * @param chunks are a list of lists that contain the information to update the GUI
	 */
	@Override
	protected void process(List<Object[]> chunks) {
		for (Object[] chunk : chunks) {
			//statsComponent.addEntry(((double[]) chunk[0])[0], ((double[]) chunk[0])[1], ((double[]) chunk[0])[2], ((double[]) chunk[0])[3]);
			statsComponent.addEntry("bestLog", ((double[]) chunk[0])[0]);
			statsComponent.addEntry("avgLog", ((double[]) chunk[0])[1]);
			statsComponent.addEntry("worstLog", ((double[]) chunk[0])[2]);
			statsComponent.addEntry("divLog", ((double[]) chunk[0])[3]);
			statsComponent.addEntry("0Log", ((double[]) chunk[0])[4]);
			statsComponent.addEntry("1Log", ((double[]) chunk[0])[5]);
			statsComponent.addEntry("?Log", ((double[]) chunk[0])[6]);
		}
		statsComponent.revalidate();
		statsComponent.repaint();
		if (bestFitViewer != null) {
			bestFitViewer.setBestChromosome(((ArrayList<Integer>) chunks.get(chunks.size()-1)[2]).get(0), ((ArrayList<char[]>) chunks.get(chunks.size()-1)[1]).get(0));
		}
		if (populationViewer != null) {
			populationViewer.setPopulation(((ArrayList<Integer>) chunks.get(chunks.size()-1)[2]), ((ArrayList<char[]>) chunks.get(chunks.size()-1)[1]));
		}
//		For audio
//		new SinSynth(Integer.parseInt(evolutionSim.getPopulation().get(0).toString(), 2), DELAY);
//		new SinSynth(Integer.parseInt(evolutionSim.getPopulation().get(evolutionSim.getPopulation().size()/2).toString(), 2), DELAY);
//		new SinSynth(Integer.parseInt(evolutionSim.getPopulation().get(evolutionSim.getPopulation().size()-1).toString(), 2), DELAY);
	}
	/**
	 * ensures: this method sets the BestFitViewer
	 * @param the instance of BestFitViewer
	 */
	public void setBestFitViewer(BestFitViewer viewer) {
		this.bestFitViewer = viewer;
	}
	
	/**
	 * ensures: this method sets the PopulaitonViewer
	 * @param the instance of BestFitViewer
	 */
	public void setPopulationViewer(PopulationViewer viewer) {
		this.populationViewer = viewer;
	}

}
