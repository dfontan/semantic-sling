package org.apache.sling.servlets.semantic.rdf.writer.impl;

import java.io.IOException;
import java.io.Writer;
import java.text.Format;
import java.text.SimpleDateFormat;

import javax.jcr.ItemNotFoundException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.commons.conversion.NameParser;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.namespace.SessionNamespaceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlets.semantic.rdf.model.CollectionResource;
import org.apache.sling.servlets.semantic.rdf.model.CollectionTypes;
import org.apache.sling.servlets.semantic.rdf.model.LiteralResource;
import org.apache.sling.servlets.semantic.rdf.model.RDFGraph;
import org.apache.sling.servlets.semantic.rdf.model.RDFResource;
import org.apache.sling.servlets.semantic.rdf.model.ResourceReference;
import org.apache.sling.servlets.semantic.rdf.model.Triple;
import org.apache.sling.servlets.semantic.rdf.model.impl.CollectionResourceBuilder;
import org.apache.sling.servlets.semantic.rdf.model.impl.LiteralResourceImpl;
import org.apache.sling.servlets.semantic.rdf.model.impl.RDFGraphImpl;
import org.apache.sling.servlets.semantic.rdf.model.impl.ResourceReferenceImpl;
import org.apache.sling.servlets.semantic.rdf.model.impl.TripleImpl;
import org.apache.sling.servlets.semantic.rdf.writer.RdfResourceWriter;



/**
 * A writer outputting <a href="http://www.w3.org/TeamSubmission/turtle/">turtle</a> triple format.  
 * Output of resource properties based on property types and availability of linked resources. As such, 
 * the value of this writer increases based on the quality of data modeling done in the repository. 
 * 
 * @author paulmichelotti
 *
 */
public class TtlWriter implements RdfResourceWriter {

	public static final String CONTENT_TYPE = "text/turtle";
	public static final String CHARACTER_ENCODING = "UTF-8";
	public static final String EXTENSIONS = "ttl";
	
	public static final String XML_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema";
	public static final String RDF_SCHEMA_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
	private final Format dateFormatter;
	
	private final NameFactory nameFactory;
	
	public TtlWriter() {
		
		this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		this.nameFactory = NameFactoryImpl.getInstance();
	}
	
	@Override
	public void write(Resource r, HttpServletRequest request, HttpServletResponse response) throws RepositoryException, IOException, InvalidObjectException {
		
		response.setContentType( CONTENT_TYPE );
		response.setCharacterEncoding( CHARACTER_ENCODING ); 
		
		Node node = r.adaptTo( Node.class );
		
		PropertyIterator propertyIterator = node.getProperties();
		
		Session curSession = r.getResourceResolver().adaptTo( Session.class );
		
		String base = this.constructBaseUri( request );
		
		RDFGraph graph = new RDFGraphImpl( base );

		/*
		 * TODO: Currently all namespaces are output.  It would be ideal to only output namespaces necessary to represent
		 *       the document at hand.
		 */
		for ( String prefix : curSession.getNamespacePrefixes() ) {
			/*
			 * "rep" is an internal namespace Jackrabbit uses which needs to be ignored when outputting the set of namespaces
			 */
			if ( ! StringUtils.isEmpty( prefix ) && ! prefix.equals( "rep" ) ) {
				graph.addNamespace( prefix, this.writePrefixUri( curSession.getNamespaceURI( prefix ) ) );
			}
		}
		
		/*
		 * The XML Schema namespace is included as a default in Jackrabbit.  I add the following statement 
		 * to catch problems in the event that this is not the case.
		 */
		try {
			curSession.getNamespacePrefix( XML_SCHEMA_NS );
		}
		catch ( NamespaceException e ) {
			graph.addNamespace( "xs", XML_SCHEMA_NS + "#" );
		}
		
		/*
		 * Check to make sure RDF was included as a namespace
		 */
		try { 
			curSession.getNamespacePrefix( RDF_SCHEMA_NS );
		}
		catch ( NamespaceException e ) {
			graph.addNamespace( "rdf", RDF_SCHEMA_NS );
		}
		
		/*
		 * Iterate through all properties adding triples to the graph for each
		 */
		while( propertyIterator.hasNext() ) {
			Property curProperty = propertyIterator.nextProperty();
			graph.addTriple( this.createSingleTriple(r, curProperty, base ));
		}
		
		/*
		 * jcr:content is a child node treated herein as a property.  When this child exists it is assumed that it holds the content of the parent 
		 * node and as such needs to be treated separate from other child nodes.
		 * 
		 * TODO: Review if this is the most appropriate way to handle the jcr:content node
		 */
		if ( node.hasNode("jcr:content") ) {
			graph.addTriple( 
					new TripleImpl(
							new ResourceReferenceImpl( this.encode( r.getPath() ) ), 
							new ResourceReferenceImpl( "jcr:content", true ), 
							new ResourceReferenceImpl( base + this.encode( r.getPath() ) + "/jcr%3Acontent" )
							));
		}
		
		this.writeGraph( response.getWriter(), graph );
		
	}
	
