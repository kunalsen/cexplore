package sw.proj.rdf;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.ARP;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.StatementHandler;

/**
 * This an RDFXML handler to convert rdf statements into json. It extends the class ARP provided by the jena package
 * @author kunal
 *
 */
public class RDFXMLProcessor extends ARP {

	public RDFXMLProcessor() {
		super();
		this.getOptions().setLaxErrorMode();
	}

	class RDFXMLStatementHandler implements StatementHandler {
		List<JSONObject> jsons;
		String intendedComponent;
	    //sets the intended component
		RDFXMLStatementHandler(int code, String dataset) {
			jsons = new ArrayList<JSONObject>();

			if (dataset.equalsIgnoreCase("dbpedia_query")) {
				switch (code) {
				case 1:
					intendedComponent = "redirection";
					break;
				case 2:
					intendedComponent = "disambiguation";
					break;
				case 3:
					intendedComponent = "class";
					break;
				case 4:
					intendedComponent = "topics";
					break;
				case 5:
					intendedComponent = "owlsameas";
					break;
				case 6:
					intendedComponent = "abstract";
					break;
				}
			} else if (dataset.equalsIgnoreCase("dbpedia_class")) {
				switch (code) {
				case 1:
					intendedComponent = "subclassof";
					break;
				case 2:
					intendedComponent = "superclassof";
					break;
				}
			} else if (dataset.equalsIgnoreCase("dbpedia_foaf")) {
				intendedComponent = "foaf";
			} else if (dataset.equalsIgnoreCase("compare")) {
				intendedComponent = "comp";
			} else if (dataset.equalsIgnoreCase("dbpedia_prop")) {
				switch (code) {
				case 1:
					intendedComponent = "subjprop";
					break;
				case 2:
					intendedComponent = "objprop";
				}
			}
			else if(dataset.equals("dbpedia_instance")){intendedComponent = "type";}
		}

