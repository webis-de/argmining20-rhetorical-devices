package de.aitools.ie.uima.io;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class for writing UIMA XMI files.
 */
public class UIMAAnnotationFileWriter {

	/**
	 * Writes the content of the given CAS object to a file with a name derived
	 * from the given name to the given output directory.
	 * 
	 * @param cas The CAS object
	 * @param outputDirectory The output directory
	 * @param inputFileName The file name
	 */
	public void write(CAS cas, String outputDirectory, String inputFileName){
		try {
			if (cas == null) {
				return;
			}
			// Create directory if not yet existing
			if (!new File(outputDirectory).exists()){
				new File(outputDirectory).mkdirs();
			}
			
			// Create output file with derived name
			String outputFileName = inputFileName.substring(0, 
					inputFileName.lastIndexOf(".")) + ".xmi";
			File file = new File(outputDirectory + outputFileName);

			UIMAAnnotationFileWriter.write(cas, file);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	 /**
   * Writes the content of the given CAS object to a file.
   * 
   * @param cas The CAS object
   * @param file The output file
   */
	public static void write(final CAS cas, File file)
	throws IOException {
    if (cas == null) { throw new NullPointerException(); }

    // Write to file
    try (OutputStream outStream = new BufferedOutputStream(
        new FileOutputStream(file));) {
      XMLSerializer xmlSerializer = new XMLSerializer(outStream);
      xmlSerializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
      XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(
        cas.getTypeSystem());
      xmiCasSerializer.serialize(cas, 
        xmlSerializer.getContentHandler());
    } catch (SAXParseException sae) {
      System.err.print("SAX");
      file.delete();
      throw new IOException(sae);
    } catch (final SAXException se) {
      throw new IOException(se);
    }
	}
}
