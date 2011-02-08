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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.cytobank.acs.core.exceptions.InvalidAssociationException;
import org.cytobank.acs.core.exceptions.InvalidFileResourceUriSchemeException;
import org.cytobank.acs.core.exceptions.InvalidIndexException;
import org.w3c.dom.Element;

/**
 * This class represents a file association between one <code>FileResourceIdentifier</code> and another.
 * 
 * @author Chad Rosenberg <chad@cytobank.org>
 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
 */
public class Association extends ElementWrapper {
	/** The error message to put into <code>InvalidAssociationException</code> if the relationship field is blank or null */
	public static final String BLANK_RELATIONSHIP_ERROR = "Association relationship may not be blank."; 
	
	/** The <code>TableOfContents</code> that owns this <code>Association</code> */
	protected TableOfContents tableOfContents;
	
	/**
	 * Creates an <code>Association</code> instance that represents an Association between one <code>FileResourceIdentifier</code> and another.  This method will not add the new <code>Association</code>
	 * to associations list found in the <code>fileResourceFrom</code> <code>FileResourceIdentifier</code>.
	 * <p>
	 * NOTE: The preferred way to create an <code>Association</code> is to use the {@link FileResourceIdentifier#createAssociation(FileResourceIdentifier, String)} method.
	 * 
	 * @param fileResourceFrom the <code>FileResourceIdentifier</code> the <code>Association</code> will created for
	 * @param withFileResource the <code>FileResourceIdentifier</code> the <code>Association</code> will be pointing to
	 * @param relationship the relationship between the two
	 * @throws InvalidAssociationException if there is an invalid association exception 
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code>
	 * @throws URISyntaxException If there is a problem the <code>URI</code>
	 * @throws InvalidFileResourceUriSchemeException if a URI scheme is used that is now allowed according to the ACS specification.
	 * @see FileResourceIdentifier#createAssociation
	 */
	public Association(FileResourceIdentifier fileResourceFrom, FileResourceIdentifier withFileResource, String relationship) throws InvalidAssociationException, InvalidIndexException, URISyntaxException, InvalidFileResourceUriSchemeException {
		this.element = fileResourceFrom.createElement(Constants.ASSOCIATED_ELEMENT);
		this.tableOfContents = fileResourceFrom.getTableOfContents();

		// The element has to be set first or this will throw an NullPointerException
		// setRelationship will handle the checking of a blank/null relationship
		setRelationship(relationship);

		// Get the uri to allow an exception to be raised first, if there is a problem
		setAssociatedUriTo(withFileResource.getUri());
	}

	/**
	 * Creates an <code>Association</code> instance that represents an Association between one <code>FileResourceIdentifier</code> to another from an
	 * xml <code>org.w3c.dom.Element</code>
	 * 
	 * @param tableOfContents the <code>TableOfContents</code> the <code>Association</code> created will be owned by
	 * @param associationElement the xml <code>org.w3c.dom.Element</code> the <code>Association</code> will be created from
	 * @throws InvalidAssociationException if there is an invalid association exception 
	 */
	protected Association(TableOfContents tableOfContents, Element associationElement) throws InvalidAssociationException {
		this.tableOfContents = tableOfContents;
		this.element = associationElement;
		if (StringUtils.isBlank(getRelationship()))
			throw new InvalidAssociationException(BLANK_RELATIONSHIP_ERROR);
	}
	
	/**
	 * Gets the <code>URI</code> representing the <code>FileResourceIdentifier</code> that this association points to.
	 * 
	 * @return the <code>URI</code> representing the <code>FileResourceIdentifier</code> that this association points to
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code>
	 * @throws URISyntaxException If there is a problem the <code>URI</code>
	 */
	public URI getAssociatedUriTo() throws InvalidIndexException, URISyntaxException {
		return new URI(getAttributeValue(Constants.WITH_ATTRIBUTE));
	}
	
	/**
	 * Sets the <code>URI</code> representing the <code>FileResourceIdentifier</code> that this association points to.
	 * 
	 * @param uri the <code>URI</code> to set
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code>
	 * @throws InvalidFileResourceUriSchemeException if a URI scheme is used that is now allowed according to the ACS specification.
	 */
	public void setAssociatedUriTo(URI uri) throws InvalidIndexException, InvalidFileResourceUriSchemeException {
		if (!FileResourceIdentifier.isUriSchemeAllowed(uri))
			throw new InvalidFileResourceUriSchemeException("The uri " + uri + "contains an shema that is not allowed");
		
		setAttributeValue(Constants.WITH_ATTRIBUTE, uri.toString());
	}
	
	/**
	 * Gets the relationship to the <code>FileResourceIdentifier</code> that this association points to.
	 * 
	 * @return a <code>String</code> representing the relationship to the <code>FileResourceIdentifier</code> that this association points to
	 */
	public String getRelationship() {
		return getAttributeValue(Constants.RELATIONSHIP_ATTRIBUTE);
	}

