package org.apache.sling.servlets.semantic.rdf.model;

public interface ResourceReference extends RDFResource {

	public String getUri();
	
	/**
	 * 
	 * @return True if the URI used is a qualified URI, false otherwise
	 */
	public Boolean getIsQualified();
	
}
