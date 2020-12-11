/**
 * 
 */
package rhetoricDetection.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

import weka.core.ContingencyTables;

/**
 * Performs the statistical significance test (chi-square) 
 * between two distributions of rhetorical devices
 * 
 * Used to determine whether the rhetorical patterns detected
 * across one dimension(genre: Editorial) of articles differs from another(genre: Review)
 * 
 * @author viorel.morari
 *
 */
public class SignificanceTest {

	/**
	 * Performs the statistical significance test (chi-square) 
	 * between two distributions of rhetorical devices
	 * 
	 * @param observed1
	 * 					- first of the compared distributions of RD
	 * @param observed2
	 * 					- second of the compared distributions of RD
	 *
	 */
	public static void test(double[] observed1, double[] observed2)
    {
        double alpha = 0.1; // confidence level 99%
        //convert double to long
        List<Long> observed = new ArrayList<Long>(); 
        		
        for (double d : observed2) {
        	observed.add(Math.round(d)); 
		}
        //convert distribution values to long --> requirement of ChiSquareTest()
        long[] observedLong2 = observed.stream().mapToLong(l -> l).toArray();
        observed.clear();
        for (double d : observed1) {
        	observed.add(Math.round(d)); 
		}
        long[] observedLong1 = observed.stream().mapToLong(l -> l).toArray();
        System.out.println();
        //print the observed values
        System.out.printf("%15.15s: ", "Observed1");
        for (int i = 0; i < observedLong1.length; i++) {
            System.out.printf("%-6d ", observedLong1[i]);
        }
        
        System.out.println();
        
        System.out.printf("%15.15s: ", "Observed2");
        for (int i = 0; i < observedLong2.length; i++) {
            System.out.printf("%-6d ", observedLong2[i]);
        }
        long[][] observedMultiChi = new long[2][observedLong1.length];
        double[][] observedMultiCramer = new double[2][observed1.length];
        //copy long arrays to a two dimension array to fit the method
        System.arraycopy( observedLong1, 0, observedMultiChi[0], 0, observedLong1.length );
        System.arraycopy( observedLong2, 0, observedMultiChi[1], 0, observedLong2.length );
        
        System.arraycopy( observed1, 0, observedMultiCramer[0], 0, observed1.length );
        System.arraycopy( observed2, 0, observedMultiCramer[1], 0, observed2.length );
        System.out.println();
        System.out.println();
        
        ChiSquareTest t = new ChiSquareTest();
        
        double pval = t.chiSquareTest(observedMultiChi);
        System.out.printf("p-value: %.9f\n", pval);
        
        boolean rejected = t.chiSquareTest(observedMultiChi, alpha);
        System.out.println("Rejected value: "+rejected);
        System.out.println("X^2 Test: " + ((!rejected)? ("PASS") : ("FAIL")));
        
        System.out.println("Cramer's V: " +
        		ContingencyTables.CramersV(observedMultiCramer));
}
	
	
	/**
	 * Main method to run the test
	 * 
	 * Here we define the distributions of RD
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

        double[] observed_arts = new double[] 
		{   //author-hevesi
				56.44, 146.55, 34.39, 9.96, 58.30, 55.96, 69.94, 118.08, 6.27, 132.06, 3.09, 1.20, 3.23, 5.06, 2.98, 39.05, 41.48, 228.45, 143.02, 10.99, 0, 30.84, 17.10, 167.01, 1.00, 2.20
		};
        double[] observed_education = new double[] 
		{   //author-lewis
				59.64, 154.36, 56.96, 25.45, 74.20, 52.29, 59.83, 128.62, 5.22, 130.20, 2.06, 0.00, 1.08, 2.03, 0.99, 37.27, 32.53, 253.84, 169.27, 13.19, 1.00, 28.85, 13.15, 138.56, 3.00, 3.31
		};
        double[] observed_science = new double[] 
		{   //author-douglas
				52.18, 134.82, 52.66, 17.70, 56.18, 22.94, 54.77, 113.86, 1.04, 119.04, 4.12, 1.20, 1.08, 4.05, 0.99, 31.95, 27.65, 226.70, 135.78, 7.70, 0, 25.87, 17.10, 141.32, 1.00, 1.10
		};
        
        double[] observed_biography = new double[] 
		{   //author-hevesi
				132.05, 211.03, 103.17, 55.32, 82.67, 105.50, 128.08, 112.81, 9.40, 203.67, 16.50, 3.60, 3.23, 22.28, 6.96, 60.34, 71.56, 420.14, 226.30, 14.29, 1.00, 58.70, 30.25, 162.42, 0.00, 5.51
		};
        double[] observed_editorial = new double[] 
		{   //author-lewis
				51.12, 102.26, 102.09, 50.90, 37.10, 22.94, 80.05, 75.91, 4.18, 132.99, 18.56, 2.40, 7.54, 15.19, 6.96, 50.58, 26.84, 196.94, 122.20, 7.70, 4.00, 57.71, 19.73, 90.85, 3.00, 3.31
		};
        double[] observed_review = new double[] 
		{   //author-douglas
				86.26, 150.45, 93.50, 46.47, 59.36, 71.56, 92.69, 82.23, 8.35, 163.68, 11.34, 1.20, 5.38, 12.15, 5.97, 57.68, 42.29, 316.86, 181.04, 10.99, 0.00, 54.72, 34.19, 127.55, 3.00, 6.61
		};
        
        double[] observed_hevesi = new double[] 
		{   //author-hevesi
				100.10, 246.20, 36.54, 12.17, 67.84, 81.65, 117.97, 141.27, 4.18, 171.12, 1.03, 2.40, 1.08, 2.03, 4.97, 35.50, 67.50, 384.25, 256.17, 13.19,34.82, 19.73, 181.69, 0.0
		};
        double[] observed_lewis = new double[] 
		{   //author-lewis
				86.26, 169.34, 48.36, 17.70, 63.60, 63.30, 112.07, 124.41, 2.09, 109.74, 2.06, 0.00, 0.00, 0.00, 0.00, 33.72, 85.39, 323.86, 209.10, 20.89,37.81, 17.10, 159.67, 2.00
		};
        double[] observed_douglas = new double[] 
		{   //author-douglas
				97.97, 222.75, 63.41, 17.70, 68.90, 77.06, 76.68, 131.79, 11.49, 161.82, 1.03, 3.60, 2.15, 5.06, 4.97, 49.69, 48.79, 323.86, 271.56, 5.50, 26.86, 28.93, 189.95, 0.0
		};

        
		System.out.println("############################");
		System.out.println("TOPIC: SCIENCE vs. EDUCATION");
		System.out.println("############################");
		test(observed_science, observed_education);
		
		System.out.println("##########################");
		System.out.println("TOPIC: ARTS vs. EDUCATION");
		System.out.println("##########################");
		test(observed_education, observed_arts);
		
		System.out.println("########################");
		System.out.println("TOPIC: ARTS vs. SCIENCE");
		System.out.println("########################");
		test(observed_arts, observed_science);
        
		System.out.println("############################");
		System.out.println("GENRE: BIO vs. EDITORIAL");
		System.out.println("############################");
		test(observed_biography, observed_editorial);

		System.out.println("##########################");
		System.out.println("GENRE: EDITORIAL vs. REVIEW");
		System.out.println("##########################");
		test(observed_editorial, observed_review);

		System.out.println("########################");
		System.out.println("GENRE: REVIEW vs. BIO");
		System.out.println("########################");
		test(observed_review, observed_biography);

		System.out.println("############################");
		System.out.println("AUTHOR: HEVESI vs. LEWIS");
		System.out.println("############################");
		test(observed_hevesi, observed_lewis);

		System.out.println("##########################");
		System.out.println("AUTHOR: LEWIS vs. DOUGLAS");
		System.out.println("##########################");
		test(observed_lewis, observed_douglas);

		System.out.println("########################");
		System.out.println("AUTHOR: DOUGLAS vs. HEVESI");
		System.out.println("########################");
		test(observed_douglas, observed_hevesi);
        
/*        System.out.println("############################");
        System.out.println("DEBATE: CLINTON --> REST vs. CLINTON --> TRUMP");
        System.out.println("############################");
        test(clinton_rest, clinton_trump);
        
        System.out.println("##########################");
        System.out.println("DEBATE: CLINTON --> TRUMP vs. TRUMP --> CLINTON");
        System.out.println("##########################");
        test(clinton_trump, trump_clinton);
        
        System.out.println("########################");
        System.out.println("DEBATE: TRUMP --> CLINTON vs. TRUMP --> REST");
        System.out.println("########################");
        test(trump_clinton, trump_rest);
        
        System.out.println("########################");
        System.out.println("DEBATE: CLINTON --> REST vs. TRUMP --> REST");
        System.out.println("########################");
        test(clinton_rest, trump_rest);
        
        System.out.println("########################");
        System.out.println("DEBATE: CLINTON --> REST vs. TRUMP --> CLINTON");
        System.out.println("########################");
        test(clinton_rest, trump_clinton);
        
        System.out.println("########################");
        System.out.println("DEBATE: CLINTON --> TRUMP vs. TRUMP --> REST");
        System.out.println("########################");
        test(clinton_trump, trump_rest);*/

        
	}

}
