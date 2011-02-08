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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.cytobank.acs.core.RelationshipTypes;

public class RelationshipTypesTest {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testConstantTests() throws Exception {
		assertTrue(RelationshipTypes.isGatingDescription(RelationshipTypes.GATING_DESCRIPTION));
		assertTrue(RelationshipTypes.isCompensationDescription(RelationshipTypes.COMPENSATION_DESCRIPTION));
		assertTrue(RelationshipTypes.isCompensatedVersion(RelationshipTypes.COMPENSATED_VERSION));		
		assertTrue(RelationshipTypes.isClassificationResults(RelationshipTypes.CLASSIFICATION_RESULTS));		
		assertTrue(RelationshipTypes.isProjectWorkspace(RelationshipTypes.PROJECT_WORKSPACE));
		assertTrue(RelationshipTypes.isInstrumentationSettingsDescription(RelationshipTypes.INSTRUMENTATION_SETTINGS_DESCRIPTION));
		assertTrue(RelationshipTypes.isSampleSpecimenDescription(RelationshipTypes.SAMPLE_SPECIMEN_DESCRIPTION));
		assertTrue(RelationshipTypes.isAnalysisDescription(RelationshipTypes.ANALYSIS_DESCRIPTION));
		assertTrue(RelationshipTypes.isResultsDescription(RelationshipTypes.RESULTS_DESCRIPTION));
		assertTrue(RelationshipTypes.isRelatedPublication(RelationshipTypes.RELATED_PUBLICATION));
		assertTrue(RelationshipTypes.isDigitalSignature(RelationshipTypes.DIGITAL_SIGNATURE));
		
		String someText = "some text";
		assertFalse(RelationshipTypes.isGatingDescription(someText));
		assertFalse(RelationshipTypes.isCompensationDescription(someText));
		assertFalse(RelationshipTypes.isCompensatedVersion(someText));		
		assertFalse(RelationshipTypes.isClassificationResults(someText));		
		assertFalse(RelationshipTypes.isProjectWorkspace(someText));
		assertFalse(RelationshipTypes.isInstrumentationSettingsDescription(someText));
		assertFalse(RelationshipTypes.isSampleSpecimenDescription(someText));
		assertFalse(RelationshipTypes.isAnalysisDescription(someText));
		assertFalse(RelationshipTypes.isResultsDescription(someText));
		assertFalse(RelationshipTypes.isRelatedPublication(someText));
		assertFalse(RelationshipTypes.isDigitalSignature(someText));
	}
	
	@Test
	public void testTestAssociationType() throws Exception {
		assertTrue("Tests should be case insensitive", RelationshipTypes.testAssociationType("abc", "aBc"));
		assertTrue("Tests should ignore forward white spaces", RelationshipTypes.testAssociationType("abc", " abc"));
		assertTrue("Tests should ignore aft white spaces", RelationshipTypes.testAssociationType("abc", "abc "));
		assertTrue("Tests should ignore forward and aft white spaces", RelationshipTypes.testAssociationType("abc", " abc "));
		assertFalse("Tests should not ignore mid white spaces", RelationshipTypes.testAssociationType("abc", "ab c"));

		assertFalse("Tests should return false on nulls", RelationshipTypes.testAssociationType("abc", null));
	}

	@After
	public void tearDown() throws Exception {
	}
	
}
