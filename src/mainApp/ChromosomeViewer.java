package mainApp;

import javax.swing.JFrame;
import javax.swing.UIManager;
/**
 * Class: ChromosomeViewer
 * 
 * @author F23-R-401
 *<br>Purpose: A simple viewer for the main editor. Might be expanded to include more JFrames in the future
 */
public class ChromosomeViewer {

	public static final int WINDOW_WIDTH = 600;
	public static final int WINDOW_HEIGHT = 600;

	public static void main(String[] args) {
//		Only needed for viewing on macOS:
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		end mac specific code
		
		ChromosomeEditor chromosomeEditor = new ChromosomeEditor();
		chromosomeEditor.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		chromosomeEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chromosomeEditor.setVisible(true);

	}

}
