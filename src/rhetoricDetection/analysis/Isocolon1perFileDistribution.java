/**
 * 
 */
package rhetoricDetection.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rhetoricDetection.SyntacticRhetoricSimpleAnnotator;

/**
 * This class reads the input file containing bicolon/tricolon/tetracolon samples
 * and distributes each instance of this RD to a separate file for better evaluation
 * 
 * @author viorel.morari
 *
 * 
 */
public class Isocolon1perFileDistribution {

	// -------------------------------------------------------------------------
	// PARAMETERS
	// -------------------------------------------------------------------------
	/**
	 * The path to the text file with isocolon samples
	 */
	private static final String INPUT_FILE_ISOCOLON = 
			"data/rhetorical-devices-recall/all-isocolon.txt";
	
	/**
	 * The path to the output folder to store the isocolon samples in separate files
	 */
	private static final String OUTPUT_FOLDER_ISOCOLON = 
			"data/isocolon-evaluation/all-but-isocolon/";
	
	// -------------------------------------------------------------------------
	// PROCESSING
	// -------------------------------------------------------------------------
	/**
	 * Distributes each sample in the input text file to separate files
	 * 
	 * @param inputFile
	 *              - file with isocolon samples
	 * @param outputFolder
	 * 				- folder to store samples in separate files
	 */

	private void linesInFilesDistribution(String inputFile, String outputFolder) throws IOException {
		BufferedReader br;
		FileWriter fw;
		String str;
		int lineCounter = 0;
		try {
			//read input
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
			//write each isocolon sample to separate file
			while ((str = br.readLine()) != null) {
				fw = new FileWriter(outputFolder+lineCounter+".txt");
				fw.write(System.lineSeparator());
				fw.write(str);
				lineCounter++;
				fw.flush();
			}
			br.close();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	/**Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Isocolon1perFileDistribution dist = new Isocolon1perFileDistribution();
		try {
			dist.linesInFilesDistribution(INPUT_FILE_ISOCOLON, OUTPUT_FOLDER_ISOCOLON);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
