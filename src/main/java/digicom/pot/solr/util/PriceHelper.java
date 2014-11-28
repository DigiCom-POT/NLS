/**
 * 
 */
package digicom.pot.solr.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.util.Span;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digicom.pot.nlp.util.OpenNLPUtil;

/**
 * @author Sagar
 * 
 */
public class PriceHelper {

	Logger logger = LoggerFactory.getLogger(PriceHelper.class);

	public Map<String, String> parseString(String queryString,
			OpenNLPUtil extractor) {
		Map<String, String> priceQueryParser = new HashMap<String, String>();
		priceQueryParser.put("query", queryString);
		try {
			String money = hasMoney(queryString, extractor);
			if (null != money) {
				String filter = getqueryString(queryString, money, false,
						extractor);
				String query = getqueryString(queryString, money, true,
						extractor);
				priceQueryParser.put("query", query);
				priceQueryParser.put("filter", getNumber(filter));
				return priceQueryParser;
			}
		} catch (Exception e) {
			return priceQueryParser;
		}
		return priceQueryParser;
	}

	private String getqueryString(String queryString, String price,
			boolean flag, OpenNLPUtil extractor) throws IOException {
		String[] tokens = extractor.tokenizeSentence(queryString);
		int moneyPos = 0;

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equalsIgnoreCase(price)) {
				moneyPos = i;
			}
		}

		String prevValue = null;
		if (moneyPos > 1) {
			if (tokens[moneyPos - 1].equalsIgnoreCase("than")) {
				prevValue = tokens[moneyPos - 2] + " " + tokens[moneyPos - 1];
				if (flag) {
					return createQueryString(moneyPos - 2, tokens);
				}
			} else {
				prevValue = tokens[moneyPos - 1];
				if (flag) {
					return createQueryString(moneyPos - 1, tokens);
				}
			}

			switch (prevValue) {
			case "under":
			case "less than":
				return "[* TO " + price + "]";
			case "above":
			case "more than":
				return "[" + price + " TO *]";
			default:
				return queryString;
			}
		}
		return queryString;
	}

	private String createQueryString(int j, String[] tokens) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < j; i++) {
			buffer.append(tokens[i]).append(" ");
		}
		return buffer.toString();
	}

	/**
	 * Replace the current with empty string in query string
	 * 
	 * @param money
	 * @return
	 * @throws ParseException
	 */
	private String getNumber(String money) throws ParseException {
		money = money.replace("$", "");
		money = money.replace(" usd ", "");
		return money;
	}

	/**
	 * Finds the money from the query String e.g. "Camera under 40$" returns 40$
	 * 
	 * @param queryString
	 * @return
	 * @throws IOException
	 */
	private String hasMoney(String queryString, OpenNLPUtil extractor)
			throws IOException {
		for (String sentence : extractor.segmentSentences(queryString)) {
			String[] tokens = extractor.tokenizeSentence(sentence);
			Span[] spans = extractor.findMoney(tokens);
			for (Span span : spans) {
				for (int position = span.getStart(); position < span.getEnd();) {
					return tokens[position];
				}
			}
		}
		return null;
	}

}
