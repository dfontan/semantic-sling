package org.apache.sling.servlets.semantic.rdf.model.impl;

import java.util.List;

import org.apache.sling.servlets.semantic.rdf.model.CollectionResource;
import org.apache.sling.servlets.semantic.rdf.model.CollectionTypes;
import org.apache.sling.servlets.semantic.rdf.model.RDFResource;


public class SeqCollectionResource implements CollectionResource {

	private final List<RDFResource> collection;
	
	public SeqCollectionResource( List<RDFResource> resources ) {
		this.collection = resources;
	}
	
	@Override
	public List<RDFResource> getCollection() {
		return this.collection;
	}

	@Override
	public CollectionTypes getCollectionType() {
		return CollectionTypes.seq;
	}

	@Override
	public Boolean isBag() {
		return false;
	}

	@Override
	public Boolean isSeq() {
		 return true;
	}

	@Override
	public Boolean isAlt() {
		return false;
	}

}
