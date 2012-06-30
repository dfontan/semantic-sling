package org.apache.sling.servlets.semantic.rdf.model;

public interface Triple {

	public ResourceReference getSubject();
	
	public ResourceReference getPredicate();
	
	public RDFResource getObject();
	
}
