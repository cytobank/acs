/*
 *  This file is part the Cytobank ACS Library.
 *  Copyright (C) 2010 Cytobank, Inc.  All rights reserved.
 *
 *  The Cytobank ACS Library program is free software: 
 *  you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cytobank.acs.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.cytobank.acs.core.exceptions.DuplicateFileResourceIdentifierException;
import org.cytobank.acs.core.exceptions.InvalidAssociationException;
import org.cytobank.acs.core.exceptions.InvalidFileResourceUriSchemeException;
import org.cytobank.acs.core.exceptions.InvalidIndexException;
import org.cytobank.acs.util.FileUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents a file resource stored within an ACS Container.
 * 
 * @author Chad Rosenberg <chad@cytobank.org>
 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
 */
public class FileResourceIdentifier extends AdditionalInfoElementWrapper {
	/**
	 * A scheme representing a Uniform Resource Names (URN).
	 * <p>
	 * According to the ACS specification <i>Uniform Resource Names (URNs) may only be used for files that are not required to interpret the contents of the ACS container.
	 * For example, the URN urn:issn:1552-4957 may be used to reference a publication related to the contents of the container.</i>
	 * 
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public static final String URN_SCHEME = "urn";
	
	/** 
	 * A <code>String</code> array of the allowed URI schemes as defined in the ACS specification. 
	 * 
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public static final String[] ALLOWED_URI_SCHEMES = new String[]{"file", "http", "https", "ftp", URN_SCHEME};

	/**  
	 * The (optional) <code>InputStream</code> pointing to the represented file.  If this is set to null, it is assumed
	 * either that the represented file exists in a previous ACS container, or is an external URL resource which means it won't live
	 * in the ACS container to be written to.
	 */ 
	protected InputStream sourceFileStream;
	
	/**  
	 * The (optional) <code>File</code> pointing to the represented file.  If this is set to null, it is assumed
	 * either that the represented file exists in a previous ACS container, or is an external URL resource which means it won't live
	 * in the ACS container to be written to.
	 */ 
	protected File sourceFile;
	
	/**
	 * Keeps track if the {@link FileResourceIdentifier#sourceInputStreamReadFrom} has been read from.
	 */
	protected boolean sourceInputStreamReadFrom = false;
	
	/** The <code>TableOfContents</code> instance this <code>FileResourceIdentifier</code> is owned by. */ 
	protected TableOfContents tableOfContents;
			
	/** The list of <code>Association</code>s that this <code>FileResourceIdentifier</code> has. */
	protected Vector<Association> associations = new Vector<Association>();

	
	/**
	 * Creates a new <code>FileResourceIdentifier</code> instance from an <code>InputStream</code>.  The
	 * <code>sourceInputStream</code> parameter will not be used until {@link FileResourceIdentifier#writeRepresentedFile} is called, which happens on
	 * a {@link ACS#writeAcsContainer} call.  The <code>sourceInputStream</code> parameter will remain open until {@link ACS#writeAcsContainer}
	 * or {@link FileResourceIdentifier#close} is called.
	 * <p>
	 * NOTE: The preferred method to create a <code>FileResourceIdentifier</code> is to use the 
	 * {@link TableOfContents#createFileResourceIdentifier} method on an existing <code>TableOfContents</code>.
	 * <p>
	 * This constructor sets <code>this.sourceInputStream</code> to the specified <code>sourceInputStream</code>
	 * and will cause the <code>hasSourceInputStream()</code> method to return <code>true</code>, which will indicate
	 * that when this new instance of <code>FileResourceIdentifier</code> is being written to a new ACS container that
	 * the file this <code>FileResourceIdentifier</code> represents will have to be pulled in from the <code>sourceInputStream</code>
	 * as opposed to existing in a previous ACS container, in which it is copied, or from an external URL resource which means it won't live
	 * in the ACS container to be written to.
	 * 
	 * @param tableOfContents the <code>TableOfContents</code> instance that the created <code>FileResourceIdentifier</code> will be owned by 
	 * @param sourceFileStream the <code>InputStream</code> pointing to the represented file of the created <code>FileResourceIdentifier</code>
	 * @throws InvalidAssociationException if there is an invalid association exception
	 * @see TableOfContents#createFileResourceIdentifier
	 * @see FileResourceIdentifier#hasSourceInputStream
	 */
	public FileResourceIdentifier(TableOfContents tableOfContents, InputStream sourceFileStream) throws InvalidAssociationException {
		this(tableOfContents, tableOfContents.createElement(Constants.FILE_ELEMENT));
		this.sourceFileStream = sourceFileStream;
	}
	

