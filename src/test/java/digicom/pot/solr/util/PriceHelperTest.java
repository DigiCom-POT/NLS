package digicom.pot.solr.util;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digicom.pot.nlp.util.OpenNLPUtil;

public class PriceHelperTest {

	private static OpenNLPUtil extractor;
	Logger logger = LoggerFactory.getLogger(PriceHelperTest.class);

	@Before
	public void setUp() throws Exception {
		try {
			extractor = new OpenNLPUtil("test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testParseString() throws IOException {
		PriceHelper pricehelper = new PriceHelper();
		logger.info("Result value : "
				+ pricehelper.parseString("Camera under 10$", extractor));
		logger.info("Result value : "
				+ pricehelper.parseString("Camera above 10$", extractor));
		logger.info("Result value : "
				+ pricehelper
						.parseString("red tshirt less than 20$", extractor));
		logger.info("Result value : "
				+ pricehelper
						.parseString("toys price more than 20$", extractor));
		logger.info("Result value : "
				+ pricehelper.parseString("Camera with good battery life",
						extractor));
	}

}
