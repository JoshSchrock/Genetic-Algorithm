package soundLib;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Class: SinSynth
 * @author F23-R-401 &
 *         Thumbz @ https://stackoverflow.com/questions/8632104/sine-wave-sound-generator-in-java
 * 
 * Purpose: Plays sine wave of a specific frequency for a certain number of milliseconds
 */
public class SinSynth {

	private static final int SAMPLE_RATE = 16 * 1024;

	/**
	 * Makes the byte array representing sampled sine wave
	 * @param freq
	 * @param ms
	 * @return
	 */
	private byte[] createSinWaveBuffer(double freq, int ms) {
		int samples = (int) ((ms * SAMPLE_RATE) / 1000);
		byte[] output = new byte[samples];

		double period = (double) SAMPLE_RATE / freq;
		for (int i = 0; i < output.length; i++) {
			double angle = 2.0 * Math.PI * i / period;
			output[i] = (byte) (Math.sin(angle) * 127f); //127f is byte cast
		}

		return output;
	}

	/**
	 * ensures: Constructor that plays the sound
	 * @param freq
	 * @param length
	 */
	public SinSynth(int freq, int length) {
		final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
		SourceDataLine line;
		try {
			line = AudioSystem.getSourceDataLine(af);
			line.open(af, SAMPLE_RATE);
			line.start();

			byte[] toneBuffer = createSinWaveBuffer(freq, length);
			line.write(toneBuffer, 0, toneBuffer.length);

			line.drain();
			line.close();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}