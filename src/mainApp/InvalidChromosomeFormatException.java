package mainApp;

public class InvalidChromosomeFormatException extends Exception {
	private int correctNum;
	private int actualNum;
	
	public InvalidChromosomeFormatException() {
		super();
		this.correctNum = 0;
		this.actualNum = 0;
	}
	
	public InvalidChromosomeFormatException(int correct, int actual) {
		super();
		this.correctNum = correct;
		this.actualNum = actual;
	}
	
	public InvalidChromosomeFormatException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		return "Expected length " + this.correctNum + " but got length " + this.actualNum;
	}
}
