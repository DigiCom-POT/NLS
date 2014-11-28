/**
 * 
 */
package digicom.pot.solrnlp.web;

import static spark.Spark.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import digicom.pot.solr.SearchQueryProcessor;

/**
 * Simple Web Client for SOLR using spark java
 * 
 * @author Sagar
 * 
 */
public class ClientApp {

	static Logger logger = LoggerFactory.getLogger(ClientApp.class);
	static SearchQueryProcessor queryProcessor = new SearchQueryProcessor();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		setPort(4777);
		staticFileLocation("/webapp");

		get("/hello-nlp", (req, res) -> "hello world from the web client");

		get("/search", (request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			String searchTerm = request.queryParams("searchTerm");
			String flow = request.queryParams("flow");
			if (null == flow) {
				flow = "nlp";
			}
			logger.info("Request recieved :" + searchTerm + " Flow :" + flow);
			try {
				return queryProcessor.solrsearch(searchTerm, flow);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		});
	}
}
