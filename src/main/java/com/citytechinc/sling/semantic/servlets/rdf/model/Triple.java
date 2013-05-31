package com.citytechinc.sling.semantic.servlets.rdf.model;

public interface Triple {

	public ResourceReference getSubject();
	
	public ResourceReference getPredicate();
	
	public RDFResource getObject();
	
}
