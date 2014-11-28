package digicom.pot.solr.util;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digicom.pot.nlp.util.OpenNLPUtil;

public class BrandHelper {

	Logger logger = LoggerFactory.getLogger(BrandHelper.class);

	/**
	 * Returns the colors if identified from the String
	 * 
	 * @param query
	 * @return
	 */
	public List<String> getBrands(String query, OpenNLPUtil extractor) {
		List<String> resultArr = new ArrayList<String>();
		for (String sentence : extractor.segmentSentences(query)) {
			String[] tokens = extractor.tokenizeSentence(sentence);
			Span[] spans = extractor.findBrand(tokens);
			double[] spanProbs = extractor.findBrandProb(spans);
			int counter = 0;
			for (Span span : spans) {
				StringBuffer brand = new StringBuffer("");
				for (int i = span.getStart(); i < span.getEnd(); i++) {
					brand.append(tokens[i]);
					if (i < span.getEnd()) {
						brand.append(" ");
					}
				}
				logger.info("Brand span:" + brand);
				logger.info("Probability is: " + spanProbs[counter]);
				if (spanProbs[counter] > 0.5 && !brand.equals("")) {
					resultArr.add(brand.toString().trim());
				}
				counter++;
			}
		}
		return resultArr;
	}

}