	/**
	 * Creates a new <code>FileResourceIdentifier</code> instance from a <code>File</code>.
	 * <p>
	 * NOTE: The preferred method to create a <code>FileResourceIdentifier</code> is to use the 
	 * {@link TableOfContents#createFileResourceIdentifier} method on an existing <code>TableOfContents</code>.
	 * <p>
	 * This constructor sets <code>this.sourceFile</code> to the specified <code>File</code>
	 * and will cause the <code>hasSourceFile()</code> method to return <code>true</code>, which will indicate
	 * that when this new instance of <code>FileResourceIdentifier</code> is being written to a new ACS container that
	 * the file this <code>FileResourceIdentifier</code> represents will have to be pulled in from the <code>sourceFile</code>
	 * as opposed to existing in a previous ACS container, in which it is copied, or from an external URL resource which means it won't live
	 * in the ACS container to be written to.
	 * 
	 * @param tableOfContents the <code>TableOfContents</code> instance that the created <code>FileResourceIdentifier</code> will be owned by 
	 * @param sourceFile the <code>File</code> pointing to the represented file of the created <code>FileResourceIdentifier</code>
	 * @throws InvalidAssociationException if there is an invalid association exception
	 * @see TableOfContents#createFileResourceIdentifier
	 * @see FileResourceIdentifier#hasSourceFile
	 */
	public FileResourceIdentifier(TableOfContents tableOfContents, File sourceFile) throws InvalidAssociationException {
		this(tableOfContents, tableOfContents.createElement(Constants.FILE_ELEMENT));
		this.sourceFile = sourceFile;
	}
	
	
	/**
	 * Creates a new or existing <code>FileResourceIdentifier</code> instance from an xml <code>org.w3c.dom.Element</code>.
	 * <p>
	 * NOTE: The preferred method to create a <code>FileResourceIdentifier</code> is to use the 
	 * {@link TableOfContents#createFileResourceIdentifier} method on an existing <code>TableOfContents</code>.
	 * 
	 * @param tableOfContents tableOfContents the <code>TableOfContents</code> instance the the created <code>FileResourceIdentifier</code> will be owned by
	 * @param fileResourceIdentifierElement the xml <code>org.w3c.dom.Element</code> that represents this <code>FileResourceIdentifier</code>
	 * @throws InvalidAssociationException if there is an invalid association
	 * @see TableOfContents#createFileResourceIdentifier
	 */
	protected FileResourceIdentifier(TableOfContents tableOfContents, Element fileResourceIdentifierElement) throws InvalidAssociationException {
		this.tableOfContents = tableOfContents;
		this.element = fileResourceIdentifierElement;
		
		setupAssociations(tableOfContents, fileResourceIdentifierElement);
		setupAdditionalInfo();
	}

	
	/**
	 * Sets up <code>associations</code> from the <code>element</code>
	 * 
	 * @throws InvalidAssociationException If there is a problem with one of the <code>associations</code> 
	 */

