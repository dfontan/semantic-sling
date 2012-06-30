package org.apache.sling.servlets.semantic.rdf.model.impl;

import org.apache.sling.servlets.semantic.rdf.model.LiteralResource;

public class LiteralResourceImpl implements LiteralResource {

	private final String value;
	private final String type;
	
	public LiteralResourceImpl( String value, String type ) {
		this.value = value;
		this.type = type;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getType() { 
		return this.type;
	}
	
	public String toString() { 
		return this.value;
	}
	
	public int hashCode() {
		return ("LiteralResource value:" + this.value + " type:" + this.type).hashCode();
	}
}
