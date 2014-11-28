package digicom.pot.solr.util;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digicom.pot.nlp.util.OpenNLPUtil;

public class ColorHelperTest {

	private static OpenNLPUtil extractor;
	Logger logger = LoggerFactory.getLogger(ColorHelperTest.class);

	@Before
	public void setUp() throws Exception {
		try {
			extractor = new OpenNLPUtil("test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetColors() throws IOException {
		ColorHelper colorHelper = new ColorHelper();
		logger.info("Result value : "
				+ colorHelper.getColors("red blue shoes", extractor));
		logger.info("Result value : "
				+ colorHelper.getColors("green t-shirt under 20$", extractor));
	}

}
