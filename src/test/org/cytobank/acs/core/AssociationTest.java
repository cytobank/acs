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

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.cytobank.TestUtils;
import org.cytobank.acs.core.exceptions.InvalidAssociationException;

public class AssociationTest {

	static final String MY_NEW_FILE = "/my_new_file";
	static final String MY_NEW_FILE_PATH = "file://" + MY_NEW_FILE;

	static final String MY_NEW_FILE2 = "/my_new_file2";
	static final String MY_NEW_FILE_PATH2 = "file://" + MY_NEW_FILE2;

	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testCreateAssociation() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		File newFile  = TestUtils.testFile();
		File newFile2 = TestUtils.testFile();

		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		
		FileResourceIdentifier associatedLocalFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH2, newFile2);

		assertTrue("FileResourceIdentifier xml element should have no associations when created", fileResourceIdentifier.element.getElementsByTagName(Constants.ASSOCIATED_ELEMENT).getLength() == 0);
		assertTrue("FileResourceIdentifier xml element should have no associations when created", associatedLocalFileResourceIdentifier.element.getElementsByTagName(Constants.ASSOCIATED_ELEMENT).getLength() == 0);

		
		// Create an association from localFileResourceIdentifier to associatedLocalFileResourceIdentifier
		Association association = fileResourceIdentifier.createAssociation(associatedLocalFileResourceIdentifier, RelationshipTypes.ANALYSIS_DESCRIPTION);
		
		assertEquals("Association.getAssociatedTo() should return the FileResourceIdentifier that the association it referring to", associatedLocalFileResourceIdentifier, association.getAssociatedTo());
		assertTrue("Association.isAnalysisDescription() should return true if the association relationship type was set to RelationshipTypes.ANALYSIS_DESCRIPTION", association.isAnalysisDescription());

		assertTrue("FileResourceIdentifier xml element should have an associations when created after an association was create created", fileResourceIdentifier.element.getElementsByTagName(Constants.ASSOCIATED_ELEMENT).getLength() == 1);
		assertTrue("The FileResourceIdentifier being associated with's xml element should have no associations when created after an association was create created", associatedLocalFileResourceIdentifier.element.getElementsByTagName(Constants.ASSOCIATED_ELEMENT).getLength() == 0);

		FileResourceIdentifier tempFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier);
		Association[] tempFileResourceIdentifierAssociations = tempFileResourceIdentifier.getAssociations();
		
		assertTrue(tempFileResourceIdentifierAssociations.length == 1);
		
		Association tempFileResourceIdentifierAssociation = tempFileResourceIdentifierAssociations[0];
				
		assertEquals("The RelationshipType of the saved association should be retained.", RelationshipTypes.ANALYSIS_DESCRIPTION, tempFileResourceIdentifierAssociation.getRelationship());		
	
		assertEquals("Association.getAssociatedUriTo should return the URI to the FileResourceIdentifier that the association is to", new URI(MY_NEW_FILE_PATH2), tempFileResourceIdentifierAssociation.getAssociatedUriTo());
	
		FileResourceIdentifier associatedTempFileResourceIdentifier = tempFileResourceIdentifierAssociation.getAssociatedTo();
		assertEquals("Association.getAssociatedTo() should return the FileResourceIdentifier that the Accoiation points to", new URI(MY_NEW_FILE_PATH2), associatedTempFileResourceIdentifier.getUri());
	}

	@Test
	public void testCreateAssociationWithBadRelationship() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		File newFile  = TestUtils.testFile();
		File newFile2 = TestUtils.testFile();

		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		
		FileResourceIdentifier associatedLocalFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH2, newFile2);

		assertTrue("FileResourceIdentifier xml element should have no associations when created", fileResourceIdentifier.element.getElementsByTagName(Constants.ASSOCIATED_ELEMENT).getLength() == 0);
		
		// Create an association from localFileResourceIdentifier to associatedLocalFileResourceIdentifier
		try {
			fileResourceIdentifier.createAssociation(associatedLocalFileResourceIdentifier, "");
			
			fail("An association with an empty relationship should throw an InvalidAssociationException");
		} catch (InvalidAssociationException expectedException) {
		}
			
		assertTrue("FileResourceIdentifier xml element should still have no associations when created after a failed association create attempt", fileResourceIdentifier.element.getElementsByTagName(Constants.ASSOCIATED_ELEMENT).getLength() == 0);
	}
	
	@Test
	public void testRelationshipTypes() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		
		TableOfContents tableOfContents = acsV2.createNextTableOfContents(); 

		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.GATING_DESCRIPTION).isGatingDescription());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.COMPENSATION_DESCRIPTION).isCompensationDescription());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.COMPENSATED_VERSION).isCompensatedVersion());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.CLASSIFICATION_RESULTS).isClassificationResults());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.PROJECT_WORKSPACE).isProjectWorkspace());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.INSTRUMENTATION_SETTINGS_DESCRIPTION).isInstrumentationSettingsDescription());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.SAMPLE_SPECIMEN_DESCRIPTION).isSampleSpecimenDescription());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.ANALYSIS_DESCRIPTION).isAnalysisDescription());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.RESULTS_DESCRIPTION).isResultsDescription());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.RELATED_PUBLICATION).isRelatedPublication());
		assertTrue(setupAssociation(tableOfContents, RelationshipTypes.DIGITAL_SIGNATURE).isDigitalSignature());

		
		String someOtherRelationship = "foo bar baz";
		Association someOtherAssociation = setupAssociation(tableOfContents, someOtherRelationship);
		
		assertFalse(someOtherAssociation.isGatingDescription());
		assertFalse(someOtherAssociation.isCompensationDescription());
		assertFalse(someOtherAssociation.isCompensatedVersion());
		assertFalse(someOtherAssociation.isClassificationResults());
		assertFalse(someOtherAssociation.isProjectWorkspace());
		assertFalse(someOtherAssociation.isInstrumentationSettingsDescription());
		assertFalse(someOtherAssociation.isSampleSpecimenDescription());
		assertFalse(someOtherAssociation.isAnalysisDescription());
		assertFalse(someOtherAssociation.isResultsDescription());
		assertFalse(someOtherAssociation.isRelatedPublication());
		assertFalse(someOtherAssociation.isDigitalSignature());
	}
	
	public Association setupAssociation(TableOfContents tableOfContents, String relationshipType) throws Exception {
		File newFile  = TestUtils.testFile();
		File newFile2 = TestUtils.testFile();

		// URIs may not have spaces in them
		String relationshipTypeEscaped = relationshipType.replaceAll(" ", "+");
		
		FileResourceIdentifier fileResourceIdentifier = tableOfContents.createFileResourceIdentifier(MY_NEW_FILE_PATH+relationshipTypeEscaped, newFile);
		FileResourceIdentifier associatedLocalFileResourceIdentifier = tableOfContents.createFileResourceIdentifier(MY_NEW_FILE_PATH2+relationshipTypeEscaped, newFile2);
		
		return fileResourceIdentifier.createAssociation(associatedLocalFileResourceIdentifier, relationshipType);
	}
	
	@After
	public void tearDown() throws Exception {
	}
}
