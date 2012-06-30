package org.apache.sling.servlets.semantic.rdf.model;

import java.util.Map;
import java.util.Set;

public interface RDFGraph {

	public String getName();
	
	public String getBaseUri();
	
	public Set<Triple> getTriples();
	
	public void addTriple( Triple t );
	
	public void addAllTriples( Set<Triple> t );
	
	public void removeTriple( Triple t );
	
	public Boolean hasTriple( Triple t );
	
	public void addNamespace( String prefix, String uri );
	
	public void removeNamespaceByPrefix( String prefix );
	
	public void removeNamespaceByUri( String uri );
	
	public Boolean hasNamespacePrefix( String prefix );
	
	public Boolean hasNamespaceUri( String uri );
	
	public Boolean hasNamespaces();
	
	public Map<String,String> getNamespaces();
	
	public String getNamespaceUri( String prefix );
	
	public String getNamespacePrefix( String Uri );
}