		List<JSONObject> retrieveJSONObjects() {
			Collections.sort(jsons, new Comparator<JSONObject>() {
				public int compare(JSONObject j1, JSONObject j2) {
					try {
						int comp = j1.getString("type").compareTo(
								j2.getString("type"));
						if (comp == 0)
							return j1.getString("displayname").compareTo(
									j2.getString("displayname"));
						return comp;
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
					return 0;
				}
			});
			return jsons;
		}
		
		/**
		 * Implementation of statement method
		 * This method converts an statement of type <SPO> with O as a literal into json
		 */
		public void statement(AResource a, AResource b, ALiteral l) {
			String subj = a.toString();
			String auri = a.toString();
			subj = subj.substring(subj.lastIndexOf('/') + 1);
			try {
				JSONObject js = new JSONObject();
				if (intendedComponent.equals("objprop")) {
					throw new JSONException(
							l.toString()
									+ ": literals as object cannot possibly occur for objprop query");
				}
				js.put("orig", URLDecoder.decode(subj));
				js.put("origuri", auri);
				if (intendedComponent.equals("subjprop")) {
					String p = b.toString();
					js.put("typeuri", p);
					js.put("type", p.substring(p.lastIndexOf('/')+1));
				} else if (intendedComponent.equals("foaf")) {
					js.put("type", b.toString());
				} else if (intendedComponent.equals("comp")) {
					if (b.toString().equals(
							"http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
						js.put("type", "All compared entities are");
					} else if (b.toString().contains(
							"http://dbpedia.org/ontology")) {
						String type = b.toString().substring(
								"http://dbpedia.org/ontology/".length());
						js.put("type", "All compared entities have " + type
								+ " as");
					} else if (b.toString().contains(
							"http://dbpedia.org/property")) {
						String type = b.toString().substring(
								"http://dbpedia.org/property/".length());
						js.put("type", "All compared entities have " + type
								+ " as");
					} else {
						js.put("type", b.toString());
					}
				} else {
					js.put("type", intendedComponent);
				}
				js.put("displayname", l.toString());
				js.put("literal", l.toString());
				jsons.add(js);
				// System.out.println("Triple found :: " + js.toString());
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
		}
		
		/**
		 * Implementation of statement method
		 * This method converts an statement of type <SPO> with O as a resource into json
		 */
		public void statement(AResource a, AResource b, AResource l) {
			if (a.toString().equals(l.toString()))
				return;
			String luri = l.toString();
			luri = luri.replace("_%28disambiguation%29", "");
			// String disp = l.toString();
			String disp = luri;
			disp = disp.substring(disp.lastIndexOf('/') + 1);
			String subj = a.toString();
			subj = subj.substring(subj.lastIndexOf('/') + 1);
			try {
				if (disp.equals("owl#Thing")) {
					return;
				}
				JSONObject js = new JSONObject();
				if (intendedComponent.equals("objprop")) {
					String ss = URLDecoder.decode(luri);
					js.put("orig", ss.substring(ss.lastIndexOf('/')+1));
					js.put("origuri", luri);
					String p = b.toString();
					js.put("typeuri", p);
					js.put("type", "is " + p.substring(p.lastIndexOf('/') + 1) + " of");
					js.put("displayname", URLDecoder.decode(subj));
					js.put("uri", a.toString());
					jsons.add(js);
					return;
				}
				js.put("orig", URLDecoder.decode(subj));
				js.put("origuri", a.toString());
				if (intendedComponent.equals("subjprop")) {
					String p = b.toString();
					js.put("typeuri", p);
					js.put("type", p.substring(p.lastIndexOf('/')+1));
				} else if (intendedComponent.equals("foaf")) {
					js.put("type", b.toString());
				} else if (intendedComponent.equals("comp")) {
					if (b.toString().equals(
							"http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
						js.put("type", "All compared entities are");
					} else if (b.toString().equals(
							"http://purl.org/dc/terms/subject")) {
						js.put("type",
								"All compared entities fall in the category.");
					} else if (b.toString().contains(
							"http://dbpedia.org/ontology")) {
						String type = b.toString().substring(
								"http://dbpedia.org/ontology/".length());
						js.put("type", "All compared entities have " + type
								+ " as");
					} else if (b.toString().contains(
							"http://dbpedia.org/property")) {
						String type = b.toString().substring(
								"http://dbpedia.org/property/".length());
						js.put("type", "All compared entities have" + type
								+ " as");
					} else {
						js.put("type", b.toString());
					}
				} else {
					js.put("type", intendedComponent);
				}
				js.put("displayname", URLDecoder.decode(disp));
				js.put("uri", luri);
				jsons.add(js);
				// System.out.println("Triple found :: " + js.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class RDFXMLErrorHandler implements ErrorHandler {
		public void fatalError(SAXParseException e) throws SAXParseException {
			System.out.println("FATAL ERROR!");
			e.printStackTrace();
			throw e;
		}

		public void error(SAXParseException e) throws SAXParseException {
			System.out.println("ERROR!");
			e.printStackTrace();
			throw e;
		}

		public void warning(SAXParseException e) throws SAXParseException {
			System.out.println("WARNING..");
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * The process method should be called from the clients to generate json objects
	 * from a query and its results
	 * @param dataset_query
	 * @param resultMap
	 * @return
	 */
	public List<JSONObject> process(String dataset_query,
			Map<Integer, String> resultMap) {

		List<JSONObject> jsonsResponse = new ArrayList<JSONObject>();
		this.getHandlers().setErrorHandler(new RDFXMLErrorHandler());

		Iterator<Integer> itr = resultMap.keySet().iterator();
		Pattern uripattern1 = Pattern.compile("(<([\\w])*:[\\d]([\\w])*)");
		Pattern uripattern2 = Pattern.compile("(</([\\w])*:[\\d]([\\w])*)");
		while (itr.hasNext()) {

			int num = itr.next();
			String inmap = resultMap.get(num);
			boolean tryagain = false;
			try {
				RDFXMLStatementHandler sh = new RDFXMLStatementHandler(num,
						dataset_query);
				this.getHandlers().setStatementHandler(sh);
				this.load(new StringReader(inmap));
				List<JSONObject> rs = sh.retrieveJSONObjects();
				jsonsResponse.addAll(rs);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				tryagain = true;
			} catch (SAXParseException s) {
				s.printStackTrace();
				tryagain = true;
			} catch (SAXException ss) {
				ss.printStackTrace();
				tryagain = true;
			}
			
			if (tryagain) {
				RDFXMLStatementHandler sh = new RDFXMLStatementHandler(num,
						dataset_query);
				this.getHandlers().setStatementHandler(sh);
				Matcher m = uripattern1.matcher(inmap);
				StringBuffer sb = new StringBuffer();
				while (m.find()) {
					m.appendReplacement(sb, m.group().replace(":", ":_"));
				}
				m.appendTail(sb);
				m = uripattern2.matcher(sb.toString());
				sb = new StringBuffer();
				while (m.find()) {
					m.appendReplacement(sb, m.group().replace(":", ":_"));
				}
				m.appendTail(sb);
				try {
					this.load(new StringReader(sb.toString()));
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				List<JSONObject> rs = sh.retrieveJSONObjects();
				jsonsResponse.addAll(rs);
			}
		}
		System.out.println("process:: Json done");
		return jsonsResponse;
	}

	
}