	/**
	 * Sets the relationship to the <code>FileResourceIdentifier</code> that this association points to.
	 * 
	 * @param relationship A <code>String</code> describing the relationship.  A constant out of <code>RelationshipTypes</code>, which represent the known association types
	 * from the ACS specification, should be considered before using a custom type.  An <code>InvalidAssociationException</code> will be thrown if this parameter is empty or null.
	 */
	public void setRelationship(String relationship) throws InvalidAssociationException {
		if (StringUtils.isBlank(relationship))
			throw new InvalidAssociationException(BLANK_RELATIONSHIP_ERROR);
		
		relationship = StringUtils.strip(relationship.toLowerCase());

		setAttributeValue(Constants.RELATIONSHIP_ATTRIBUTE, relationship);
	}
	
	/**
	 * Gets the <code>FileResourceIdentifier</code> that this <code>Association</code> points to.
	 * 
	 * @return the <code>FileResourceIdentifier</code> that this <code>Association</code> points to, or <code>null</code> if it cannot be found.
	 * @throws InvalidAssociationException if there is an invalid association exception
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code>
	 * @throws URISyntaxException If there is a problem the <code>URI</code>
	 */
	public FileResourceIdentifier getAssociatedTo() throws InvalidAssociationException, InvalidIndexException, URISyntaxException {
		if (tableOfContents == null)
			throw new InvalidAssociationException("Association has no TableOfContents");
		
		return tableOfContents.getFileResourceIdentifierByUri(getAssociatedUriTo());
	}
	
	/**
	 * Returns <code>true</code> if this relationship represents a gating description.
	 *  
	 * @return <code>true</code> if this relationship represents a gating description, <code>false</code> otherwise
	 * @see RelationshipTypes#isGatingDescription
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public boolean isGatingDescription() {
		return RelationshipTypes.isGatingDescription(getRelationship());
	}

	/**
	 * Returns <code>true</code> if this relationship represents a compensation description.
	 *  
	 * @return <code>true</code> if this relationship represents a compensation description, <code>false</code> otherwise
	 * @see RelationshipTypes#isCompensationDescription
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public boolean isCompensationDescription() {
		return RelationshipTypes.isCompensationDescription(getRelationship());
	}

	/**
	 * Returns <code>true</code> if this relationship represents a compensated version.
	 *  
	 * @return <code>true</code> if this relationship represents a compensated version, <code>false</code> otherwise
	 * @see RelationshipTypes#isCompensatedVersion
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public boolean isCompensatedVersion() {
		return RelationshipTypes.isCompensatedVersion(getRelationship());
	}
	
	/**
	 * Returns <code>true</code> if this relationship represents classification results.
	 *  
	 * @return <code>true</code> if this relationship represents classification results, <code>false</code> otherwise
	 * @see RelationshipTypes#isClassificationResults
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */	
	public boolean isClassificationResults() {
		return RelationshipTypes.isClassificationResults(getRelationship());
	}

	/**
	 * Returns <code>true</code> if this relationship represents a project workspace.
	 *  
	 * @return <code>true</code> if this relationship represents a project workspace, <code>false</code> otherwise
	 * @see RelationshipTypes#isProjectWorkspace
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */	
	public boolean isProjectWorkspace() {
		return RelationshipTypes.isProjectWorkspace(getRelationship());
	}
	
	/**
	 * Returns <code>true</code> if this relationship represents an instrumentation settings description.
	 *  
	 * @return <code>true</code> if this relationship represents an instrumentation settings description, <code>false</code> otherwise
	 * @see RelationshipTypes#isInstrumentationSettingsDescription
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */	
	public boolean isInstrumentationSettingsDescription() {
		return RelationshipTypes.isInstrumentationSettingsDescription(getRelationship());
	}

	/**
	 * Returns <code>true</code> if this relationship represents a sample description.
	 *  
	 * @return <code>true</code> if this relationship represents a sample description, <code>false</code> otherwise
	 * @see RelationshipTypes#isSampleSpecimenDescription
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */	
	public boolean isSampleSpecimenDescription() {
		return RelationshipTypes.isSampleSpecimenDescription(getRelationship());
	}

	/**
	 * Returns <code>true</code> if this relationship represents an analysis description.
	 *  
	 * @return <code>true</code> if this relationship represents an analysis description, <code>false</code> otherwise
	 * @see RelationshipTypes#isAnalysisDescription
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */	
	public boolean isAnalysisDescription() {
		return RelationshipTypes.isAnalysisDescription(getRelationship());
	}
	
	/**
	 * Returns <code>true</code> if this relationship represents a results description.
	 *  
	 * @return <code>true</code> if this relationship represents a results description, <code>false</code> otherwise
	 * @see RelationshipTypes#isResultsDescription
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */	
	public boolean isResultsDescription() {
		return RelationshipTypes.isResultsDescription(getRelationship());
	}
	
	/**
	 * Returns <code>true</code> if this relationship represents a related publication.
	 *  
	 * @return <code>true</code> if this relationship represents a related publication, <code>false</code> otherwise
	 * @see RelationshipTypes#isRelatedPublication
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */	
	public boolean isRelatedPublication() {
		return RelationshipTypes.isRelatedPublication(getRelationship());
	}
	
	/**
	 * Returns <code>true</code> if this relationship represents a digital signature.
	 *  
	 * @return <code>true</code> if this relationship represents a digital signature, <code>false</code> otherwise
	 * @see RelationshipTypes#isDigitalSignature
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */	
	public boolean isDigitalSignature() {
		return RelationshipTypes.isDigitalSignature(getRelationship());
	}
		
}
