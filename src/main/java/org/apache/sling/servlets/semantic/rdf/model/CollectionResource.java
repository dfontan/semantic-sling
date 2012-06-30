package org.apache.sling.servlets.semantic.rdf.model;

import java.util.List;

public interface CollectionResource extends RDFResource {

	public List<RDFResource> getCollection();
	
	public CollectionTypes getCollectionType();
	
	public Boolean isBag();
	
	public Boolean isSeq();
	
	public Boolean isAlt();
}
