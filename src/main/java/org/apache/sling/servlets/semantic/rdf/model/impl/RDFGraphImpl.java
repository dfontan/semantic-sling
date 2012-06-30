package org.apache.sling.servlets.semantic.rdf.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.sling.servlets.semantic.rdf.model.RDFGraph;
import org.apache.sling.servlets.semantic.rdf.model.Triple;


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
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Set<Triple> getTriples() {
		return this.graph;
	}

	@Override
	public void addTriple(Triple t) {
		this.graph.add( t );
	}

	@Override
	public void removeTriple(Triple t) {
		this.graph.remove( t );
	}

	@Override
	public Boolean hasTriple(Triple t) {
		return this.graph.contains( t );
	}

	@Override
	public String getBaseUri() {
		return this.baseUri;
	}

	@Override
	public void addAllTriples(Set<Triple> t) {
		this.graph.addAll( t );		
	}

	@Override
	public void addNamespace(String prefix, String uri) {
		this.namespaces.put( prefix, uri );
		
	}

	@Override
	public void removeNamespaceByPrefix(String prefix) {
		this.namespaces.remove(prefix);
		
	}

	@Override
	public void removeNamespaceByUri(String uri) {
		for( Entry<String,String> e : this.namespaces.entrySet() ) {
			if( e.getValue().equals( uri ) ) {
				this.namespaces.remove( e.getKey() );
				return;
			}
		}
	}

	@Override
	public Boolean hasNamespacePrefix(String prefix) {
		return this.namespaces.containsKey( prefix );
	}

	@Override
	public Boolean hasNamespaceUri(String uri) {
		return this.namespaces.containsValue( uri );
	}

	@Override
	public Boolean hasNamespaces() {
		return !this.namespaces.isEmpty();
	}

	@Override
	public Map<String, String> getNamespaces() {
		return this.namespaces;
	}

	@Override
	public String getNamespaceUri(String prefix) {
		return this.namespaces.get( prefix );
	}

	@Override
	public String getNamespacePrefix(String Uri) {
		for( Entry<String,String> e : this.namespaces.entrySet() ) {
			if( e.getValue().equals( Uri ) ) {
				return e.getKey();
			}
		}
		
		return null;
	}

}
