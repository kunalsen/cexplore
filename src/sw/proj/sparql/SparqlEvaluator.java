package sw.proj.sparql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javatools.administrative.Announce;

import org.json.JSONObject;

import sw.proj.db.JdbcConnector;
import sw.proj.entity.CacheKey;
import sw.proj.rdf.RDFXMLProcessor;
import sw.proj.utility.PropertyManager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;

import converters.RDFSConverter;

/**
 * 
 * @author Kunal,Adila This class makes the calls to sparql endpoints.
 * 
 */

public class SparqlEvaluator {

	private static SparqlEvaluator eval;

	private static Map<CacheKey, List<JSONObject>> kQueryCache = new HashMap<CacheKey, List<JSONObject>>();
	private static int kChunkSize = 3400;
	private static Map<CacheKey, Integer> kChunkNumCache = new HashMap<CacheKey, Integer>();
	
	
	/**
	 * private constructor such that no class outside this class can instantiate
	 * an object of this class.
	 */
	private SparqlEvaluator() {

	}

	/**
	 * Singleton implementation : returns a singleton instance of this class
	 * 
	 * @return
	 */
	public static SparqlEvaluator getInstance() {
		if (eval == null) {
			return new SparqlEvaluator();
		}
		return eval;
	}

	public static int getCacheNum(String term, String query, String dataset) {
		return kChunkNumCache.get(new CacheKey(term, query, 1, dataset));
	}

