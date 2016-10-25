package id.web.ard.glcmfeatureextraction;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			GLCMFeatureExtraction glcmfe = new GLCMFeatureExtraction(new File("sample.png"), 15);
			glcmfe.extract();
			
			System.out.println("Contrast: "+glcmfe.getContrast());
			System.out.println("Homogenity: "+glcmfe.getHomogenity());
			System.out.println("Entropy: "+glcmfe.getEntropy());
			System.out.println("Energy: "+glcmfe.getEnergy());
			System.out.println("Dissimilarity: "+glcmfe.getDissimilarity());
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
