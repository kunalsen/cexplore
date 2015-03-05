package sw.proj.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import sw.proj.sparql.SparqlEvaluator;


public class TestConcept {

	private List<String> disambiguation = new ArrayList<String>();
	private List<String> topics = new ArrayList<String>();
	private List<String> classes = new ArrayList<String>();
	private List<String> links = new ArrayList<String>();
	private List<String> properties = new ArrayList<String>();
	public void testConcept(String concept) throws Exception{
		int n = testConceptSearch(concept, 1, "dbpedia_query");
		
		for(int i=2;i<=n;i++){
			testConceptSearch(concept, i, "dbpedia_query");
		}
		//get the properties
		testConceptSearch(concept, 1, "dbpedia_prop");
		System.out.println(concept+ "=================== disambiguates ===========================================================================");
		printList(disambiguation);
		System.out.println(concept+ "=================== belongs to class ===========================================================================");
		printList(classes);
		System.out.println(concept+ "=================== belongs to topics ===========================================================================");
		printList(topics);
		System.out.println(concept+ "=================== has properties ===========================================================================");
		printList(properties);
	}
	public int testConceptSearch(String concept, int chunkNum, String query) throws Exception{
		
		
		System.out.println("Concept:: " + concept);
		// SparqlEvaluator eval = SparqlEvaluator.getInstance();
		// RDFXMLProcessor eval = new RDFXMLProcessor();
		if (concept == null)
			return 0;
		// List<JSONObject> js = eval.process(concept,query);
		
		
		List<JSONObject> js = new ArrayList<JSONObject>();

		try {
			js = SparqlEvaluator.getInstance()
					.process(concept, query, chunkNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//resp.getWriter().print("");
		}
		if (js.isEmpty()) {
			//resp.getWriter().print("");
			return 0;
		}
		StringBuilder resp = new StringBuilder();
		Iterator<JSONObject> itr = js.iterator();
		//System.out.println("Size of list :: " + js.size());

		//resp.setContentType("text/html; charset=UTF-8");

		String dset = "dbpedia";

		
		
	
		int count = 1;
		while (itr.hasNext()) {
			
			JSONObject json = itr.next();
			String prop = json.getString("type");
			if(prop.equals("class")){
				classes.add(json.getString("displayname"));
			}else if(prop.equals("topics")){
				topics.add(json.getString("displayname"));
			}else if(prop.equals("disambiguation")){
				disambiguation.add(json.getString("displayname"));
			}
			else if(json.has("typeuri") && !json.getString("typeuri").equals("")){
				properties.add(prop+" - "+json.getString("displayname"));
			}
			resp.append(json.getString("orig").toString()+" property "+json.getString("type")+" value "+json.getString("displayname"));
			resp.append(json.toString());
			//System.out.println((count++) + " :" + json);
		}
		
		return SparqlEvaluator.getCacheNum(concept, query, dset);
	}
	
	private void printList(List<String> list){
		for(String str: list){
			System.out.println(str);
		}
	}
	public static void main(String[] args) throws Exception{
		Scanner in = new Scanner(System.in);
		System.out.println("Enter a concept name : ");
		String input = in.nextLine();
		TestConcept c = new TestConcept();
		String is[] = input.split(" ");
		StringBuilder str = new StringBuilder();
		for(String i:is){
			i = i.substring(0,1).toUpperCase() + i.substring(1, i.length());
			if(!str.toString().equals("")){
				str.append("_"+i);
			}else{
				str.append(i);
			}
		}
		c.testConcept(str.toString());
	}

}
