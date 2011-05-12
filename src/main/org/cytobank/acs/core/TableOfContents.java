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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.cytobank.acs.core.exceptions.DuplicateFileResourceIdentifierException;
import org.cytobank.acs.core.exceptions.InvalidAssociationException;
import org.cytobank.acs.core.exceptions.InvalidFileResourceUriSchemeException;
import org.cytobank.acs.core.exceptions.InvalidIndexException;

/**
 * This class represents and provides convenience methods for an ACS table of contents xml file found within
 * an ACS file.
 * 
 * @author Chad Rosenberg <chad@cytobank.org>
 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
 */

public class TableOfContents extends AdditionalInfoElementWrapper {
	
	/** The version of table of contents that this instance represents. */
	protected int version;
	
	/** The <code>ACS</code> instance that this <code>TableOfContents</code> is owned by. */
	protected ACS acs;
	
	/** The xml <code>org.w3c.dom.Document</code> that this <code>TableOfContents</code> is associated with. */
	protected Document tableOfContentsDoc;
		
	/** The <code>FileResourceIdentifier</code> that this <code>TableOfContents</code> contains. */
	protected Vector<FileResourceIdentifier> fileResourceIdentifiers;
	
	/** A <code>HashMap</code> indexing the list of <code>FileResourceIdentifier</code>s by a <String> uri. */
	protected HashMap<String, FileResourceIdentifier> fileResourceIdentifiersByUri;
	
	/**
	 * Creates a <code>TableOfContents</code> instance from an ACS table of contents xml file.
	 * <p>
	 * NOTE: The preferred method for creating an instance of <code>TableOfContents</code> is to use the
	 * {@link ACS#createNextTableOfContents()} method.
	 * 
	 * @param tableOfContentsXml a <code>File</code> pointing to the ACS xml table of contents xml file
	 * @param acs the <code>ACS</code> instance that the created <code>TableOfContents</code> will be owned by
	 * @param version the version of the <code>TableOfContents</code> to be created
	 * @throws InvalidIndexException If the file version could not be parsed, or does not conform to the spec.
	 * @throws URISyntaxException If there is a problem with any of the URIs
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @throws SAXException If there was a problem parsing the xml
	 * @see ACS#createNextTableOfContents
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public TableOfContents(File tableOfContentsXml, ACS acs, int version) throws InvalidIndexException, URISyntaxException, InvalidAssociationException, DuplicateFileResourceIdentifierException, SAXException {
		this.acs = acs;
		this.version = version;
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(tableOfContentsXml);
			parseXml(fileInputStream);
			fileInputStream.close();
		} catch (IOException ioe) {
			throw new InvalidIndexException(ioe.toString());
		}
	}
	
	/**
	 * Creates a <code>TableOfContents</code> instance from an <code>InputStream</code> that contains an ACS table of contents xml.
	 * <p>
	 * NOTE: The preferred method for creating an instance of <code>TableOfContents</code> is to use the
	 * {@link ACS#createNextTableOfContents()} method.
	 * 
	 * @param tableOfContentsXmlStream an <code>InputStream</code> that contains an ACS table of contents xml file
	 * @param version the version of the <code>TableOfContents</code> to be created
	 * @throws InvalidIndexException If the file version could not be parsed, or does not conform to the spec.
	 * @throws URISyntaxException If there is a problem with any of the URIs
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate  
	 * @throws SAXException If there was a problem parsing the xml
	 * @see ACS#createNextTableOfContents
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public TableOfContents(InputStream tableOfContentsXmlStream, int version) throws InvalidIndexException, IOException, URISyntaxException, InvalidAssociationException, DuplicateFileResourceIdentifierException, SAXException {
		this.version = version;
		parseXml(tableOfContentsXmlStream);		
	}
	
	/**
	 * Creates a copy of a <code>TableOfContents</code> from another while allowing a new version to be specified.
	 * 
	 * @param tableOfContents the <code>TableOfContents</code> to copy
	 * @param version the version of the new instance
	 * @throws InvalidIndexException If the file version could not be parsed, or does not conform to the spec.
	 * @throws URISyntaxException If there is a problem with any of the URIs
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate  
	 * @throws SAXException If there was a problem parsing the xml
	 */
	public TableOfContents(TableOfContents tableOfContents, int version) throws InvalidIndexException, URISyntaxException, InvalidAssociationException, DuplicateFileResourceIdentifierException, SAXException {
		this.acs = tableOfContents.acs;
		this.version = version;
		FileInputStream fileInputStream;
		try {
			File previousXml = acs.tempFile();
			tableOfContents.writeXml(previousXml);
			fileInputStream = new FileInputStream(previousXml);
			parseXml(fileInputStream);
			fileInputStream.close();
		} catch (IOException ioe) {
			throw new InvalidIndexException(ioe.toString());
		}
	}
	
