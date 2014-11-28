package digicom.pot.solr.util;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digicom.pot.nlp.util.OpenNLPUtil;

public class ColorHelper {

	Logger logger = LoggerFactory.getLogger(ColorHelper.class);
	
	/**
	 * Returns the colors if identified from the String
	 * 
	 * @param query
	 * @return
	 */
	public List<String> getColors(String query, OpenNLPUtil extractor) {
		List<String> resultArr = new ArrayList<String>();
		for (String sentence : extractor.segmentSentences(query)) {
			String[] tokens = extractor
					.tokenizeSentence(sentence.toLowerCase());
			Span[] spans = extractor.findColor(tokens);
			double[] spanProbs = extractor.findColorProb(spans);
			int counter = 0;
			for (Span span : spans) {
				StringBuffer color = new StringBuffer("");
				for (int i = span.getStart(); i < span.getEnd(); i++) {
					color.append(tokens[i]);
					if (i < span.getEnd()) {
						color.append(" ");
					}
				}
				logger.info("color span: " + color);
				logger.info("Probability is: " + spanProbs[counter]);
				if (spanProbs[counter] > 0.75 && !color.equals("")) {
					resultArr.add(color.toString().trim());
				}
				counter++;
			}
		}
		return resultArr;
	}

}
