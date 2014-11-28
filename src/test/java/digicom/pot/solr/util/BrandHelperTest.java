package digicom.pot.solr.util;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digicom.pot.nlp.util.OpenNLPUtil;

public class BrandHelperTest {

	private static OpenNLPUtil extractor;
	Logger logger = LoggerFactory.getLogger(BrandHelperTest.class);

	@Before
	public void setUp() throws Exception {
		try {
			extractor = new OpenNLPUtil("test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetBrands() {
		BrandHelper brandHelper = new BrandHelper();
		logger.info("Result value : "
				+ brandHelper.getBrands("Nike running shoes ", extractor));
		logger.info("Result value : "
				+ brandHelper.getBrands("ProGear sports red tshirt", extractor));
	}

}
