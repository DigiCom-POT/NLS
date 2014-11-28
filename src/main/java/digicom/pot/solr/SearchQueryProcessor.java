package digicom.pot.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import digicom.pot.nlp.util.OpenNLPUtil;
import digicom.pot.solr.util.BrandHelper;
import digicom.pot.solr.util.ColorHelper;
import digicom.pot.solr.util.PriceHelper;

/**
 * @author Sagar
 *
 */
public class SearchQueryProcessor {

	static Logger logger = LoggerFactory.getLogger(SearchQueryProcessor.class);
	private static OpenNLPUtil extractor = null;
	private static String NLS_COLLECTION = "http://localhost:8983/solr/nls";
	private static HttpSolrServer solr = new HttpSolrServer(NLS_COLLECTION);

	private static String COLOR_ATTR = "P_Color:";
	private static String PRICE_ATTR = "P_OfferPrice:";
	private static String BRAND_ATTR = "P_Brand:";
	/**
	 * 
	 * @param queryString
	 * @param extractor
	 * @param query
	 * @return
	 */
	public String applyBrandFilter(String queryString, OpenNLPUtil extractor,
			SolrQuery query) {
		BrandHelper brandHelper = new BrandHelper();
		List<String> brands = brandHelper.getBrands(queryString, extractor);
		logger.info("Brand:" + brands);
		if (null != brands && !brands.isEmpty()) {
			// From filter query changing to boost query as it is making it
			// mandatory
			query.set("bq", BRAND_ATTR + brands.get(0) + "^50");
		}
		return queryString;
	}

	/**
	 * 
	 * @param queryString
	 * @param extractor
	 * @param query
	 * @return
	 */
	public String applyColorFilter(String queryString, OpenNLPUtil extractor,
			SolrQuery query) {
		ColorHelper colorhelper = new ColorHelper();
		List<String> colors = colorhelper.getColors(queryString, extractor);
		logger.info("Colors:" + colors);
		if (null != colors && !colors.isEmpty()) {
			// Adding the filter query for color
			query.addFilterQuery(COLOR_ATTR + colors.get(0));
		}
		return queryString;
	}

	/**
	 * 
	 * @param queryString
	 * @param extractor
	 * @param query
	 * @return
	 */
	public String applyPriceFilter(String queryString, OpenNLPUtil extractor,
			SolrQuery query) {
		String updateQSTR = queryString;
		PriceHelper pricehelper = new PriceHelper();
		Map<String, String> price = pricehelper.parseString(updateQSTR,
				extractor);
		logger.info("Price Filter:" + price);
		if (null != price.get("filter")) {
			updateQSTR = price.get("query");
			query.addFilterQuery(PRICE_ATTR + price.get("filter"));
		}
		return updateQSTR;
	}

	/**
	 * 
	 * @param queryString
	 * @param flow
	 * @return
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public String solrsearch(String queryString, String flow)
			throws IOException, SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setStart(0);
		if (null == queryString) {
			logger.info("Query is empty");
			return "";
		}

		if (!flow.equals("old")) {
			query.set("defType", "edismax");
			if (null == extractor) {
				extractor = new OpenNLPUtil();
			}
			applyColorFilter(queryString, extractor, query);
			applyBrandFilter(queryString, extractor, query);
			queryString = applyPriceFilter(queryString, extractor, query);
		}
		query.setQuery(queryString);

		logger.info("After Processing Query :: " + query);
		QueryResponse response = solr.query(query);
		SolrDocumentList results = response.getResults();

		logger.info("No of Docs returned : " + results.size());
		for (int i = 0; i < results.size(); ++i) {
			logger.debug(results.get(i).toString());
		}
		Gson j = new Gson();
		return j.toJson(results);
	}
}