	/**
	 * Write a Turtle representation of a provided RDFGraph to a Writer.
	 * 
	 * @param w
	 * @param g
	 * @throws IOException
	 * @throws InvalidObjectException
	 */
	private void writeGraph( Writer w, RDFGraph g ) throws IOException, InvalidObjectException {
		
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
	
	/**
	 * <p>
	 * Creates a single triple object building up the Subject, Predicate, and Object.  Object types are determined 
	 * based on the type of the property values as defined in the Repository.
	 * </p>
	 * <p>
	 * <h2>Subject</h2>
	 * The Subject will always be the URI of the resource which is being processed.  This URI will be rendered relative to the base which is the 
	 * server on which Sling is running
	 * </p>
	 * <p>
	 * <h2>Predicate</h2>
	 * The predicate is the name of the property being rendered.  When the property name is qualified it will be taken as is under the presumption 
	 * that the qualifying namespace was included as a prefix in the ttl file.  When the property name is not qualified, a path to the property definition 
	 * will be used.  This path will be rendered relative to the base.
	 * </p>
	 * <p>
	 * <h2>Object</h2>
	 * How the object is defined will be based on the type of the value.  There are 12 value types supported by this function (listed below).  Any other value type 
	 * (such as UNDEFINED) is defined as though the property value is a String. Refer to http://www.day.com/specs/jcr/2.0/3_Repository_Model.html#3.6.1 Property Types 
	 * for further explanation of each type and it's usage in JCR.
	 * </p>
	 * <p>
	 * <h3>Property Types</h3>
	 * <ul>
	 * <li>STRING</li>
	 * <li>BOOLEAN</li>
	 * <li>DECIMAL</li>
	 * <li>DOUBLE</li>
	 * <li>LONG</li>
	 * <li>DATE</li>
	 * <li>URI</li>
	 * <li>PATH</li>
	 * <li>NAME</li>
	 * <li>REFERENCE</li>
	 * <li>WEAKREFERENCE</li>
	 * <li>BINARY</li>
	 * </ul> 
	 * 
	 * @param r The resource which is the Subject of the Triple
	 * @param p The property which serves as the Predicate of the Triple
	 * @param base URI base for reference type properties
	 * @param v The object standing as the Value of the Triple
	 * @return A string representation of the constructed Triple
	 * @throws RepositoryException
	 * @throws IOException 
	 */
	private Triple createSingleTriple( Resource r, Property p, String base ) throws RepositoryException, IOException {
		
		ResourceReference subject = new ResourceReferenceImpl( this.encode( r.getPath() ) );
		ResourceReference predicate;
		
		if( p.getName().contains( ":" ) ) {
			predicate = new ResourceReferenceImpl( p.getName(), true );
		}
		else {
			predicate = new ResourceReferenceImpl( this.encode( p.getPath() ) );
		}
		
		/*
		 * Build the object based on the cardinality of the property
		 */
		if ( p.isMultiple() ) {
			/*
			 * per the JCR Spec (http://www.day.com/specs/jcr/2.0/3_Repository_Model.html#3.6.3 Single and Multi-Value Properties) 
			 * multi valued properties are ordered so we write them out as a SEQ collection
			 */
			CollectionResourceBuilder collectionBuilder = new CollectionResourceBuilder( CollectionTypes.seq );
			
			for ( Value curValue : p.getValues() ) {
				collectionBuilder.addResource( this.buildObject(p, curValue, r.getResourceResolver().adaptTo( Session.class ), base));
			}
			
			return new TripleImpl( subject, predicate, collectionBuilder.build() );
			
		}
		
		RDFResource object = this.buildObject(p, p.getValue(), r.getResourceResolver().adaptTo( Session.class ), base);
		
		return new TripleImpl( subject, predicate, object );
		
	}
	
	private RDFResource buildObject( Property p, Value v, Session session, String base ) throws ValueFormatException, IllegalStateException, RepositoryException, IOException {
		switch ( p.getType() ) {
		
		case PropertyType.STRING :
			return new LiteralResourceImpl( v.getString(), "string" );
		case PropertyType.BOOLEAN : 
			return new LiteralResourceImpl( new Boolean( v.getBoolean() ).toString(), "boolean" );
		case PropertyType.DECIMAL : 
			return new LiteralResourceImpl( v.getDecimal().toString(), "decimal" );
		case PropertyType.DOUBLE : 
			return new LiteralResourceImpl( Double.toString( v.getDouble() ), "double" );
		case PropertyType.LONG : 
			return new LiteralResourceImpl( Long.toString( v.getLong() ).toString(), "long" );
		case PropertyType.DATE :
			return new LiteralResourceImpl( this.dateFormatter.format( v.getDate().getTime() ), "dateTime" );
		case PropertyType.URI : 
			return new ResourceReferenceImpl( v.getString() );
		case PropertyType.PATH : 
			/*
			 * A PATH should point to an existing NODE.  In the case that it does not, we write out the path as a relative URI
			 */
			try {
				return new ResourceReferenceImpl( base + this.encode( p.getNode().getPath() ) );
			}
			catch ( ItemNotFoundException e ) {
				return new ResourceReferenceImpl( v.getString() );
			}
		case PropertyType.NAME :
			/*
			 * See http://www.day.com/specs/jcr/2.0/3_Repository_Model.html#3.1.3 Names for the specifics on the 
			 * construction of JCR Names and their usage.  
			 */
			Name nameProperty = NameParser.parse( v.getString(), new SessionNamespaceResolver( session ), this.nameFactory );
			
			if( !StringUtils.isEmpty(nameProperty.getNamespaceURI())) {
				return new ResourceReferenceImpl( this.writePrefixUri( nameProperty.getNamespaceURI() ) + nameProperty.getLocalName() );
			}
			return new ResourceReferenceImpl( nameProperty.getLocalName() );
			
		case PropertyType.REFERENCE : 
			/*
			 * A REFERENCE should point to an existing NODE.  Cases where it does not should be rare since a REFERENCE supports referential integrity 
			 * however, if it does not, the reference UUID is rendered as a string.
			 */
			try {
				return new ResourceReferenceImpl( base + this.encode( p.getNode().getPath() ) );
			}
			catch ( ItemNotFoundException e ) {
				return new LiteralResourceImpl( v.getString(), "string" );
			}
		case PropertyType.WEAKREFERENCE : 
			/*
			 * A WEEKREFERENCE should point to an existing NODE.  In the case that it does not, we write out the reference UUID as a string.
			 */
			try {
				return new ResourceReferenceImpl( base + this.encode( p.getNode().getPath() ) );
			}
			catch ( ItemNotFoundException e ) {
				return new LiteralResourceImpl( v.getString(), "string" );
			}
		case PropertyType.BINARY : 
			return new LiteralResourceImpl( Base64.encodeBase64String( IOUtils.toByteArray( v.getBinary().getStream() ) ), "base64Binary" );  
		default :
			return new LiteralResourceImpl( v.getString(), "string" );
		
		}
	}
	
	/**
	 * <p>
	 * The default namespaces stored in Jackrabbit do not end with a "/" or a "#".  As such, making a statement 
	 * such as sling:resourceType results in a rendering of <code>http://sling.apache.org/jcr/sling/1.0resourceType</code> which 
	 * is not ... good.  What we want is <code>http://sling.apache.org/jcr/sling/1.0/resourceType</code>.
	 * </p>
	 * <p>
	 * Here we have to make assumptions then about how the namespace URI should end.  The XML Schema namespace, which is a
	 * default in JCR, needs to end with "#".  Other than that, the assumption made is that all other namespaces which do not 
	 * have a proper ending character have "/" appended to them.  
	 * </p>
	 * <p>
	 * As you add your own namespaces, be sure to end them appropriately!
	 * </p>
	 * 
	 * @param uri
	 * @return
	 */
	private String writePrefixUri( String uri ) {
		if ( ! uri.endsWith( "/" ) && ! uri.endsWith( "#" ) ) {
			if ( uri.equals( XML_SCHEMA_NS ) ) {
				return uri + "#";
			}
			return uri + "/";
		}
		return uri;
	}
	
	/**
	 * Builds the base URI which will be used for all names which are not defined to be within a namespace already
	 * 
	 * TODO: Construction of the base URI needs further review and thought, specifically the default port handling. 
	 *       I've build this for my specific use case but this may not cover all use cases and/or server configurations
	 * 
	 * @param request
	 * @return The portion of the request URI prior to the resource path
	 */
	private String constructBaseUri( HttpServletRequest request ) {
		
		
		String uriWithoutPort = request.getScheme() + "://" + request.getServerName();
		
		String uri;
		
		if ( request.getServerPort() != 80 ) {
			uri = uriWithoutPort + ":" + request.getServerPort();
		}
		else {
			uri = uriWithoutPort;
		}
		
		return uri;
		
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
	
	private String encode( String path ) {
		
		return path.replace( ":", "%3A" );
	}

}