	/**
	 * Parses an ACS table of contents xml from an <code>InputStream</code> and sets the <code>tableOfContentsDoc</code> and <code>element</code>
	 * element variables for this instance from the xml parser.
	 * 
	 * @param tableOfContentsXmlStream the table of contents 
	 * @throws InvalidIndexException If the file version could not be parsed, or does not conform to the spec.
	 * @throws IOException If an input or output exception occurred
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @throws SAXException If there was a problem parsing the xml
	 */
	protected void parseXml(InputStream tableOfContentsXmlStream) throws InvalidIndexException, IOException, URISyntaxException, InvalidAssociationException, DuplicateFileResourceIdentifierException, SAXException {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			tableOfContentsDoc = docBuilder.parse(tableOfContentsXmlStream);
			element = tableOfContentsDoc.getDocumentElement();
			setupFileResourceIdentifiers();
			setupAdditionalInfo();
		} catch (ParserConfigurationException pce) {
			throw new InvalidIndexException(pce.toString());
		}
		
	}
	
	/**
	 * Returns the <code>ACS</code> instance that this <code>TableOfContents</code> is owned by.
	 * 
	 * @return the <code>ACS</code> that this <code>TableOfContents</code> is owned by
	 */
	public ACS getAcs() {
		return acs;
	}

	/**
	 * Sets the <code>ACS</code> instance that this <code>TableOfContents</code> is owned by.
	 * 
	 * @param acs the <code>ACS</code> instance that this <code>TableOfContents</code>
	 */
	public void setAcs(ACS acs) {
		this.acs = acs;
	}
		
	/**
	 * Gets the ACS index file version that this <code>TableOfContents</code> represents.
	 * 
	 * @return index file version
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Returns the <code>TableOfContents</code> that is parent to this one.
	 * <p>
	 * NOTE: The data that the <code>TableOfContents</code> uses does not use inheritance and
	 * the parent <code>TableOfContents</code> instance is not necessary to derive any
	 * of the data represented by this instance. 
	 * 
	 * @return the parent <code>TableOfContents</code> to this one
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public TableOfContents getParentTableOfContents() {
		// TODO
		return null;
	}
	
	/**
	 * Returns the number of <code>FileResourceIdentifier</code>s contained within this <code>TableOfContents</code> instance.
	 * 
	 * @return the number of <code>FileResourceIdentifier</code>s
	 */
	public int getNumberOfFileResourceIdentifiers() {
		return fileResourceIdentifiers.size();
	}
	
	/**
	 * Returns an array of all the <code>FileResourceIdentifier</code>s contained within this <code>TableOfContents</code> instance. 
	 *
	 * @return an array of all the <code>FileResourceIdentifier</code>s
	 */
	public FileResourceIdentifier[] getFileResourceIdentifiers() {
		FileResourceIdentifier[] results = new FileResourceIdentifier[fileResourceIdentifiers.size()];
		fileResourceIdentifiers.toArray(results);
		return results;
	}

	/**
	 * Returns an array of all the <code>FileResourceIdentifier</code>s contained within this <code>TableOfContents</code> instance that are associated to
	 * another FileResourceIdentifier.
	 * <p>
	 * This method essentially takes all <code>FileResourceIdentifier</code>s and filters them down to the results that have an association with the specified
	 * <code>associatedTo</code> <code>FileResourceIdentifier</code>.  This is particularly useful if all <code>FileResourceIdentifier</code>s are needed for a specific workspace. 
	 *
	 * @return an array of all the <code>FileResourceIdentifier</code>s
	 */
	public FileResourceIdentifier[] getFileResourceIdentifiersAssociatedTo(FileResourceIdentifier associatedTo) throws InvalidAssociationException, InvalidIndexException, URISyntaxException {
		if (associatedTo == null)
			return null;
		
		
		Vector<FileResourceIdentifier> associatedFileResourceIdentifiers = new Vector<FileResourceIdentifier>();
		
		for (FileResourceIdentifier fileResource : fileResourceIdentifiers) {
			for (Association association : fileResource.associations) {
				if (associatedTo.equals(association.getAssociatedTo())) {
					associatedFileResourceIdentifiers.add(fileResource);
					break;
				}
			}
		}
		
		FileResourceIdentifier[] results = new FileResourceIdentifier[associatedFileResourceIdentifiers.size()];
		associatedFileResourceIdentifiers.toArray(results);
		return results;
	}
		
	/**
	 * Returns an array of all the <code>FileResourceIdentifier</code>s contained within this <code>TableOfContents</code> instance 
	 * that represent FCS files.
	 * 
	 * @return an array of all the <code>FileResourceIdentifier</code>s that are fcs files.
	 */
	public FileResourceIdentifier[] getFcsFiles() {
		Vector<FileResourceIdentifier> fcsFiles = new Vector<FileResourceIdentifier>();
		
		for (FileResourceIdentifier fileResource : fileResourceIdentifiers) {
			if (fileResource.isFcsFile()) {
				fcsFiles.add(fileResource);
			}
		}
		
		FileResourceIdentifier[] results = new FileResourceIdentifier[fcsFiles.size()];
		fileResourceIdentifiers.toArray(results);
		
		return results;
	}

	
	/**
	 * Returns an array of all the <code>FileResourceIdentifier</code>s contained within this <code>TableOfContents</code> instance 
	 * that represent FCS files with an association to a particular <code>FileResourceIdentifier</code>.
	 * <p>
	 * This method is particularly useful when all FCS files associated with a given workspace FileResourceIdentifier are needed.
	 * 
	 * @return an array of all the <code>FileResourceIdentifier</code>s that are FCS files, or <code>null</code> if <code>associatedTo</code> parameter
	 *         is <code>null</code>.
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws InvalidAssociationException if there is an invalid association
	 */
	public FileResourceIdentifier[] getFcsFilesAssociatedTo(FileResourceIdentifier associatedTo) throws InvalidAssociationException, InvalidIndexException, URISyntaxException {
		if (associatedTo == null)
			return null;
		
		Vector<FileResourceIdentifier> fcsFiles = new Vector<FileResourceIdentifier>();
		
		for (FileResourceIdentifier fileResource : fileResourceIdentifiers) {
			if (fileResource.isFcsFile()) {
				for (Association association : fileResource.associations) {
					if (associatedTo.equals(association.getAssociatedTo())) {
						fcsFiles.add(fileResource);
						break;
					}
				}
			}
		}
		
		FileResourceIdentifier[] results = new FileResourceIdentifier[fcsFiles.size()];
		fcsFiles.toArray(results);
		
		return results;
	}

	
	/**
	 * Returns an array of all unique <code>FileResourceIdentifier</code>s contained within this <code>TableOfContents</code> instance
	 * that have been identified as project workspaces.
	 * 
	 * @return an array of all the <code>FileResourceIdentifier</code>s that are project workspaces
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws InvalidAssociationException if there is an invalid association
	 * @see RelationshipTypes#isProjectWorkspace
	 */
	public FileResourceIdentifier[] getProjectWorkspaces() throws InvalidAssociationException, InvalidIndexException, URISyntaxException {
		HashSet<FileResourceIdentifier> projectWorkspaces = new HashSet<FileResourceIdentifier>();
		
		// Find all fileResource associations and add the associated file to projectWorkspaces
		// if that association relationship is a project workspace.
		for (FileResourceIdentifier fileResource : fileResourceIdentifiers) {
			for (Association association : fileResource.associations) {
				if (RelationshipTypes.isProjectWorkspace(association.getRelationship())) {
					projectWorkspaces.add(association.getAssociatedTo());
				}
			}
		}
		
		FileResourceIdentifier[] results = new FileResourceIdentifier[projectWorkspaces.size()];
		projectWorkspaces.toArray(results);
		
		return results;
	}

	/**
	 * Returns a <code>FileResourceIdentifier</code> specified by the given <code>String</code> uri.    
	 * 
	 * @param uri A case-insensitive string uri. A scheme type is expected (file://, http://, etc).
	 * @return a <code>FileResourceIdentifier</code>s or null if it could not be found
	 */
	public FileResourceIdentifier getFileResourceIdentifierByUri(String uri) {
		return fileResourceIdentifiersByUri.get(uri.toLowerCase());
	}

	/**
	 * Returns a <code>FileResourceIdentifier</code> specified by the given <code>URI</code>.    
	 * 
	 * @param uri A case-insensitive <code>URI</code>
	 * @return a <code>FileResourceIdentifier</code>s or null if it could not be found
	 */
	public FileResourceIdentifier getFileResourceIdentifierByUri(URI uri) {
		return getFileResourceIdentifierByUri(uri.toString());
	}
	
	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from a local file.
	 *
	 * @param resourcePath the <code>String</code> uri of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourceFilePath the <code>String</code> path to the file that the <code>FileResourceIdentifier</code> will represent
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 */
	public FileResourceIdentifier createFileResourceIdentifier(String resourcePath, String sourceFilePath) throws InvalidIndexException, URISyntaxException, IOException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {		
		return createFileResourceIdentifier(new URI(resourcePath), new File(sourceFilePath));
	}
	
	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from a local file.
	 *
	 * @param resourcePath the <code>String</code> uri of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourceFile the <code>File</code> that the <code>FileResourceIdentifier</code> will represent
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 */
	public FileResourceIdentifier createFileResourceIdentifier(String resourcePath, File sourceFile) throws InvalidIndexException, URISyntaxException, IOException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		return createFileResourceIdentifier(new URI(resourcePath), sourceFile);
	}

	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from a local file with a specified MIME type.
	 * <p>
	 * NOTE: "application/vnd.isac.fcs" is the expected MIME type to be used in the case of FCS files.  (Available in {@link Constants#FCS_FILE_MIME_TYPE}.)
	 *
	 * @param resourcePath the <code>String</code> uri of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourceFile the <code>File</code> that the <code>FileResourceIdentifier</code> will represent
	 * @param mimeType the MIME type to be associated with the file
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 */
	public FileResourceIdentifier createFileResourceIdentifier(String resourcePath, File sourceFile, String mimeType) throws InvalidIndexException, URISyntaxException, IOException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		return createFileResourceIdentifier(new URI(resourcePath), sourceFile, mimeType);
	}
	
	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from a local file.
	 *
	 * @param resourcePath the case-insensitive <code>URI</code> of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourceFile the <code>File</code> that the <code>FileResourceIdentifier</code> will represent
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 */
//	public FileResourceIdentifier createFileResourceIdentifier(URI resourcePath, File sourceFile) throws InvalidIndexException, URISyntaxException, IOException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
//		FileResourceIdentifier fileResource = createFileResourceIdentifier(resourcePath, sourceFile);
//		return fileResource;
//	}

	/** TODO */
	public FileResourceIdentifier createFileResourceIdentifier(URI resourcePath, File sourceFile) throws InvalidIndexException, URISyntaxException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		FileResourceIdentifier fileResource = new FileResourceIdentifier(this, sourceFile);
		fileResource.setUri(resourcePath);
		trackFileResourceIdentifier(fileResource);
		
		// Now add the xml element that fileResource represents to this instance of TableOfContents's xml element
		element.appendChild(fileResource.element);
		return fileResource;
	}
	
	
	
	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from an <code>InputStream</code>. The
	 * <code>sourceFileStream</code> parameter will not be used until {@link FileResourceIdentifier#writeRepresentedFile} is called, which happens on
	 * a {@link ACS#writeAcsContainer} call.
	 *
	 * @param resourcePath the case-insensitive <code>URI</code> of the resource, which must be unique within this <code>TableOfContents</code>.
	 * @param sourceFileStream the <code>InputStream</code> that the <code>FileResourceIdentifier</code> will represent
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @throws IOException If an input or output exception occurred
	 */
	public FileResourceIdentifier createFileResourceIdentifier(URI resourcePath, InputStream sourceFileStream) throws InvalidIndexException, URISyntaxException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		FileResourceIdentifier fileResource = new FileResourceIdentifier(this, sourceFileStream);
		fileResource.setUri(resourcePath);
		trackFileResourceIdentifier(fileResource);
		
		// Now add the xml element that fileResource represents to this instance of TableOfContents's xml element
		element.appendChild(fileResource.element);
		return fileResource;
	}

	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from a local fcs.
	 * <p>
	 * NOTE: "application/vnd.isac.fcs" is the expected MIME type to be used in the case of FCS files. (Available in {@link Constants#FCS_FILE_MIME_TYPE}.)
	 *
	 * @param resourcePath the case-insensitive <code>URI</code> of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourceFile the <code>File</code> that the <code>FileResourceIdentifier</code> will represent
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @see Constants#FCS_FILE_MIME_TYPE
	 */
	public FileResourceIdentifier createFcsFileResourceIdentifier(URI resourcePath, File sourceFile) throws InvalidIndexException, URISyntaxException, IOException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		return createFileResourceIdentifier(resourcePath, sourceFile, Constants.FCS_FILE_MIME_TYPE);
	}
	
	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from a local fcs file.
	 * <p>
	 * NOTE: "application/vnd.isac.fcs" is the expected MIME type to be used in the case of FCS files. (Available in {@link Constants#FCS_FILE_MIME_TYPE}.)
	 *
	 * @param resourcePath the case-insensitive <code>String</code> uri of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourcePath the <code>String</code> file path to the file that the <code>FileResourceIdentifier</code> will represent
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @see Constants#FCS_FILE_MIME_TYPE
	 */
	public FileResourceIdentifier createFcsFileResourceIdentifier(String resourcePath, String sourcePath) throws InvalidIndexException, URISyntaxException, IOException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		URI resourcePathUri = new URI(resourcePath);
		File sourceFile = new File(sourcePath);
		return createFileResourceIdentifier(resourcePathUri, sourceFile, Constants.FCS_FILE_MIME_TYPE);
	}
	
	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from a local file with a specified MIME type.
	 * <p>
	 * NOTE: "application/vnd.isac.fcs" is the expected MIME type to be used in the case of FCS files. (Available in {@link Constants#FCS_FILE_MIME_TYPE}.)
	 *
	 * @param resourcePath the case-insensitive <code>String</code> uri of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourcePath the <code>String</code> file path to the file that the <code>FileResourceIdentifier</code> will represent
	 * @param mimeType the MIME type to be associated with the file
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @see Constants#FCS_FILE_MIME_TYPE
	 */
	public FileResourceIdentifier createFileResourceIdentifier(String resourcePath, String sourcePath, String mimeType) throws InvalidIndexException, URISyntaxException, IOException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		URI resourcePathUri = new URI(resourcePath);
		File sourceFile = new File(sourcePath);
		return createFileResourceIdentifier(resourcePathUri, sourceFile, mimeType);
	}
	
	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from a local file with a specified MIME type.
	 * <p>
	 * NOTE: "application/vnd.isac.fcs" is the expected MIME type to be used in the case of FCS files. (Available in {@link Constants#FCS_FILE_MIME_TYPE}.)
	 *
	 * @param resourcePath the case-insensitive <code>URI</code> of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourceFile the <code>File</code> that the <code>FileResourceIdentifier</code> will represent
	 * @param mimeType the MIME type to be associated with the file
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @see Constants#FCS_FILE_MIME_TYPE
	 */
	public FileResourceIdentifier createFileResourceIdentifier(URI resourcePath, File sourceFile, String mimeType) throws InvalidIndexException, URISyntaxException, IOException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		FileInputStream sourceFileStream = new FileInputStream(sourceFile);
		FileResourceIdentifier fileResource = createFileResourceIdentifier(resourcePath, sourceFileStream, mimeType);
		return fileResource;
	}

	/**
	 * Creates a new <code>FileResourceIdentifier</code> that will be owned by this <code>TableOfContents</code> from an <code>InputStream</code> with a specified MIME type.  The
	 * <code>sourceFileStream</code> parameter will not be used until {@link FileResourceIdentifier#writeRepresentedFile} is called, which happens on
	 * a {@link ACS#writeAcsContainer} call. The <code>sourceFileStream</code> parameter will remain open until {@link ACS#writeAcsContainer}
	 * or {@link FileResourceIdentifier#close} is called.
	 * <p>
	 * NOTE: "application/vnd.isac.fcs" is the expected MIME type to be used in the case of FCS files.  (Available in {@link Constants#FCS_FILE_MIME_TYPE}.)
	 *
	 * @param resourcePath the case-insensitive <code>URI</code> of the resource, which must be unique within this <code>TableOfContents</code>
	 * @param sourceFileStream the <code>InputStream</code> that the <code>FileResourceIdentifier</code> will represent
	 * @param mimeType the MIME type to be associated with the file
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws InvalidFileResourceUriSchemeException if the resourcePath <code>URI</code> contains a scheme that is not allowed according to the ACS specification.
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @throws IOException If an input or output exception occurred
	 * @see Constants#FCS_FILE_MIME_TYPE
	 */
	public FileResourceIdentifier createFileResourceIdentifier(URI resourcePath, InputStream sourceFileStream, String mimeType) throws InvalidIndexException, URISyntaxException, InvalidAssociationException, InvalidFileResourceUriSchemeException, DuplicateFileResourceIdentifierException {
		FileResourceIdentifier fileResource = createFileResourceIdentifier(resourcePath, sourceFileStream);
		fileResource.setMimeType(mimeType);
		return fileResource;
	}
	
	/**
	 * Gets the xml file name of this <code>TableOfContents</code>, conforming to the ACS specification.
     *
	 * @return the xml file name of this <code>TableOfContents</code>
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public String getFileName() {
		return Constants.TOC_PREFIX + this.version + Constants.TOC_SUFFIX;
	}
	
	/**
	 * Tracks a <code>FileResourceIdentifier</code> against this <code>TableOfContents</code> instance so the xml doesn't have to be parsed and <code>FileResourceIdentifier</code>
	 * rebuilt every time it is needed.  <code>FileResourceIdentifier</code> with duplicate URIs will be blocked. 
	 * 
	 * @param fileResource the <code>FileResourceIdentifier</code> to add
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 */
	protected void trackFileResourceIdentifier(FileResourceIdentifier fileResource) throws InvalidIndexException, URISyntaxException, DuplicateFileResourceIdentifierException {
		String uriKey = uriKey(fileResource);
		
		if (fileResourceIdentifiersByUri.containsKey(uriKey))
			throw new DuplicateFileResourceIdentifierException("Cannot add duplicate URI " + fileResource.getUri());
		
		fileResourceIdentifiers.add(fileResource);
		fileResourceIdentifiersByUri.put(uriKey, fileResource);
	}


	/**
	 * No longer tracks a <code>FileResourceIdentifier</code> against this <code>TableOfContents</code> instance. 
	 * 
	 * @param fileResource the <code>FileResourceIdentifier</code> to remove
	 * @return <code>true</code> if the <code>FileResourceIdentifier</code> instance was successfully removed, <code>false</code> otherwise
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 */
	protected boolean untrackFileResourceIdentifier(FileResourceIdentifier fileResource) throws InvalidIndexException, URISyntaxException  {
		String uriKey = uriKey(fileResource);

		boolean success = fileResourceIdentifiers.removeElement(fileResource);
		if (success)
			fileResourceIdentifiersByUri.remove(uriKey);
		
		return success;
	}
	
	
	/**
	 * Removes a <code>FileResourceIdentifier</code> from this <code>TableOfContents</code> instance.
	 *  
	 * @param fileResourceIdentifier the <code>FileResourceIdentifier</code> to remove
	 * @return <code>true</code> if the <code>FileResourceIdentifier</code> instance was successfully removed, <code>false</code> otherwise
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code>
	 */
	public boolean removeFileResourceIdentifier(FileResourceIdentifier fileResourceIdentifier) throws InvalidIndexException, URISyntaxException {
		boolean success = untrackFileResourceIdentifier(fileResourceIdentifier);
		  
	    if (success) {
			// Remove the fileResourceIdentifier from the xml 
	    	success &= removeElementWrapper(fileResourceIdentifier);
	    }
	    	
		return success;
	}
	
	/**
	 * Creates a new <code>TableOfContents</code> containing all the contents of this one with a version one greater than this ones.
	 * <p>
	 * NOTE: This method does not add the newly created <code>TableOfContents</code> back to this <code>TableOfContents</code>'s
	 * owning <code>ACS</code> instance.  If you need to do that, it is suggested that you use the {@link ACS#createNextTableOfContents()} method
	 * instead.
	 * 
	 * @return a new <code>TableOfContents</code> containing all the contents of this one with a version one greater than this ones
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate  
	 * @throws SAXException If there was a problem parsing the xml
	 * @see ACS#createNextTableOfContents()
	 */
	public TableOfContents nextVersion() throws IOException, InvalidIndexException, InvalidIndexException, URISyntaxException, InvalidAssociationException, DuplicateFileResourceIdentifierException, SAXException {
		File tableOfContentsXmlFile = acs.tempFile();
		writeXml(tableOfContentsXmlFile);

		// Use the copy constructor to create a new version of this instance. 
		TableOfContents results = new TableOfContents(this, version + 1);
		
		return results;
	}
	
	/**
	 * Returns a <code>String</code> containing the XML that represents this <code>TableOfContents</code>.
	 * 
	 * @return XML that represents this <code>TableOfContents</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 */
	public String toXml() throws InvalidIndexException {
		try {
			Source source = new DOMSource(tableOfContentsDoc);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			String xmlResult = stringWriter.getBuffer().toString();

			return xmlResult;
		} catch (TransformerException te) {
			throw new InvalidIndexException(te.toString());
		}
	}
	
	/**
	 * Writes out the XML that represents this <code>TableOfContents</code> to a <code>File</code>.
	 * 
	 * @param targetFile the <code>File</code> to write out the xml to
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 */
	public void writeXml(File targetFile) throws IOException, InvalidIndexException {
		FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
		try {
			writeXml(fileOutputStream);
		} finally {
			fileOutputStream.close();
		}
	}	
	
	/**
	 * Writes out the XML that represents this <code>TableOfContents</code> to an <code>OutputStream</code>.
	 * 
	 * @param outputStream the <code>OutputStream</code> to write out the xml to
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 */
	public void writeXml(OutputStream outputStream) throws IOException, InvalidIndexException {
		PrintStream out = new PrintStream(outputStream, true);
		out.print(toXml());
	}
	
	/**
	 * Sets up <code>fileResourceIdentifiers</code>, <code>fileResourceIdentifiersByUri</code> from the <code>element</code>
	 * 
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 */
	protected void setupFileResourceIdentifiers() throws InvalidIndexException, URISyntaxException, InvalidAssociationException, DuplicateFileResourceIdentifierException {
		NodeList fileNodes = element.getElementsByTagName(Constants.FILE_ELEMENT);
		int numberOfFiles = (fileNodes == null) ? 0 : fileNodes.getLength();
		
		fileResourceIdentifiers = new Vector<FileResourceIdentifier>(numberOfFiles);
		fileResourceIdentifiersByUri = new HashMap<String, FileResourceIdentifier>(numberOfFiles);
		
		for (int i=0; i<numberOfFiles; i++) {
			Element fileResourceElement = (Element) fileNodes.item(i);
			FileResourceIdentifier fileResource = new FileResourceIdentifier(this, fileResourceElement);
			trackFileResourceIdentifier(fileResource);
		}

	}
	
	/**
	 * Renames a <code>FileResourceIdentifier</code> from an old uri to a new uri.  This should be done publicly through {@link FileResourceIdentifier#setUri(URI)}.
	 * 
	 * @param oldUri the old <code>URI</code>
	 * @param newUri the new <code>URI</code>
	 * @throws DuplicateFileResourceIdentifierException if a <code>FileResourceIdentifier</code> with the same <code>URI</code> already exists in
	 * this <code>TableOfContents</code> instance
	 * @throws URISyntaxException 
	 * @throws InvalidIndexException 
	 */
	protected void renameFileResourceIdentifier(URI oldUri, URI newUri) throws DuplicateFileResourceIdentifierException, InvalidIndexException, URISyntaxException {
		String oldUriKey = uriKey(oldUri);
		String newUriKey = uriKey(newUri);
		
		if (fileResourceIdentifiersByUri.containsKey(newUriKey))
			throw new DuplicateFileResourceIdentifierException("Cannot add duplicate URI " + newUri);
		
		FileResourceIdentifier fileResourceIdentifier = fileResourceIdentifiersByUri.remove(oldUriKey);
		if (fileResourceIdentifier != null)
			fileResourceIdentifiersByUri.put(newUriKey, fileResourceIdentifier);
	}
	
	/**
	 * Closes any open streams in any <code>FileResourceIdentifiers</code>.
	 * @see ACS#close()
	 * @see FileResourceIdentifier#close()
	 */
	public void close() {
		for (FileResourceIdentifier fileResourceIdentifier : fileResourceIdentifiers) {
			 fileResourceIdentifier.close();
		}
	}
	
	/**
	 * Creates a new blank instance of <code>TableOfContents</code> with no associated <code>ACS</code> file.
	 * 
	 * <p>
	 * NOTE: The preferred method for creating an instance of <code>TableOfContents</code> is to use the
	 * {@link ACS#createNextTableOfContents()} method.  This is just a convenience method to build a
	 * <code>TableOfContents</code> instance without worrying about the constructor requirements.
	 * 
	 * @return a new blank instance of <code>TableOfContents</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 * @throws IOException If an input or output exception occurred
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code> or if the URI is a duplicate
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @throws SAXException If there was a problem parsing the xml
	 * @see ACS#createNextTableOfContents
	 */
	public static TableOfContents newInstance() throws InvalidIndexException, IOException, URISyntaxException, InvalidAssociationException, DuplicateFileResourceIdentifierException, SAXException {
		InputStream tableOfContentsXmlStream = TableOfContents.class.getResourceAsStream(Constants.NEW_TOC_TEMPLATE);
		TableOfContents tableOfContents = new TableOfContents(tableOfContentsXmlStream, 1);
		tableOfContentsXmlStream.close();
		return tableOfContents;
	}
	
	/**
	 * Returns a lower case version of a <code>URI</code> for hash keys or the like from a <code>FileResourceIdentifier</code>.
	 * 
	 * @param fileResourceIdentifier the <code>FileResourceIdentifier</code> to return the uri key from
	 * @return the lower case <code>String</code> version of the <code>FileResourceIdentifier</code> URI
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 */
	protected static String uriKey(FileResourceIdentifier fileResourceIdentifier) throws InvalidIndexException, URISyntaxException {
		return uriKey(fileResourceIdentifier.getUri());
	}
	
	/**
	 * Returns a lower case version of a <code>URI</code> for hash keys or the like from a <code>URI</code>.
	 * 
	 * @param uri the <code>URI</code> to return the uri key from
	 * @return the lower case <code>String</code> version of a URI
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code>
	 * @throws InvalidIndexException If there is a problem with the <code>TableOfContents</code> 
	 */
	protected static String uriKey(URI uri) throws InvalidIndexException, URISyntaxException {
		return uri.toString().toLowerCase();
	}

}
