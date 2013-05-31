package com.citytechinc.sling.semantic.servlets.rdf.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.citytechinc.sling.semantic.servlets.rdf.model.RDFGraph;
import com.citytechinc.sling.semantic.servlets.rdf.model.Triple;


public class RDFGraphImpl implements RDFGraph {

	private final Set<Triple> graph;
	private final String name;
	private final String baseUri;
	private final Map<String,String> namespaces;

	public RDFGraphImpl() {

		this.graph = new HashSet<Triple>();
		this.name = UUID.randomUUID().toString();
		this.baseUri = "";
		this.namespaces = new HashMap<String,String>();

	}

	public RDFGraphImpl( String baseUri ) {

		this.graph = new HashSet<Triple>();
		this.name = UUID.randomUUID().toString();
		this.baseUri = baseUri;
		this.namespaces = new HashMap<String,String>();

	}

	public RDFGraphImpl( String baseUri, String name ) {

		this.graph = new HashSet<Triple>();
		this.name = name;
		this.baseUri = baseUri;
		this.namespaces = new HashMap<String,String>();

	}

	public String getName() {
		return this.name;
	}

	public Set<Triple> getTriples() {
		return this.graph;
	}

	public void addTriple(Triple t) {
		this.graph.add( t );
	}

	public void removeTriple(Triple t) {
		this.graph.remove( t );
	}

	public Boolean hasTriple(Triple t) {
		return this.graph.contains( t );
	}

	public String getBaseUri() {
		return this.baseUri;
	}

	public void addAllTriples(Set<Triple> t) {
		this.graph.addAll( t );
	}

	public void addNamespace(String prefix, String uri) {
		this.namespaces.put( prefix, uri );

	}

	public void removeNamespaceByPrefix(String prefix) {
		this.namespaces.remove(prefix);

	}

	public void removeNamespaceByUri(String uri) {
		for( Entry<String,String> e : this.namespaces.entrySet() ) {
			if( e.getValue().equals( uri ) ) {
				this.namespaces.remove( e.getKey() );
				return;
			}
		}
	}

	public Boolean hasNamespacePrefix(String prefix) {
		return this.namespaces.containsKey( prefix );
	}

	public Boolean hasNamespaceUri(String uri) {
		return this.namespaces.containsValue( uri );
	}

	public Boolean hasNamespaces() {
		return !this.namespaces.isEmpty();
	}

	public Map<String, String> getNamespaces() {
		return this.namespaces;
	}

	public String getNamespaceUri(String prefix) {
		return this.namespaces.get( prefix );
	}

	public String getNamespacePrefix(String Uri) {
		for( Entry<String,String> e : this.namespaces.entrySet() ) {
			if( e.getValue().equals( Uri ) ) {
				return e.getKey();
			}
		}

		return null;
	}

}