	protected void setupAssociations(TableOfContents tableOfContents, Element fileResourceIdentifierElement) throws InvalidAssociationException {
		NodeList children = fileResourceIdentifierElement.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node child = children.item(i);
			if (!Constants.ASSOCIATED_ELEMENT.equals(child.getNodeName()))
				continue;
			
			Association association = new Association(tableOfContents, (Element) child);
			addAssoication(association);
		}
	}

	/**
	 * Gets the <code>URI</code> object that this <code>FileResourceIdentifier</code> refers to.  This URI may reference
	 * a file within the ACS container or reference a resource outside the ACS container.
	 * <p>
	 * Examples: "file:///my_fcs_file.fcs" would represent a file within the ACS container (<u>not the OS's file system</u>), while "http://flowcyt.sourceforge.net/acs/latest.pdf"
	 * would represent a file that is not included within the ACS container.
	 * 
	 * @return a url to the file this <code>FileResourceIdentifier</code> refers to 
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code>
	 * @throws URISyntaxException If there is a problem the <code>URI</code>
	 */
	public URI getUri() throws InvalidIndexException, URISyntaxException {
		return new URI(getAttributeValue(Constants.URI_ATTRIBUTE));
	}
	

	/**
	 * Gets the path to the file that this <code>FileResourceIdentifier</code> refers to without a URI scheme (file://). This is just a convenience method
	 * to <code>getUri().getPath()</code> and should not be used with <code>FileResourceIdentifier</code>s instances with <code>isExternalReference()</code>
	 * methods that return <code>true</code>.
	 *
	 * @return a path to the file this <code>FileResourceIdentifier</code> refers to within an ACS container, or <code>null</code> if it is not contained within
	 * the ACS container.
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code>
	 * @throws URISyntaxException If there is a problem the <code>URI</code>
	 */
	protected String getAcsFilePath() throws InvalidIndexException, URISyntaxException {
		if (isExternalReference())
			return null;
		return getUri().getPath();
	}
	
	/**
	 * Sets the URI <code>String</code> to the file that this <code>FileResourceIdentifier</code> refers to.
	 * 
	 * @param uri the <code>String</code> uri to the file that this <code>FileResourceIdentifier</code> refers to
	 * @throws InvalidFileResourceUriSchemeException if a URI scheme is used that is now allowed according to the ACS specification.
	 * @throws URISyntaxException If there is a problem the <code>URI</code>
	 * @throws InvalidIndexException 
	 * @throws DuplicateFileResourceIdentifierException if a <code>FileResourceIdentifier</code> with the same <code>URI</code> already exists in the
	 * <code>TableOfContents</code> that owns this FileResourceIdentifier
	 * @see FileResourceIdentifier#ALLOWED_URI_SCHEMES 
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public void setUri(String uri) throws InvalidFileResourceUriSchemeException, InvalidIndexException, URISyntaxException, DuplicateFileResourceIdentifierException {
		setUri(new URI(uri));
	}

	/**
	 * Sets the <code>URI</code> to the file that this <code>FileResourceIdentifier</code> refers to.
	 * 
	 * @param uri the <code>URI</code> to the file that this <code>FileResourceIdentifier</code> refers to
	 * @throws InvalidFileResourceUriSchemeException if a URI scheme is used that is now allowed according to the ACS specification.
	 * @throws URISyntaxException If there is a problem the <code>URI</code>
	 * @throws InvalidIndexException 
	 * @throws DuplicateFileResourceIdentifierException if a <code>FileResourceIdentifier</code> with the same <code>URI</code> already exists in the
	 * <code>TableOfContents</code> that owns this FileResourceIdentifier
	 * @see FileResourceIdentifier#ALLOWED_URI_SCHEMES 
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public void setUri(URI uri) throws InvalidFileResourceUriSchemeException, InvalidIndexException, URISyntaxException, DuplicateFileResourceIdentifierException {
		if (!isUriSchemeAllowed(uri))
			throw new InvalidFileResourceUriSchemeException("The uri " + uri + "contains a scheme that is not allowed");
		
		URI oldUri = getUri();
		
		// Not sure if this is needed or a good idea
//		// Correct for a missing / after file:// (eg file://foo to file:///foo) 
//		// Check to see if it's a scheme
//		if (Constants.FILE_SCHEME.equalsIgnoreCase(uri.getScheme().toLowerCase())) {
//			// Check to see if the host slash is missing
//			if (StringUtils.isBlank(uri.getPath())) {
//				// fix uri
//				uri = new URI(uri.getScheme()+":///" + uri.getHost());
//			}
//		}
		
		// renameFileResourceIdentifier needs to be called first to make sure a duplicate URI isn't set
		if (tableOfContents != null)
			tableOfContents.renameFileResourceIdentifier(oldUri, uri);

		setAttributeValue(Constants.URI_ATTRIBUTE, uri.toString());

	}

	/**
	 * Gets the MIME type of the file that this <code>FileResourceIdentifier</code> refers to.
	 * <p>
	 * NOTE: "application/vnd.isac.fcs" is the expected MIME type to be used in the case of FCS files. (Available in <code>Constants.FCS_FILE_MIME_TYPE</code>.)
	 * 
	 * @return a <code>String</code> representing the MIME type of the file that this <code>FileResourceIdentifier</code> refers to,
	 * or <code>null</code> if one was not set
	 * @see Constants#FCS_FILE_MIME_TYPE
	 */
	public String getMimeType() {
		return getAttributeValue(Constants.MIME_TYPE_ATTRIBUTE);
	}

	/**
	 * Sets the MIME type of the file that this <code>FileResourceIdentifier</code> refers to.
	 * <p>
	 * NOTE: "application/vnd.isac.fcs" is the expected MIME type to be used in the case of FCS files. (Available in <code>Constants.FCS_FILE_MIME_TYPE</code>.)
	 * 
	 * @param mimeType a <code>String</code> representing the MIME type of the file that this <code>FileResourceIdentifier</code> refers to,
	 * or <code>null</code> if there is none
	 * @see Constants#FCS_FILE_MIME_TYPE
	 */
	public void setMimeType(String mimeType) {
		// If the mimeType is a null or an empty String then delete the attribute to keep things tidy
		if (StringUtils.isBlank(mimeType))
			removeAttribute(Constants.MIME_TYPE_ATTRIBUTE);
		
		// Strip white spaces off the ends of the mime type
		mimeType = StringUtils.strip(mimeType);
		
		setAttributeValue(Constants.MIME_TYPE_ATTRIBUTE, mimeType);
	}

	/**
	 * Gets the description of the file that this <code>FileResourceIdentifier</code> refers to.
     *
	 * @return a <code>String</code> description of the file that this <code>FileResourceIdentifier</code> refers to
	 */
	public String getDescription() {
		return getAttributeValue(Constants.DESCRIPTION_ATTRIBUTE);
	}
	
	/**
	 * Sets the description of the file that this <code>FileResourceIdentifier</code> refers to.
	 * 
	 * @param description a <code>String</code> description of the file that this <code>FileResourceIdentifier</code> refers to
	 */
	public void setDescription(String description) {
		// If the description is a null or an empty String then delete the attribute to keep things tidy
		if (StringUtils.isBlank(description))
			removeAttribute(Constants.DESCRIPTION_ATTRIBUTE);
		
		setAttributeValue(Constants.DESCRIPTION_ATTRIBUTE, description);
	}
	
	/**
	 * Creates an <code>Association</code> between this <code>FileResourceIdentifier</code> and another.
	 * 
	 * @param withFileResourceIdentifier the <code>FileResourceIdentifier</code> the association that this <code>FileResourceIdentifier</code> has a relationship with
	 * @param relationship A <code>String</code> describing the relationship.  A constant out of <code>RelationshipTypes</code>, which represent the known association types
	 * from the ACS specification, should be considered before using a custom type.  An <code>InvalidAssociationException</code> will be thrown if this parameter is empty or null.
	 * @return a new <code>Association</code> object
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws URISyntaxException If there is a problem with any of the URI contained within this <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code> 
	 * @throws InvalidFileResourceUriSchemeException if a URI scheme is used that is now allowed according to the ACS specification.
	 * @see RelationshipTypes
	 */
	public Association createAssociation(FileResourceIdentifier withFileResourceIdentifier, String relationship) throws InvalidAssociationException, InvalidIndexException, URISyntaxException, InvalidFileResourceUriSchemeException {
		Association association = new Association(this, withFileResourceIdentifier, relationship);
		addAssoication(association);
		return association;
	}
		
	/**
	 * Adds an <code>Association</code> to this <code>FileResourceIdentifier</code>.
	 * 
	 * @param association the association to add
	 */
	protected void addAssoication(Association association) {
		element.appendChild(association.element);
		associations.add(association);
	}
	
	/**
	 * Removes an <code>Association</code> from this <code>FileResourceIdentifier</code>.
	 * 
	 * @param association the <code>Association</code> to remove
	 */
	public void removeAssociation(Association association) {
		element.removeChild(association.element);
		associations.remove(association);
	}
	
	/**
	 * Returns <code>true</code> if this <code>FileResourceIdentifier</code> has associations.
	 * 
	 * @return <code>true</code> if this <code>FileResourceIdentifier</code> has associations, <code>false</code> otherwise
	 */
	public boolean hasAssociations() {
		return getAssociations().length > 0;
	}
	
	/**
	 * Returns an array of all <code>Association</code> instances from this <code>FileResourceIdentifier</code>.
	 * @return an array of <code>Association</code>s
	 */
	public Association[] getAssociations() {
		Association[] results = new Association[associations.size()];
		associations.toArray(results);
		return results;
	}

	/**
	 * Returns <code>true</code> if the file this <code>FileResourceIdentifier</code> represents is known to be an FCS file by MIME type or extension.
	 * 
	 * @return <code>true</code> if the file this <code>FileResourceIdentifier</code> represents is known to be an FCS file by MIME type or extension, <code>false</code otherwise
	 */
	public boolean isFcsFile() {
		if (Constants.FCS_FILE_MIME_TYPE.equals(getMimeType()))
			return true;
		
		try {
			URI uri = getUri();
			return (uri != null && uri.getPath().toLowerCase().endsWith(Constants.FCS_FILE_EXTENSION));
		} catch (Exception e) {
			return false;		
		}		
	}
	
	/**
	 * Returns the <code>ACS</code> object this <code>FileResourceIdentifier</code> is owned by.
	 * 
	 * @return the <code>ACS</code> object this <code>FileResourceIdentifier</code> is owned by
	 */
	public ACS getAcs() {
		return tableOfContents.getAcs();
	}
	
	/**
	 * Returns the <code>TableOfContents</code> object this <code>FileResourceIdentifier</code> is owned by.
	 * 
	 * @return the <code>TableOfContents</code> object this <code>FileResourceIdentifier</code> is owned by
	 */
	public TableOfContents getTableOfContents() {
		return tableOfContents;
	}
	
	/**
	 * Returns the <code>URI</code> scheme of the file this <code>FileResourceIdentifier</code> represents.
     * 
	 * @return a <code>String</code> URI <code>URI</code> scheme
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URI contained within this <code>FileResourceIdentifier</code>
	 */
	public String resourceScheme() throws InvalidIndexException, URISyntaxException {
		URI uri = getUri();
		if (uri == null)
			return null;
		
		return getUri().getScheme();
	}
	
	/**
	 * Returns <code>true</code> if the file this <code>FileResourceIdentifier</code> represents is an external resource, which means it won't live
	 * in the ACS container.  
	 * 
	 * @return <code>true</code> if the file this <code>FileResourceIdentifier</code> represents is an external resource, <code>false</code> otherwise
	 */
	public boolean isExternalReference() {
		try {
			String scheme = resourceScheme();
			return !Constants.FILE_SCHEME.equalsIgnoreCase(scheme);
		} catch (Exception e) {
			return false;
		}		
	}
	
	/**
	 * Write the file that this <code>FileResourceIdentifier</code> represents to an <code>OutputStream</code>.
	 * 
	 * @param outputStream the <code>OutputStream</code> to write to
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs
	 */
	// TODO extract remote file
	public void writeRepresentedFile(OutputStream outputStream) throws IOException, InvalidIndexException, URISyntaxException {
		// If a sourceInputStream is available, that means that this FileResorceInstance represents a file that
		// has not yet been written to a zip file and needs to be extracted from an external source
		if (hasSourceInputStream()) {
			try {
				// Do not allow the sourceInputStream to be read more than once
				if (this.sourceInputStreamReadFrom)
					throw new IOException("sourceFileStream has already been read from");

				FileUtils.writeInputStreamToOutputStream(sourceFileStream, outputStream);
			} finally {
				this.sourceInputStreamReadFrom = true;
			}
		} else if (hasSourceFile()) {	

			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			
			try {
				FileUtils.writeInputStreamToOutputStream(fileInputStream, outputStream);
			} finally {
				fileInputStream.close();
			}
			
			
		} else  {
			ACS acs = getAcs();
			acs.extractFile(getUri(), outputStream);
		}
	}
	
	/**
	 * Write the file that this <code>FileResourceIdentifier</code> represents to a <code>File</code>.
	 * 
	 * @param targetFile the <code>File</code> to write to
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs
	 */
	public void writeRepresentedFile(File targetFile) throws IOException, InvalidIndexException, URISyntaxException {
		FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
		try {
			writeRepresentedFile(fileOutputStream);
		} finally {
			fileOutputStream.close();
		}
	}
	
	/**
	 * Write the file that this <code>FileResourceIdentifier</code> represents to a <code>String</code>.
	 * 
	 * @return a <code>String</code> with the contents of the file that this <code>FileResourceIdentifier</code> represents
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs
	 */
	// TODO extract remote file
	public String writeRepresentedFileToString() throws IOException, InvalidIndexException, URISyntaxException {
		// If a sourceInputStream is available, that means that this FileResorceInstance represents a file that
		// has not yet been written to a zip file and needs to be extracted from an external source
		if (hasSourceInputStream()) {
			try {
				// Do not allow the sourceInputStream to be read more than once
				if (this.sourceInputStreamReadFrom)
					throw new IOException("sourceFileStream has already been read from");

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();				
				FileUtils.writeInputStreamToOutputStream(sourceFileStream, byteArrayOutputStream);
				return byteArrayOutputStream.toString();
			} finally {
				this.sourceInputStreamReadFrom = true;
			}
		} else {
			ACS acs = getAcs();
			return acs.extractFileToString(getUri());
		}
	}

	
	/**
	 * Returns <code>true</code> if the file that this <code>FileResourceIdentifier</code> represents source exists outside the ACS container as a source stream.
     * This will happen if this was a newly added <code>FileResourceIdentifier</code> and has not yet been written to a new ACS container.
     * 
	 * @return <code>true</code> if the file that this <code>FileResourceIdentifier</code> represents source exists outside the ACS container, <code>false</code> otherwise
	 */
	public boolean hasSourceInputStream() {
		return sourceFileStream != null;
	}

	
	/**
	 * Returns <code>true</code> if the file that this <code>FileResourceIdentifier</code> represents source exists outside the ACS container as a source stream.
     * This will happen if this was a newly added <code>FileResourceIdentifier</code> and has not yet been written to a new ACS container.
     * 
	 * @return <code>true</code> if the file that this <code>FileResourceIdentifier</code> represents source exists outside the ACS container, <code>false</code> otherwise
	 */
	public boolean hasSourceFile() {
		return sourceFile != null;
	}
	
	/**
	 * Returns the <code>InputStream</code> to the file that this <code>FileResourceIdentifier</code> represents source exists outside the ACS container as a source stream.
	 * 
	 * @return the <code>InputStream</code> to the file that this <code>FileResourceIdentifier</code> represents source exists outside the ACS container as a source stream,
	 * or <code>null</code> if one does not exist.
	 * @throws IOException if there is no sourceInputStream available as either a stream or file
	 */
	public InputStream getSourceInputStream() throws IOException {
		
		if(hasSourceInputStream()){
			return sourceFileStream;	
		} else if(hasSourceFile()) {
			return new FileInputStream(sourceFile);
		} else {
			throw new IOException("No Source Input Stream Available.");
		}
				
	}
		
	/**
	 * Returns <code>true</code> if the scheme of the <code>URI</code> is allowed for <code>FileResourceIdentifier</code>s, according to the ACS specification.
	 * 
	 * @param uri the <code>URI</code> to test if it scheme is allowed
	 * @return <code>true</code> if it is allowed, <code>false</code> if it is not
	 * @see FileResourceIdentifier#ALLOWED_URI_SCHEMES
	 */
	public static boolean isUriSchemeAllowed(URI uri) {
		if (uri == null)
			return false;
				
		return isUriSchemeAllowed(uri.getScheme());		
	}

	/**
	 * Returns <code>true</code> if the scheme is allowed for <code>FileResourceIdentifier</code>s, according to the ACS specification.
	 * 
	 * @param uri the <code>String</code> representation of a scheme to test if it is allowed
	 * @return <code>true</code> if it is allowed, <code>false</code> if it is not
	 * @see FileResourceIdentifier#ALLOWED_URI_SCHEMES
	 */	
	public static boolean isUriSchemeAllowed(String uri) {
		if (uri == null)
			return false;
				
		for (String allowedScheme : ALLOWED_URI_SCHEMES) {
			if (allowedScheme.equalsIgnoreCase(uri))
				return true;
		}
		
		return false;		
	}

	/**
	 * Closes any open streams.
	 */
	public void close() {
		if (sourceFileStream != null) {
			try {
				sourceFileStream.close();
			} catch (IOException ignore) {}
		}
	}
}
