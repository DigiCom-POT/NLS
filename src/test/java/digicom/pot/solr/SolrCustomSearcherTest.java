package digicom.pot.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import digicom.pot.nlp.util.OpenNLPUtil;

public class SolrCustomSearcherTest {

	@Test
	public void testSolrsearch() throws IOException, SolrServerException {
				HttpSolrServer solr = new HttpSolrServer(
						"http://localhost:8983/solr/nls");
				SolrQuery query = new SolrQuery();
				String queryString = "Progear red towel";
				query.setStart(0);
				query.set("defType", "edismax");

				System.out.println(" Query  :: " + query);
				OpenNLPUtil extractor = new OpenNLPUtil();
				SearchQueryProcessor queryProcessor = new SearchQueryProcessor();
				queryString = queryProcessor.applyPriceFilter(queryString, extractor, query);
				queryString = queryProcessor.applyColorFilter(queryString, extractor, query);
				queryString = queryProcessor.applyBrandFilter(queryString, extractor, query);
				query.setQuery(queryString);
				System.out.println("After Query  :: " + query);
				QueryResponse response = solr.query(query);
				SolrDocumentList results = response.getResults();

				System.out.println(" No of Docs returned : " + results.size());
				for (int i = 0; i < results.size(); ++i) {
					System.out.println(results.get(i));
				}
	}

}
