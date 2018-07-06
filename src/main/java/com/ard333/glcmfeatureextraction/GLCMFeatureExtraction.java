/**
 * Ardiansyah | http://ard.web.id
 *
 */

package com.ard333.glcmfeatureextraction;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Ardiansyah <ard333.ardiansyah@gmail.com>
 */
public class GLCMFeatureExtraction {
	
	private BufferedImage image;
	private int[][] grayLeveledMatrix;
	private int grayLevel;
	private double contrast;
	private double homogenity;
	private double entropy;
	private double energy;
	private double dissimilarity;

	public GLCMFeatureExtraction(File image, int grayLevel) throws IOException {
		this.image = ImageIO.read(image);
		this.grayLevel = grayLevel;
		grayLeveledMatrix = new int[this.image.getWidth()][this.image.getHeight()];
	}
	
	public void extract() {
		this.createGrayLeveledMatrix();
		
		//0°
		int[][] cm0 = createCoOccuranceMatrix(0);
		double[][] cm0SN = normalizeMatrix(add(cm0, transposeMatrix(cm0)));
		
		//45°
		int[][] cm45 = createCoOccuranceMatrix(45);
		double[][] cm45SN = normalizeMatrix(add(cm45, transposeMatrix(cm45)));
		
		//90°
		int[][] cm90 = createCoOccuranceMatrix(90);
		double[][] cm90SN = normalizeMatrix(add(cm90, transposeMatrix(cm90)));
		
		//135°
		int[][] cm135 = createCoOccuranceMatrix(135);
		double[][] cm135SN = normalizeMatrix(add(cm135, transposeMatrix(cm135)));
		
		this.contrast = (double) (calcContrast(cm0SN) + calcContrast(cm45SN) + calcContrast(cm90SN) + calcContrast(cm135SN)) / 4;
		this.homogenity = (double) (calcHomogenity(cm0SN) + calcHomogenity(cm45SN) + calcHomogenity(cm90SN) + calcHomogenity(cm135SN)) / 4;
		this.entropy = (double) (calcEntropy(cm0SN) + calcEntropy(cm45SN) + calcEntropy(cm90SN) + calcEntropy(cm135SN)) / 4;
		this.energy = (double) (calcEnergy(cm0SN) + calcEnergy(cm45SN) + calcEnergy(cm90SN) + calcEnergy(cm135SN)) / 4;
		this.dissimilarity = (double) (calcDissimilarity(cm0SN) + calcDissimilarity(cm45SN) + calcDissimilarity(cm90SN) + calcDissimilarity(cm135SN)) / 4;
		
	}
	
	private void createGrayLeveledMatrix() {
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				Color rgb = new Color(image.getRGB(i, j));
				int newR = rgb.getRed();
				int newG = rgb.getGreen();
				int newB = rgb.getBlue();
				int newA = rgb.getAlpha();
				int gr = (newR + newG + newB) / 3;
				
				if (grayLevel > 0 && grayLevel < 255) {
					grayLeveledMatrix[i][j] = gr * grayLevel / 255;
				} else {
					grayLeveledMatrix[i][j] = gr;
				}
			}
		}
	}
	
	private int[][] createCoOccuranceMatrix(int angle) { //distance = 1
		int[][] temp = new int[grayLevel+1][grayLevel+1];
		int startRow = 0;
		int startColumn = 0;
		int endColumn = 0;
		
		boolean validAngle = true;
		switch (angle) {
			case 0:
				startRow = 0;
				startColumn = 0;
				endColumn = grayLeveledMatrix[0].length-2;
				break;
			case 45:
				startRow = 1;
				startColumn = 0;
				endColumn = grayLeveledMatrix[0].length-2;
				break;
			case 90:
				startRow = 1;
				startColumn = 0;
				endColumn = grayLeveledMatrix[0].length-1;
				break;
			case 135:
				startRow = 1;
				startColumn = 1;
				endColumn = grayLeveledMatrix[0].length-1;
				break;
			default:
				validAngle = false;
				break;
		}
		
		if (validAngle) {
			for (int i = startRow; i < grayLeveledMatrix.length; i++) {
				for (int j = startColumn; j <= endColumn; j++) {
					switch (angle) {
						case 0:
							temp[grayLeveledMatrix[i][j]][grayLeveledMatrix[i][j+1]]++;
							break;
						case 45:
							temp[grayLeveledMatrix[i][j]][grayLeveledMatrix[i-1][j+1]]++;
							break;
						case 90:
							temp[grayLeveledMatrix[i][j]][grayLeveledMatrix[i-1][j]]++;
							break;
						case 135:
							temp[grayLeveledMatrix[i][j]][grayLeveledMatrix[i-1][j-1]]++;
							break;
					}
				}
			}
		}
		return temp;
	}
	
	private int[][] transposeMatrix(int [][] m){
		int[][] temp = new int[m[0].length][m.length];
		for (int i = 0; i < m.length; i++){
			for (int j = 0; j < m[0].length; j++){
				temp[j][i] = m[i][j];
			}
		}
		return temp;
	}
	
	private int[][] add(int [][] m2, int [][] m1){
		int[][] temp = new int[m1[0].length][m1.length];
		for (int i = 0; i < m1.length; i++){
			for (int j = 0; j < m1[0].length; j++){
				temp[j][i] = m1[i][j] + m2[i][j];
			}
		}
		return temp;
	}
	
	private int getTotal(int [][] m){
		int temp = 0;
		for (int i = 0; i < m.length; i++){
			for (int j = 0; j < m[0].length; j++){
				temp += m[i][j];
			}
		}
		return temp;
	}
	
	private double[][] normalizeMatrix(int [][] m){
		double[][] temp = new double[m[0].length][m.length];
		int total = getTotal(m);
		for (int i = 0; i < m.length; i++){
			for (int j = 0; j < m[0].length; j++){
				temp[j][i] = (double) m[i][j] / total;
			}
		}
		return temp;
	}
	
	private double calcContrast(double[][] matrix) {
		double temp = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				temp += matrix[i][j] * Math.pow(i-j, 2);
			}
		}
		return temp;
	}
	
	private double calcHomogenity(double[][] matrix) {
		double temp = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				temp += matrix[i][j] / (1+Math.pow(i-j, 2));
			}
		}
		return temp;
	}
	
	private double calcEntropy(double[][] matrix) {
		double temp = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (matrix[i][j] != 0) {
					temp += (matrix[i][j] * Math.log10(matrix[i][j])) * -1;
				}
			}
		}
		return temp;
	}
	
	private double calcEnergy(double[][] matrix) {
		double temp = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				temp += Math.pow(matrix[i][j], 2);
			}
		}
		return temp;
	}
	
	private double calcDissimilarity(double[][] matrix) {
		double temp = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				temp += matrix[i][j] * Math.abs(i-j);
			}
		}
		return temp;
	}

	public double getContrast() {
		return contrast;
	}

	public double getHomogenity() {
		return homogenity;
	}

	public double getEntropy() {
		return entropy;
	}

	public double getEnergy() {
		return energy;
	}

	public double getDissimilarity() {
		return dissimilarity;
	}
	
}