	/**
	 * Process method is called to process a sparql query request
	 * 
	 * @param term
	 *            is the concept term which is searched for
	 * @param query
	 *            determines the type of query
	 * @return
	 * @throws IOException
	 */
	public List<JSONObject> process(String term, String query)
			throws IOException, SQLException {
		return process(term, query, 1);
	}
    /**
     * Process method implementation
     * @param term
     * @param query
     * @param sequence
     * @return
     * @throws IOException
     * @throws SQLException
     */
	public List<JSONObject> process(String term, String query, int sequence)
			throws IOException, SQLException {
		
		String dataset = "dbpedia";
		// check the query type and assign proper dataset value to be queried
		if (query.equals("dbpedia_query") || query.equals("dbpedia_class")
				|| query.equals("dbpedia_foaf") || query.equals("dbpedia_prop")
				|| query.equals("dbpedia_instance")) {
			dataset = "dbpedia";
		} else if (query == "yago_class") {
			dataset = "yago";
		}
		// lookup cache for the term has already been searched return value from
		// cache
		CacheKey cacheKey = new CacheKey(term, query, sequence, dataset);

		if (kQueryCache.containsKey(cacheKey)) {
			return kQueryCache.get(cacheKey);
		}
        
		//get all the query parameters
		Map<Integer, Map<String, String>> queryGroup = getQuery(term, query,
				dataset);

		Set<Integer> groups = queryGroup.keySet();
		Iterator<Integer> itr = groups.iterator();
		
		//prepare a collection of triples which are returned as query results
		Map<Integer, String> tripleMap = new HashMap<Integer, String>();
		try {
			while (itr.hasNext()) {
				int key = (Integer) itr.next();
				Map<String, String> params = queryGroup.get(key);
				//make a call to sparql endpoint
				String result = getSparqlCallResult(dataset, term, params);
				tripleMap.put(key, result);
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		RDFXMLProcessor processor = new RDFXMLProcessor();
		List<JSONObject> result = processor.process(query, tripleMap);
		if (result.size() == 0) {
			return new ArrayList<JSONObject>();
		}
		//populate the cache with the results
		int numOfChunks = populateCache(term, query, dataset, result);
		kChunkNumCache.put(new CacheKey(term, query, 1, dataset), numOfChunks);
		return kQueryCache.get(cacheKey);
	}

	/**
	 * For a given concept name, query type and endpoint get the SPARQL query parameters for all the queries to
	 * be sent to the endpoint as request
	 * 
	 * @param concept
	 * @param query
	 * @param endpoint
	 * @return
	 */
	private Map<Integer, Map<String, String>> getQuery(String concept,
			String query, String endpoint) throws SQLException {
		// queryGroup represents the data structure containing all the sparql
		// query parameters
		Map<Integer, Map<String, String>> queryGroup = new HashMap<Integer, Map<String, String>>();
		// fetch the parameters from the database
		Connection conn = null;
		try {
			conn = JdbcConnector.getConnection();

			PreparedStatement st = conn
					.prepareStatement("select param.name,param.value,param.query_seq from param ,query"
							+ " where param.query_id = query.string and query.string =? and query.endpoint_name=?");

			st.setString(1, query);
			st.setString(2, endpoint);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int groupNo = rs.getInt(3);
				if (!queryGroup.containsKey(groupNo)) {
					Map<String, String> params = new HashMap<String, String>();
					queryGroup.put(new Integer(groupNo), params);
				}
				queryGroup.get(groupNo).put(rs.getString(1), rs.getString(2));
				// queryString.put();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return queryGroup;
	}

	/**
	 * This method makes http request(s) to the SPARQL Endpoint and returns the set of triples from the result
	 * @param endpoint
	 * @param concept
	 * @param parameters
	 * @return
	 * @throws IOException
	 */
	private String getSparqlCallResult(String endpoint, String concept,
			Map<String, String> parameters) throws IOException {

		if (endpoint == "yago") {
			return getSparqlCallResultYago(concept, parameters);
		}
		String uri = getEndPointUrl(endpoint);

		Set<String> keys = parameters.keySet();
		Iterator<String> itr = keys.iterator();
		StringBuffer buff = new StringBuffer("");
		while (itr.hasNext()) {
			String name = itr.next();
			String value = (String) parameters.get(name);

			if (name.equals("query")) {
				value = value.replace("%TERM%", concept.replace("(", "%28")
						.replace(")", "%29"));
				System.out.println("Sparql Query :: " + value);
			}

			buff.append(name + "=" + URLEncoder.encode(value, "UTF-8") + "&");
		}
		
		URL url = new URL(uri + "?" + buff.toString());
		
		URLConnection connection = url.openConnection();
		// out.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String decodedString;
		StringBuffer sbuff = new StringBuffer();
		while ((decodedString = in.readLine()) != null) {
			
			sbuff.append(decodedString);
		}
		in.close();
       
		return sbuff.toString();
	}

	/**
	 * Sparql call result for YAGO dataset. This method is for trial
	 * @param concept
	 * @param parameters
	 * @return
	 */
	private String getSparqlCallResultYago(String concept,
			Map<String, String> parameters) {

		try {
			String directory = PropertyManager.getPropertyValue("sw",
					"cexplore.classpath") + "/db/yago2core";

			Announce.doing("Connecting to YAGO Jena version at " + directory);
			Model model = TDBFactory.createModel(directory);
			Announce.done();
			final String prefixes = "PREFIX : <"
					+ RDFSConverter.ns
					+ ">\n"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";

			final String sparqlstr = prefixes
					+ "\nCONSTRUCT { :Albert_Einstein rdf:type ?x } WHERE { :Albert_Einstein rdf:type ?x . }";
			Query query = QueryFactory.create(sparqlstr);

			final QueryExecution qexec = QueryExecutionFactory.create(query,
					model);
			Model result = qexec.execConstruct();
			StmtIterator sit = result.listStatements();
			while (sit.hasNext()) {
				System.out.println(sit.nextStatement().toString());
			}
			Announce.doing("Closing YAGO Jena version");
			Announce.done();
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
		return null;
	}

	/**
	 * For a given dataset returns the corresponding endpoint url
	 * @param endpoint
	 * @return
	 */
	private String getEndPointUrl(String endpoint) {
		Connection conn = null;
		String uri = "";
		try {
			conn = JdbcConnector.getConnection();
			PreparedStatement st = conn
					.prepareStatement("select uri from endpoint where name = ?");

			st.setString(1, endpoint);

			ResultSet rs = st.executeQuery();
			rs.next();
			uri = rs.getString(1);

			// System.out.println(queryString);
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return uri;
	}

	/**
	 * Method to populate cache for a given query and its result triples
	 * @param concept
	 * @param query
	 * @param dataset
	 * @param tripleList
	 * @return
	 */
	private int populateCache(String concept, String query, String dataset,
			List<JSONObject> tripleList) {

		Iterator<JSONObject> itr = tripleList.iterator();
		int chunkNum = 0;

		JSONObject temp = null;

		while (temp != null || itr.hasNext()) {
			int numChars = 0;
			chunkNum++;
			List<JSONObject> chunkList = new ArrayList<JSONObject>();
			CacheKey chunkKey = new CacheKey(concept, query, chunkNum, dataset);
			if (temp != null) {
				chunkList.add(temp);
				numChars += temp.length();
				temp = null;
			}
			while (itr.hasNext()) {
				JSONObject obj = itr.next();

				if (numChars + obj.toString().length() <= kChunkSize) {
					numChars += obj.toString().length();
					chunkList.add(obj);
				} else {
					temp = obj;
					break;
				}
			}
			kQueryCache.put(chunkKey, chunkList);

		}

		
		return chunkNum;
	}

	/**
	 * This method finds all immediate similarities between two or more
	 * concepts.
	 * 
	 * @param concept a list of concept names separated by ;;;;
	 * @return
	 */
	public List<JSONObject> getRelation(String concept) throws Exception {
		StringBuffer sparqlQuery = new StringBuffer();
		
		StringTokenizer tokenizer = new StringTokenizer(concept, ";;;;");
		sparqlQuery.append("construct {rdf:all ?p ?o.} where{");
		boolean first = true;
		String sub = "";
		while (tokenizer.hasMoreTokens()) {

			String token = tokenizer.nextToken();
			if (first) {
				sub = "<" + token + ">";
			}
			sparqlQuery.append("<" + token + "> ?p ?o.");
		}
		sparqlQuery.append("}");

		// System.out.println(sparqlQuery.toString());

		String uri = SparqlEvaluator.getInstance().getEndPointUrl("dbpedia");
		URL url = new URL(
				uri
						+ "?"
						+ "format="
						+ URLEncoder.encode("application/rdf+xml", "UTF-8")
						+ "&"
						+ "debug="
						+ URLEncoder.encode("on", "UTF-8")
						+ "&"
						+ "save="
						+ URLEncoder.encode("display", "UTF-8")
						+ "&"
						+ "query="
						+ URLEncoder.encode(
								sparqlQuery.toString().replace("rdf:all", sub),
								"UTF-8"));
		System.out.println("url :: " + url);
		URLConnection connection = url.openConnection();
		// out.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String decodedString;
		StringBuffer sbuff = new StringBuffer();
		while ((decodedString = in.readLine()) != null) {
			/* System.out.println(decodedString); */
			sbuff.append(decodedString);
		}
		in.close();
		System.out.println(sbuff.toString());
		RDFXMLProcessor rdfProcessor = new RDFXMLProcessor();
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, sbuff.toString());
		List<JSONObject> list = rdfProcessor.process("compare", map);
		System.out.println(list.toString());
		return list;
	}

	/**
	 * Test method
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/*
		 * List<JSONObject> list =
		 * SparqlEvaluator.getInstance().process("Organisation",
		 * "dbpedia_class",1);
		 * 
		 * Iterator itr = list.iterator(); int count=1; while(itr.hasNext()){
		 * 
		 * System.out.println(count++ +"   = "+itr.next()); }
		 */
		// System.out.println();
		String c = "Athlete", q = "dbpedia_instance";
		for (int i = 1; i < 2; ++i) {
			List<JSONObject> jsons = SparqlEvaluator.getInstance().process(c,q, i);
			if (jsons == null)
				break;
			System.out.println("chunks :: " + i);
			for (JSONObject j : jsons)
				System.out.println(j);
		}
		// System.out.println("cache num :: " + SparqlEvaluator.getCacheNum(c,
		// d, q));

	}
}
