package org.apache.sling.servlets.semantic.rdf.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.servlets.semantic.rdf.model.CollectionResource;
import org.apache.sling.servlets.semantic.rdf.model.CollectionTypes;
import org.apache.sling.servlets.semantic.rdf.model.RDFResource;


public class CollectionResourceBuilder {

	private CollectionTypes type;
	
	private final List<RDFResource> resources;
	
	public CollectionResourceBuilder( CollectionTypes type ) {
		this.type = type;
		
		this.resources = new ArrayList<RDFResource>();
	}
	
	/**
	 * Adds a resource to the resources set.  As specified in the RDF spec, a collection 
	 * makes no assumption about the uniqueness of a resource.  That said, a resource can 
	 * be added multiple times if so desired.
	 * 
	 * @param r
	 */
	public void addResource( RDFResource r ) {
		this.resources.add( r );
	}

	public CollectionResource build() {
		
		switch( this.type ) {
		case seq: 
			return new SeqCollectionResource( this.resources );
		case alt:
			return null;
		case bag: 
			return null;
		}
		
		return null;
		
	}
}
