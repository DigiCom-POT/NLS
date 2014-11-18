/**
 * 
 */
package digicom.pot.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

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

	
	private static OpenNLPUtil extractor = null; 
	private static HttpSolrServer solr = new HttpSolrServer(
			"http://localhost:8983/solr/nls");
	/**
	 * Move the main to test methods
	 * @param args
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SolrServerException,
			IOException {
		
	}

	public static String applyBrandFilter(String queryString,
			OpenNLPUtil extractor, SolrQuery query) {
		BrandHelper brandHelper = new BrandHelper();
		List<String> brands = brandHelper.getBrands(queryString, extractor);
		System.out.println("Brand:" + brands );
		if (null != brands && !brands.isEmpty()) {
			// From filter query changing to boost query as it is making it mandatory
			//query.addFilterQuery("P_Brand:" + brands.get(0));
			query.set("bq", query.get("bq"), "P_Brand:" + brands.get(0) + "^50");
		}
		return queryString;
	}

	public static String applyColorFilter(String queryString,
			OpenNLPUtil extractor, SolrQuery query) {
		ColorHelper colorhelper = new ColorHelper();
		List<String> colors = colorhelper.getColors(queryString, extractor);
		System.out.println("Colors:" + colors);
		if (null != colors && !colors.isEmpty()) {
			// From filter query changing to boost query as it is making it mandatory			
			query.addFilterQuery("P_Color:" + colors.get(0));
			//query.set("bq", "P_Color:" + colors.get(0) + "^20");
			//String newQueryString = queryString.replace(colors.get(0), "");
			//return newQueryString;
		}
		return queryString;
	}

	public static String applyPriceFilter(String queryString,
			OpenNLPUtil extractor, SolrQuery query) {
		String updateQSTR = queryString;
		PriceHelper pricehelper = new PriceHelper();
		Map<String, String> price = pricehelper.parseString(updateQSTR,
				extractor);
		System.out.println("Price Filter:" + price );
		if (null != price.get("filter")) {
			updateQSTR = price.get("query");
			query.addFilterQuery("P_OfferPrice:" + price.get("filter"));
		}

		return updateQSTR;
	}

	public static String solrsearch(String queryString) throws IOException,
			SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setStart(0);
		query.set("defType", "edismax");
		System.out.println(" Query  :: " + query);
		if(null == queryString) {
			System.out.println("Query is empty");
			return "";
		}
		if(null == extractor) {
			extractor = new OpenNLPUtil();
		}
		applyColorFilter(queryString, extractor, query);
		applyBrandFilter(queryString, extractor, query);
		queryString = applyPriceFilter(queryString, extractor, query);
		query.setQuery(queryString);

		System.out.println("After Query  :: " + query);
		QueryResponse response = solr.query(query);
		SolrDocumentList results = response.getResults();

		System.out.println(" No of Docs returned : " + results.size());
		for (int i = 0; i < results.size(); ++i) {
			System.out.println(results.get(i));
		}
		Gson j = new Gson();
		return j.toJson(results);

	}
}
