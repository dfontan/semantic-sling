package com.citytechinc.sling.semantic.servlets.rdf.model.impl;

import com.citytechinc.sling.semantic.servlets.rdf.model.ResourceReference;

public class ResourceReferenceImpl implements ResourceReference {

	private final String uri;
	private final Boolean isQualified;
	
	public ResourceReferenceImpl( String uri ) {
		this.uri = uri;
		this.isQualified = false;
	}
	
	/**
	 * 
	 * TODO: I'm not particularly thrilled by having to explicitly set isQualified. 
	 *       I would like the ( String uri ) constructor to inspect the provided 
	 *       uri in order to determine whether it is qualified.
	 *       
	 * @param uri
	 * @param isQualified
	 */
	public ResourceReferenceImpl( String uri, Boolean isQualified ) {
		this.uri = uri;
		this.isQualified = isQualified;
	}
	
	public String getUri() {
		return this.uri;
	}
	
	public String toString() {
		return this.uri;
	}
	
	public int hashCode() {
		return ("RDFResource " + this.uri).hashCode();
	}
	
	public Boolean getIsQualified() { 
		return this.isQualified;
	}
}
