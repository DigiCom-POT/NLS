/**
 * 
 */
package digicom.pot.nlp.custom.model;

import java.io.File;

import opennlp.addons.modelbuilder.DefaultModelBuilderUtil;

/**
 * @author Sagar
 *
 */
public class CreateNERModel {

	private static String RESOURCE_FOLDER = "src/main/resources/";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/**
	     * establish a file to put sentences in
	     */
	    File sentences = new File(RESOURCE_FOLDER + "cmodel/sentences.txt");

	    /**
	     * establish a file to put your NER hits in (the ones you want to keep based on prob)
	     */
	    File knownEntities = new File(RESOURCE_FOLDER + "cmodel//knownentities.txt");

	    /**
	     * establish a BLACKLIST file to put your bad NER hits in (also can be based on prob)
	     */
	    File blacklistedentities = new File(RESOURCE_FOLDER + "cmodel/blentities.txt");

	    /**
	     * establish a file to write your annotated sentences to
	     */
	    File annotatedSentences = new File(RESOURCE_FOLDER + "cmodel/annotatedSentences.txt");

	    /**
	     * establish a file to write your model to
	     */
	    File theModel = new File(RESOURCE_FOLDER + "cmodel/colorModel.bin");

	    /**
	     * THIS IS WHERE THE ADDON IS GOING TO USE THE FILES (AS IS) TO CREATE A NEW MODEL. YOU SHOULD NOT HAVE TO RUN THE FIRST PART AGAIN AFTER THIS RUNS, JUST NOW PLAY WITH THE
	     * KNOWN ENTITIES AND BLACKLIST FILES AND RUN THE METHOD BELOW AGAIN UNTIL YOU GET SOME DECENT RESULTS (A DECENT MODEL OUT OF IT).
	     */
	    DefaultModelBuilderUtil.generateModel(sentences, knownEntities, blacklistedentities, theModel, annotatedSentences, "color", 3);
	}
}
