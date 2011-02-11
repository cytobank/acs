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
import java.net.URI;

import org.junit.*;

import org.cytobank.TestUtils;
import org.cytobank.acs.core.ACS;
import org.cytobank.acs.core.FileResourceIdentifier;
import org.cytobank.acs.core.TableOfContents;

import static org.junit.Assert.*;

public class ACSTest {

	@Before
	public void setUp() throws Exception {		
	}

	@Test
	public void tempFile() throws Exception {
		ACS acs = new ACS();
		File file = acs.tempFile();
		assertNotNull(file);
		assertTrue(file.exists());
	}
	
	@Test
	public void contructAcsInstance() throws Exception {
		ACS acs = new ACS();
		assertEquals((Integer) 0, (Integer) acs.getCurrentVersion());
		TableOfContents tableOfContents = acs.getTableOfContents();
		assertNull("A new ACS instance should return null on ACS.getTableOfContents()", tableOfContents);
	}
	
	@Test
	public void testCreateNewTableOfContents() throws Exception {
		ACS acs = new ACS();
		TableOfContents tableOfContents = acs.getTableOfContents();
		
		assertEquals((Integer) 0, (Integer) acs.getCurrentVersion());
		assertNull("A new ACS instance should return null on ACS.getTableOfContents()", tableOfContents);
		
		TableOfContents nextTableOfContents = acs.createNextTableOfContents();

		assertEquals("After calling ACS.createNextTableOfContents() on a new ACS, the first version should be 1", (Integer) 1, (Integer) acs.getCurrentVersion());
		assertEquals("Calling TableOfContents.getVersion() after the first ACS.createNextTableOfContents() call, the version should be 1", (Integer) 1, (Integer) nextTableOfContents.getVersion());
	}

	@Test
	public void testCreateNewTableOfContentsFromExistingAcsFile() throws Exception {
		ACS acsV2 = TestUtils.getTestAcsInstance(TestUtils.U937_ACS_TEST_FILE2);

		TableOfContents tableOfContentsV1 = acsV2.getTableOfContents(1);
		
		assertEquals((Integer) 2, (Integer) acsV2.getCurrentVersion());
		assertEquals((Integer) 1, (Integer) tableOfContentsV1.getVersion());
		assertEquals((Integer) 1, (Integer) tableOfContentsV1.getNumberOfFileResourceIdentifiers());
		
		TableOfContents tableOfContentsV2 = acsV2.getTableOfContents(2);
		assertEquals((Integer) 2, (Integer) tableOfContentsV2.getVersion());
		assertEquals((Integer) 15, (Integer) tableOfContentsV2.getNumberOfFileResourceIdentifiers());

		TableOfContents tableOfContentsV3 = acsV2.createNextTableOfContents();
		assertEquals((Integer) 3, (Integer) tableOfContentsV3.getVersion());
		assertEquals((Integer) 15, (Integer) tableOfContentsV3.getNumberOfFileResourceIdentifiers());
	}
	
	@Test
	public void testGetTableOfContents() throws Exception {
		ACS acsV1 = TestUtils.getAcsV1();
		ACS acsV2 = TestUtils.getAcsV2();

		assertEquals((Integer) 1, (Integer) acsV1.getCurrentVersion());
		TableOfContents tableOfContentsV1 = acsV1.getTableOfContents();
		
		assertNotNull(tableOfContentsV1);
		assertEquals((Integer) 1, (Integer) tableOfContentsV1.getVersion());
		assertEquals((Integer) 1, (Integer) tableOfContentsV1.getNumberOfFileResourceIdentifiers());

		assertEquals((Integer) 2, (Integer) acsV2.getCurrentVersion());
		TableOfContents tableOfContents_v2 = acsV2.getTableOfContents();
		assertNotNull(tableOfContents_v2);
		assertEquals((Integer) 2, (Integer) tableOfContents_v2.getVersion());
		assertEquals((Integer) 15, (Integer) tableOfContents_v2.getNumberOfFileResourceIdentifiers());
	}
	
	@Test
	public void updateAndSave() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		File newFile = TestUtils.testFile();
		String newFileMd5sum = TestUtils.md5sum(newFile);
		
		
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		String newFileName = "file:///my_new_file";
		
		newToc.createFileResourceIdentifier(new URI(newFileName), newFile);

		File newAcsFile = File.createTempFile("AcsTest", "acs");
		
		acsV2.writeAcsContainer(newAcsFile);
		
		ACS newAcs = new ACS(newAcsFile);
				
		assertEquals((Integer) 3, (Integer) newAcs.getCurrentVersion());
		
		TableOfContents tableOfContents_v3 = newAcs.getTableOfContents();
		
		assertNotNull(tableOfContents_v3);
		assertEquals((Integer) 3, (Integer) tableOfContents_v3.getVersion());
		assertEquals((Integer) 16, (Integer) tableOfContents_v3.getNumberOfFileResourceIdentifiers());
		
		File rewrittenFile = File.createTempFile("rewritten_file", "tmp");
		
		FileResourceIdentifier fileResourceIdentifier = tableOfContents_v3.getFileResourceIdentifierByUri(newFileName);
		fileResourceIdentifier.writeRepresentedFile(rewrittenFile);
		
		String rewrittenFileMd5sum = TestUtils.md5sum(rewrittenFile);
		
		assertEquals(newFileMd5sum, rewrittenFileMd5sum);
		 
		
	}
	
	@After
	public void tearDown() throws Exception {
	}	
}
