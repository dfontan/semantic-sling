package com.citytechinc.sling.semantic.servlets.rdf.model.impl;

import com.citytechinc.sling.semantic.servlets.rdf.model.RDFResource;
import com.citytechinc.sling.semantic.servlets.rdf.model.ResourceReference;
import com.citytechinc.sling.semantic.servlets.rdf.model.Triple;

public class TripleImpl implements Triple {

	private final ResourceReference subject;
	private final ResourceReference predicate;
	private final RDFResource object;
	
	public TripleImpl( ResourceReference subject, ResourceReference predicate, RDFResource object ) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	
	public ResourceReference getSubject() {
		return this.subject;
	}
	
	public ResourceReference getPredicate() {
		return this.predicate;
	}
	
	public RDFResource getObject() {
		return this.object;
	}
	
	public int hashCode() {
		return ( "TripleImpl " + Integer.toString(this.subject.hashCode()) + Integer.toString(this.predicate.hashCode()) + Integer.toString(this.object.hashCode()) ).hashCode();
	}
}
