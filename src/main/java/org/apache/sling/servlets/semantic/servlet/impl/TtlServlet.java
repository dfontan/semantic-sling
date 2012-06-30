package org.apache.sling.servlets.semantic.servlet.impl;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.semantic.rdf.writer.RdfResourceWriter;
import org.apache.sling.servlets.semantic.rdf.writer.impl.InvalidObjectException;
import org.apache.sling.servlets.semantic.rdf.writer.impl.TtlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Serve repository content in <a href="http://www.w3.org/TeamSubmission/turtle/">turtle</a> format. 
 * This servlet is configured to listen to any resource GET request with the extension ".ttl".
 * 
 * @author paulmichelotti
 *
 */
@SlingServlet(
		extensions = TtlServlet.EXTENSIONS,
		resourceTypes = TtlServlet.TYPES, 
		methods = {"GET"})
@Properties(value = {
    @Property(name = "service.description", value = "Serves a Resource in TTL format")
})
public class TtlServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger( TtlServlet.class );
	
	public static final String EXTENSIONS = "ttl";
	public static final String TYPES = "sling/servlet/default";

	@Override
	protected void doGet(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
		
    	RdfResourceWriter writer = new TtlWriter();
    	
    	try {
			writer.write( request.getResource(), request, response );
		} catch (RepositoryException e) {
			log.error( "RepositoryException encountered in handling servlet", e );
			response.sendError(500);
		} catch (InvalidObjectException e) {
			log.error( "Invalid object found in writing Graph", e );
			response.sendError(500);
		}
    	
    }
}
