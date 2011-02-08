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

import org.cytobank.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class AdditionalInfoTest {
	static final String MY_NEW_FILE = "/my_new_file";
	static final String MY_NEW_FILE_PATH = "file://" + MY_NEW_FILE;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSetAndGetInfo() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		
		String testText = "This is my additional info";
		
		AdditionalInfo additionalInfo = fileResourceIdentifier.addAdditionalInfo(testText);
		assertEquals("AdditionalInfo.getInfo should return the set additional info text", testText, additionalInfo.getInfo());
		
		String newText = "This is my new text";
		additionalInfo.setInfo(newText);
		assertEquals("AdditionalInfo.getInfo should return the set additional info text", newText, additionalInfo.getInfo());
		
		FileResourceIdentifier reloadedFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier);
		AdditionalInfo reloadedAdditionalInfo = reloadedFileResourceIdentifier.getAdditionalInfo()[0];

		assertEquals("AdditionalInfo.getInfo should return the set additional info text", newText, reloadedAdditionalInfo.getInfo());
	}
	
	@After
	public void tearDown() throws Exception {
	}


}
