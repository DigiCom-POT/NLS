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
		// TODO Auto-generated method stub
				HttpSolrServer solr = new HttpSolrServer(
						"http://localhost:8983/solr/nls");
				SolrQuery query = new SolrQuery();
				String queryString = "Progear red towel";

				// query.addFilterQuery("cat:electronics","store:amazon.com");
				// query.setFields("id","price","merchant","cat","store");
				query.setStart(0);
				query.set("defType", "edismax");

				System.out.println(" Query  :: " + query);
				OpenNLPUtil extractor = new OpenNLPUtil();
				queryString = SearchQueryProcessor.applyPriceFilter(queryString, extractor, query);
				queryString = SearchQueryProcessor.applyColorFilter(queryString, extractor, query);
				queryString = SearchQueryProcessor.applyBrandFilter(queryString, extractor, query);
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
