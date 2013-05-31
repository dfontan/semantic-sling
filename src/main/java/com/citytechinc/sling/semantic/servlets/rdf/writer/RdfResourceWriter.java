package com.citytechinc.sling.semantic.servlets.rdf.writer;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.Resource;

import com.citytechinc.sling.semantic.servlets.rdf.writer.impl.InvalidObjectException;


public interface RdfResourceWriter {

	/**
	 * Write a definition of the resource to the output stream provided by response.  Such a method should only 
	 * write the definition of the provided resource and should not attempt to write nested resources.  
	 * 
	 * @param r
	 * @param response
	 * @throws InvalidObjectException 
	 */
	public void write( Resource r, HttpServletRequest request, HttpServletResponse response ) throws RepositoryException, IOException, InvalidObjectException;
	
}
