package Milestone2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;

public class StatsComponent extends JComponent {
	// How wide to make side panel for reporting numbers
	public static final int SIDE_OFFSET = 100;
	public static final int SMALL_SIDE_OFFSET = 10;
	public static final int LAST_MARKER_OFFSET = 25;
	public static final int HEIGHT_OFFSET = 10;
	public static final int MARKER_HEIGHT_OFFSET = 15;

	public static final int MARKER_NUMBER = 11;

	private int iterations;

	public static final Color MARKER_COLOR = new Color(255, 255, 255);
	public static final Color BEST_COLOR = new Color(134, 184, 184);
	public static final Color AVG_COLOR = new Color(204, 102, 0);
	public static final Color WORST_COLOR = new Color(196, 116, 195);
	public static final Color DIVERSITY_COLOR = new Color(65, 150, 87);
	public static final Color QUESTION_ALLELE_COLOR = new Color(0, 0, 255);
	public static final Color CORRECT_ALLELE_COLOR = new Color(0, 255, 0);
	public static final Color INCORRECT_ALLELE_COLOR = new Color(255, 0, 0);

	// track the fits
	private HashMap<String, ArrayList<Double>> logs = new HashMap<String, ArrayList<Double>>();
	private HashMap<String, Integer> lastVals = new HashMap<String, Integer>();
	private HashMap<String, Color> logColors = new HashMap<String, Color>();
	private HashMap<String, String> logLabels = new HashMap<String, String>();
	private ArrayList<String> logOrder = new ArrayList<String>();

	public StatsComponent() {
		addNewLog("bestLog", BEST_COLOR, "Best fit: ");
		addNewLog("avgLog", AVG_COLOR, "Average fit: ");
		addNewLog("worstLog", WORST_COLOR, "Worst fit: ");
		addNewLog("divLog", DIVERSITY_COLOR, "Diversity: ");
		addNewLog("1Log", CORRECT_ALLELE_COLOR, "1 Alleles: ");
		addNewLog("0Log", INCORRECT_ALLELE_COLOR, "0 Alleles: ");
		addNewLog("?Log", QUESTION_ALLELE_COLOR, "? Alleles: ");
	}

	public void addNewLog(String name, Color lineColor, String label) {
		this.logs.put(name, new ArrayList<Double>());
		this.lastVals.put(name, 0);
		this.logColors.put(name, lineColor);
		this.logLabels.put(name, label);
		this.logOrder.add(name);
	}

	// add to the logs
	public void addEntry(String log, Double entry) {
		this.logs.get(log).add(entry);
	}

	// reset the logs
	public void reset() {
		for (String log : this.logs.keySet()) {
			this.logs.get(log).clear();
		}
	}

	public void setIteration(int iterations) {
		this.iterations = iterations;
	}

	public double getLineWidth() {
		return ((double) (this.getWidth() - SIDE_OFFSET)) / this.iterations;
	}

	public int getPercentFit(String log) {
		ArrayList<Double> curLog = this.logs.get(log);
		return curLog.get(curLog.size() - 1).intValue();
	}

	public int getPercentYPosition(String log, int index) {
		return (int) (this.getHeight() * (1 - (this.logs.get(log).get(index) / 100)));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));

		// Draw box for reporting numbers
		g2.drawRect(0, 0, SIDE_OFFSET - 1, this.getHeight());
		// Fill region for data to be shown
		g2.fillRect(SIDE_OFFSET, 0, this.getWidth() - SIDE_OFFSET, this.getHeight());

		// Don't try to draw unless there is data to be drawn
		if (this.logs.get("worstLog").size() > 0) {

			// draw the labels
			int numLogs = this.logs.size();
			int curLabelPos = 1;
			for (String log : this.logOrder) {
				g2.setColor(this.logColors.get(log));
				g2.drawString(this.logLabels.get(log) + getPercentFit(log) + "%", SMALL_SIDE_OFFSET,
						this.getHeight() * curLabelPos / (numLogs + 1));
				curLabelPos++;
			}

			// prepare for plotting
			for (String log : this.lastVals.keySet()) {
				this.lastVals.replace(log, getPercentYPosition(log, 0));
			}
			
			for (int i = 1; i < this.logs.get("bestLog").size(); i++) {
				for (String log : this.logs.keySet()) {
					int yVal = getPercentYPosition(log, i);
					g2.setColor(this.logColors.get(log));
					g2.draw(new Line2D.Double(SIDE_OFFSET + (i - 1) * getLineWidth(), this.lastVals.get(log),
							SIDE_OFFSET + i * getLineWidth(), yVal));
					this.lastVals.replace(log, yVal);
				}
			}
			
			// draw x-axis and markers
			double plotLength = iterations * getLineWidth();
			double markerGap = plotLength / (MARKER_NUMBER - 1);
			double markerNumberGap = (double) iterations / (MARKER_NUMBER - 1);

			g2.setColor(MARKER_COLOR);
			for (int i = 0; i < MARKER_NUMBER - 1; i++) {
				g2.draw(new Line2D.Double(SIDE_OFFSET + i * markerGap, this.getHeight() - HEIGHT_OFFSET, SIDE_OFFSET + i * markerGap,
						this.getHeight()));
				g2.drawString("" + (i * (int) markerNumberGap), SIDE_OFFSET + i * (int) markerGap, this.getHeight() - MARKER_HEIGHT_OFFSET);
			}
			g2.draw(new Line2D.Double(SIDE_OFFSET + (MARKER_NUMBER - 1) * markerGap, this.getHeight() - HEIGHT_OFFSET, SIDE_OFFSET + (MARKER_NUMBER - 1) * markerGap,
					this.getHeight()));
			
			g2.drawString("" + ((MARKER_NUMBER - 1) * (int) markerNumberGap), SIDE_OFFSET + (MARKER_NUMBER - 1) * (int) markerGap - LAST_MARKER_OFFSET, this.getHeight() - MARKER_HEIGHT_OFFSET);

		}

		
	}
}
