package com.citytechinc.sling.semantic.servlets.rdf.model.impl;

import java.util.List;

import com.citytechinc.sling.semantic.servlets.rdf.model.CollectionResource;
import com.citytechinc.sling.semantic.servlets.rdf.model.CollectionTypes;
import com.citytechinc.sling.semantic.servlets.rdf.model.RDFResource;


public class SeqCollectionResource implements CollectionResource {

	private final List<RDFResource> collection;

	public SeqCollectionResource( List<RDFResource> resources ) {
		this.collection = resources;
	}

	public List<RDFResource> getCollection() {
		return this.collection;
	}

	public CollectionTypes getCollectionType() {
		return CollectionTypes.seq;
	}

	public Boolean isBag() {
		return false;
	}

	public Boolean isSeq() {
		 return true;
	}

	public Boolean isAlt() {
		return false;
	}

}
