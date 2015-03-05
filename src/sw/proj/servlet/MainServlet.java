package sw.proj.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import sw.proj.sparql.SparqlEvaluator;

/**
 * 
 * @author kunal,Adila main servlet handles all the requests Process the query
 *         and call helper classes to make sparql calls.
 * 
 */
public class MainServlet extends HttpServlet {
	/**
	 * doGet implementation this method collects all request parameters
	 * and makes the appropriate calls to get query results
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String type = req.getParameter("type");
		String concept = req.getParameter("concept");
		if (type != null && type.equals("compare")) {
			getcompareResults(concept, resp);
			return;
		}
		if (type != null && type.equals("freeb")) {
			try {
				callFreebase(concept, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		String query = req.getParameter("query");
		// default dbpedia_query
		if (query == null) {
			query = "dbpedia_query";
		}
		
		String chunk = req.getParameter("chunk");
		int chunkNum = 1;
		if (chunk != null) {
			chunkNum = Integer.parseInt(chunk);
		}
		String cont = req.getParameter("cont");
		// All string should be have CamelCase and words separated by underscore
		//if cont != null or concept contains '_' then camelize except if camelcase is not intended
		if ((cont == null && !concept.contains("_"))) {
			String camel = req.getParameter("camel");
			if (camel == null || !camel.contains("false")) {
				concept = toCamelCaseUnderscore(concept);
			} else {
				concept = putUnderscore(concept);
			}
		}
		System.out.println("Concept:: " + concept);
		// SparqlEvaluator eval = SparqlEvaluator.getInstance();
		// RDFXMLProcessor eval = new RDFXMLProcessor();
		if (concept == null)
			return;
		// List<JSONObject> js = eval.process(concept,query);
		System.out.println("Before Process");
		System.out.println(chunkNum);
		List<JSONObject> js = new ArrayList<JSONObject>(); 
		
		try {
			js = SparqlEvaluator.getInstance().process(concept,
					query, chunkNum);
		} catch (Exception e) {
			
			e.printStackTrace();
			resp.getWriter().print("");
		}
		if (js.isEmpty()) {
			resp.getWriter().print("");
			return;
		}
		Iterator<JSONObject> itr = js.iterator();
		System.out.println("Size of list :: " + js.size());

		resp.setContentType("text/html; charset=UTF-8");

		String dset = "dbpedia";

		String respout = "{\"bindings\": [";
		resp.getWriter().print(respout);
		System.out.println(respout);

		
		respout = "{\"type\":\"num\",\"val\":\""
			+ SparqlEvaluator.getCacheNum(concept, query, dset)
			+ "\"},";
		resp.getWriter().print(respout);
		System.out.println(respout);

		boolean first = true;
		int count = 1;
		while (itr.hasNext()) {
			if (!first) {
				respout = ","; resp.getWriter().print(respout); System.out.println(respout);

			}
			first = false;
			String nstr = itr.next().toString();
			resp.getWriter().print(nstr);
			System.out.println((count++) + " :" + nstr);
		}
		System.out.println("After loop");
		resp.getWriter().print("]}");
		System.out.println("Done");

	}

	/**
	 * This method replaces all spaces with underscore
	 * DBpedia has concept names like Alan_Turing
	 * @param concept
	 * @return
	 */
	private String putUnderscore(String concept) {
		StringBuffer newConcept = new StringBuffer("");
		// concept = concept.replace(" ", "_");
		StringTokenizer token = new StringTokenizer(concept, " ");
		boolean firstTest = true;
		while (token.hasMoreTokens()) {
			if (!firstTest) {
				newConcept.append("_");
			}
			firstTest = false;
			String str = (String) token.nextElement();
			newConcept.append(str);
		}

		concept = newConcept.toString();

		return concept;

	}

	/**
	 * Converts a concept string to DBpedia friendly syntax
	 * @param concept
	 * @return
	 */
	private String toCamelCaseUnderscore(String concept) {
		StringBuffer newConcept = new StringBuffer("");
		// concept = concept.replace(" ", "_");
		StringTokenizer token = new StringTokenizer(concept, " ");
		boolean firstTest = true;
		while (token.hasMoreTokens()) {
			if (!firstTest) {
				newConcept.append("_");
			}
			firstTest = false;
			String str = (String) token.nextElement();
			str = str.substring(0, 1).toUpperCase()
					+ str.substring(1).toLowerCase();
			newConcept.append(str);
		}

		concept = newConcept.toString();

		return concept;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	/**
	 * Method to call freebase endpoint. This is currently for testing
	 * @param concept
	 * @param resp
	 * @throws Exception
	 */
	private void callFreebase(String concept, HttpServletResponse resp)
			throws Exception {
		URL url = new URL("http://www.freebase.com/api/service/search?query="
				+ concept);
		URLConnection connection = url.openConnection();
		// out.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection
				.getInputStream()));

		String decodedString;
		StringBuffer sbuff = new StringBuffer();
		while ((decodedString = in.readLine()) != null) {
			/* System.out.println(decodedString); */
			sbuff.append(decodedString);
		}
		in.close();
		resp.setContentType("text/html; charset=UTF-8");
		resp.getWriter().print(sbuff.toString());
	}
 
	/**
	 * The client can make calls to compare two uris. This requires us to analyze all the 
	 * property values which are shared by these uris for the same property
	 * @param concept
	 * @param resp
	 */
	private void getcompareResults(String concept, HttpServletResponse resp) {
		try {
			List<JSONObject> js = SparqlEvaluator.getInstance().getRelation(
					concept);
			Iterator<JSONObject> itr = js.iterator();
			System.out.println("Size of list :: " + js.size());

			resp.setContentType("text/html; charset=UTF-8");

			String dset = "dbpedia";

			resp.getWriter().print("{\"bindings\": [");

			// resp.getWriter().println("Response from Servlet"+js.size());
			boolean first = true;
			int count = 1;
			while (itr.hasNext()) {
				if (!first) {
					resp.getWriter().print(",");
				}
				first = false;
				resp.getWriter().print(itr.next().toString());
				System.out.println(count++);
			}
			System.out.println("After loop");
			resp.getWriter().print("]}");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
