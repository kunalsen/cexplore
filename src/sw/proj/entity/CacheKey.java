package sw.proj.entity;

/**
 * This class is used to generate unique cache key to store queries and their 
 * corresponding results
 * @author kunal
 *
 */
public class CacheKey {
 private String concept;
 private String query;
 private int sequence;
 private String dataset;

 
public CacheKey(String concept, String query, int sequence, String dataset) {
	super();
	this.concept = concept;
	this.query = query;
	this.sequence = sequence;
	this.dataset = dataset;
}

public String getConcept() {
	return concept;
}
public void setConcept(String concept) {
	this.concept = concept;
}
public String getQuery() {
	return query;
}
public void setQuery(String query) {
	this.query = query;
}
public int getSequence() {
	return sequence;
}
public void setSequence(int sequence) {
	this.sequence = sequence;
}
public String getDataset() {
	return dataset;
}
public void setDataset(String dataset) {
	this.dataset = dataset;
}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((concept == null) ? 0 : concept.hashCode());
	result = prime * result + ((dataset == null) ? 0 : dataset.hashCode());
	result = prime * result + ((query == null) ? 0 : query.hashCode());
	result = prime * result + sequence;
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	CacheKey other = (CacheKey) obj;
	if (concept == null) {
		if (other.concept != null)
			return false;
	} else if (!concept.equals(other.concept))
		return false;
	if (dataset == null) {
		if (other.dataset != null)
			return false;
	} else if (!dataset.equals(other.dataset))
		return false;
	if (query == null) {
		if (other.query != null)
			return false;
	} else if (!query.equals(other.query))
		return false;
	if (sequence != other.sequence)
		return false;
	return true;
}
 
 
 
 
 }
