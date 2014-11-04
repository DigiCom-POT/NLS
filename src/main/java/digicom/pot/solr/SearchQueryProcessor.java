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
		if (null != brands && !brands.isEmpty()) {
			query.addFilterQuery("P_Brand:" + brands.get(0));
		}
		return queryString;
	}

	public static String applyColorFilter(String queryString,
			OpenNLPUtil extractor, SolrQuery query) {
		ColorHelper colorhelper = new ColorHelper();
		List<String> colors = colorhelper.getColors(queryString, extractor);
		if (null != colors && !colors.isEmpty()) {
			query.addFilterQuery("P_Color:" + colors.get(0));
			// String newQueryString = queryString.replace(colors.get(0), "");
			// return newQueryString;
		}
		return queryString;
	}

	public static String applyPriceFilter(String queryString,
			OpenNLPUtil extractor, SolrQuery query) {
		String updateQSTR = queryString;
		PriceHelper pricehelper = new PriceHelper();
		System.out.println(" Applying Price Helper to find price in query");
		Map<String, String> price = pricehelper.parseString(updateQSTR,
				extractor);
		System.out.println(" Got Value :: " + price);

		if (null != price.get("filter")) {
			updateQSTR = price.get("query");
			query.addFilterQuery("P_OfferPrice:" + price.get("filter"));
		}

		return updateQSTR;
	}

	public static String solrsearch(String queryString) throws IOException,
			SolrServerException {
		HttpSolrServer solr = new HttpSolrServer(
				"http://localhost:8983/solr/nls");
		SolrQuery query = new SolrQuery();
		query.setStart(0);
		System.out.println(" Query  :: " + query);
		if(null == queryString) {
			System.out.println("Query is empty");
			return "";
		}
		OpenNLPUtil extractor = new OpenNLPUtil();
		queryString = applyPriceFilter(queryString, extractor, query);
		queryString = applyColorFilter(queryString, extractor, query);
		queryString = applyBrandFilter(queryString, extractor, query);
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
