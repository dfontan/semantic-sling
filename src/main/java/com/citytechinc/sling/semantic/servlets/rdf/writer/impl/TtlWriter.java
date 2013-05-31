package com.citytechinc.sling.semantic.servlets.rdf.writer.impl;

import java.io.IOException;
import java.io.Writer;
import java.text.Format;
import java.text.SimpleDateFormat;

import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;

import com.citytechinc.sling.semantic.servlets.rdf.model.CollectionResource;
import com.citytechinc.sling.semantic.servlets.rdf.model.LiteralResource;
import com.citytechinc.sling.semantic.servlets.rdf.model.RDFGraph;
import com.citytechinc.sling.semantic.servlets.rdf.model.RDFResource;
import com.citytechinc.sling.semantic.servlets.rdf.model.ResourceReference;
import com.citytechinc.sling.semantic.servlets.rdf.model.Triple;



/**
 * A writer outputting <a href="http://www.w3.org/TeamSubmission/turtle/">turtle</a> triple format.
 * Output of resource properties based on property types and availability of linked resources. As such,
 * the value of this writer increases based on the quality of data modeling done in the repository.
 *
 * @author paulmichelotti
 *
 */
public class TtlWriter extends AbstractResourceWriter {

	public static final String CONTENT_TYPE = "text/turtle";
	public static final String CHARACTER_ENCODING = "UTF-8";
	public static final String EXTENSIONS = "ttl";

	private final Format dateFormatter;
	private final NameFactory nameFactory;

	public TtlWriter() {

		this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		this.nameFactory = NameFactoryImpl.getInstance();
	}

	/**
	 * Write a Turtle representation of a provided RDFGraph to a Writer.
	 *
	 * @param w
	 * @param g
	 * @throws IOException
	 * @throws InvalidObjectException
	 */
	protected void writeGraph( Writer w, RDFGraph g ) throws IOException, InvalidObjectException {

		/*
		 * Write prefixed namespaces
		 */
		w.write( "@prefix : <" + g.getBaseUri() + "> . \n" );

		for( String curNs : g.getNamespaces().keySet() ) {
			w.write( "@prefix " + curNs + ": <" + g.getNamespaceUri( curNs ) + "> . \n" );
		}

		w.write( "\n" );

		/*
		 * Establish the base
		 */
		w.write( "@base <" + g.getBaseUri() + "> . \n \n" );

		for( Triple curTriple : g.getTriples() ) {
			String subject;
			String predicate;
			String object;

			subject = this.writeResourceReference( curTriple.getSubject() );
			predicate = this.writeResourceReference( curTriple.getPredicate() );
			object = this.writeRDFResource( curTriple.getObject() );

			w.write( subject + " " + predicate + " " + object + " . \n" );
		}
	}

	private String writeRDFResource( RDFResource r ) throws InvalidObjectException {
		if ( r instanceof LiteralResource ) {
			LiteralResource curLiteralObject = (LiteralResource) r;
			return "\"" + curLiteralObject.getValue() + "\"^^xs:" + curLiteralObject.getType();
		}
		else if ( r instanceof ResourceReference ) {
			ResourceReference curResourceObject = (ResourceReference) r;
			return this.writeResourceReference( curResourceObject );
		}
		else if ( r instanceof CollectionResource ) {
			CollectionResource curCollectionResource = (CollectionResource) r;
			return this.writeCollectionResource( curCollectionResource );
		}
		else {
			throw new InvalidObjectException( "An object which was not of type ResourceReference or LiteralResource was encountered" );
		}
	}

	private String writeResourceReference( ResourceReference r ) {
		if ( r.getIsQualified() ) {
			return r.getUri();
		}
		return "<" + r.getUri() + ">";
	}

	private String writeCollectionResource( CollectionResource r ) throws InvalidObjectException {
		StringBuffer returnBuffer = new StringBuffer();

		returnBuffer.append( "[" );

		if ( r.isBag() ) {
			returnBuffer.append( " a rdf:Bag; " );
		}
		else if( r.isAlt() ) {
			returnBuffer.append( " a rdf:Alt; " );
		}
		else if( r.isSeq() ) {
			returnBuffer.append( " a rdf:Seq; " );
		}

		for( int i=0; i<r.getCollection().size(); i++ ) {
			returnBuffer.append( "rdf:_" );
			returnBuffer.append( i+1 );
			returnBuffer.append( " " );
			returnBuffer.append( this.writeRDFResource( r.getCollection().get(i) ) );
			returnBuffer.append( "; " );
		}

		returnBuffer.append( "]" );

		return returnBuffer.toString();
	}


	@Override
	protected String getContentType() {
		return CONTENT_TYPE;
	}


	@Override
	protected String getCharacterEncoding() {
		return CHARACTER_ENCODING;
	}


	@Override
	protected Format getDateFormatter() {
		return this.dateFormatter;
	}


	@Override
	protected NameFactory getNameFactory() {
		return this.nameFactory;
	}

}
