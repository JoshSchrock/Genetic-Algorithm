package mainApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * Class: ChromosomeIO
 * @author F23-R-401
 * <br>Purpose: Used to save and load chromosome data to and from .txt files
 * <br>Restrictions: Cannot be used for filetypes other than .txt
 * <br>For example: 
 * <pre>
 *    ChromosomeIO fileIO = new ChromosomeIO(frame);
 * </pre>
 */

public class ChromosomeIO {

	private JFrame parent;
	private JFileChooser fileChooser;
	private FileNameExtensionFilter filter;
	private String curFileName;

	/**
	 * ensures: initializes parent to the selected parent and initializes new fileChooser and filter
	 * @param parent JFrame which has this instance of ChromosomeIO
	 */
	public ChromosomeIO(JFrame parent) {
		this.fileChooser = new JFileChooser();
		this.filter = new FileNameExtensionFilter("Text Files", "txt");
		this.parent = parent;
		fileChooser.setFileFilter(filter);
		
	} // ChromosomeIO


	/**
	 * ensures: inputFilename is opened, read, and returned to the caller
	 * @return array of type boolean corresponding to the data of the file selected by the user in the showOpenDialog GUI
	 * @throws FileNotFoundException
	 * @throws DataFormatException
	 */
	public char[] loadChromosome() throws FileNotFoundException, InvalidChromosomeFormatException {
		fileChooser.setDialogTitle("Please Select a Chromosome:");
		int returnVal = fileChooser.showOpenDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " + fileChooser.getSelectedFile().getName());
			this.curFileName = fileChooser.getSelectedFile().getName();
		} else {
			throw new FileNotFoundException();
		}

		char[] geneList = loadChromosomeToList(fileChooser.getSelectedFile());
		 
		return geneList;
		
	} // loadChromosome

	
	/**
	 * ensures: chromosomeFile is opened, read, converted from a String to an array of type boolean, and returned to the caller
	 * @param chromosomeFile .txt file selected by the user in the showOpenDialog GUI
	 * @return array of type boolean corresponding to the data of chromosomeFile
	 * @throws InvalidChromosomeFormatException
	 */
	public char[] loadChromosomeToList(File chromosomeFile) throws InvalidChromosomeFormatException {
		String chromosomeString = readChromosomeToString(chromosomeFile);
		char[] geneList = new char[chromosomeString.length()];
		
		// Allowed Characters in .txt File:
		// "1" - gene with value of binary 1
		// "0" - gene with value of binary 0
		for (int i = 0; i < chromosomeString.length(); i++) {
			try {
			geneList[i] = chromosomeString.charAt(i);
			} catch(Exception e) {
				System.err.println("Chromosome file contains incorrect character at index: " + i);
				throw new InvalidChromosomeFormatException();
			}
		}
		return geneList;
		
	} // loadChromosomeToList

	
	/**
	 * ensures: chromosomeFile is opened, read, and returned to the caller as a String
	 * @param chromosomeFile .txt file selected by the user in the showOpenDialog GUI
	 * @return String corresponding to the values of the genes in the chromosome to be saved
	 */
	public String readChromosomeToString(File chromosomeFile) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(chromosomeFile);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			File parentFolder = chromosomeFile.getParentFile();
			System.err.println("Folder searched for the file not found: " + parentFolder.getAbsolutePath());
		} // end try-catch

		String chromosomeString = "";
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			chromosomeString += line;
			System.out.println(line);
		} // end while
		scanner.close();

		return chromosomeString;
		
	} // readChromosomeToString


	/**
	 * ensures: geneList is saved to the .txt file selected by the user in the showSaveDialog GUI as a string
	 * @param geneList array of type boolean corresponding to the values of the genes in the chromosome to be saved
	 */
	public void saveChromosome(char[] geneList) {
		fileChooser.setDialogTitle("Where would you like to save your chromosome?");
		int returnVal = fileChooser.showSaveDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File chromosomeFile = fileChooser.getSelectedFile();
			saveChromosomeFromList(chromosomeFile, geneList);
			System.out.println("You chose to open this file: " + fileChooser.getSelectedFile().getName());
		} else {
			return;
		}
		
	} // saveChromosome
	
	
	/**
	 * ensures: geneList is converted to a String and saved to the .txt file outputFile
	 * @param outputFile Uses FileWriter class from java.io which writes formatted representations of objects to a file
	 * @param geneList array of type boolean corresponding to the chromosome data to be saved
	 */
	public void saveChromosomeFromList(File outputFile, char[] geneList) {
		String chromosomeString = "";
		for (int i = 0; i < geneList.length; i++) {
			chromosomeString += geneList[i];
		}
		writeChromosomeFromString(outputFile, chromosomeString);
		
	} // saveChromosomeFromList
	
	
	/**
	 * ensures: chromosomeString is saved by being written to the .txt file outputFile
	 * @param outputFile Uses FileWriter class from java.io which writes formatted representations of objects to a file
	 * @param chromosomeString String corresponding to the values of the genes in the chromosome to be written to outputFile
	 */
	public void writeChromosomeFromString(File outputFile, String chromosomeString) {
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(outputFile);
			fw.write(chromosomeString);
			fw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} // end try-catch

	} // WriteChromosomeFromString
	
	
	/**
	 * ensures: curFileName is returned
	 * @return curFileName the name of the currently loaded file
	 */
	public String getCurFileName() {
		return this.curFileName;
	} // getCurFileName
	
	
	public JFrame getParent() {
		return this.parent;
	}

}
