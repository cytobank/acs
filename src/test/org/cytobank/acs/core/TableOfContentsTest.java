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
import java.net.URI;

import org.junit.*;

import org.apache.commons.lang.StringUtils;
import org.cytobank.TestUtils;
import org.cytobank.acs.core.ACS;
import org.cytobank.acs.core.FileResourceIdentifier;
import org.cytobank.acs.core.TableOfContents;
import org.cytobank.acs.core.exceptions.DuplicateFileResourceIdentifierException;
import org.cytobank.acs.util.FileUtils;

import static org.junit.Assert.*;

public class TableOfContentsTest {	
	static final String MY_NEW_FILE = "/my_new_file";
	static final String MY_NEW_FILE_PATH = "file://" + MY_NEW_FILE;
	
	static final String MY_NEW_FILE2 = "/my_new_file2";
	static final String MY_NEW_FILE_PATH2 = "file://" + MY_NEW_FILE2;

	static final String MY_NEW_FILE3 = "/my_new_file3";
	static final String MY_NEW_FILE_PATH3 = "file://" + MY_NEW_FILE3;

	
	static final String[] EXPECTED_U937_URIS = new String[]{
			"file:///20071001-u937.001",
			"file:///20071001-u937.002",
			"file:///20071001-u937.003",
			"file:///20071001-u937.004",
			"file:///20071001-u937.005",
			"file:///20071001-u937.006",
			"file:///20071001-u937.007",
			"file:///20071001-u937.008",
			"file:///20071001-u937.009",
			"file:///20071001-u937.010",
			"file:///20071001-u937.011",
			"file:///20071001-u937.012",
			"file:///20071001-u937.013",
			"file:///20071001-u937.014",
			"file:///20071001-u937.015"
	};
	
	static final String[] EXPECTED_U937_MD5SUMS = new String[] {
		"0313d1d7516447b8053f295ee6972762",
		"d5d18ee16681ac45a43a36a4ffbc0d46",
		"5143e4b425c8c96a5f006fa57dfca77b",
		"9348ff09f49eb6e297d59e1ae67948b9",
		"64dc4461392c032c51f650cd46ced6e0",
		"d3b17b77089022a7193d7d47c67d22fa",
		"e7bae9844498f748acb87b65556ab48e",
		"71ef468cf3ef45e1a8b22df218bad77f",
		"8865081861efd8b5deb8e2efbcbfa373",
		"41e9c36d0ebdd6acc254416cea7aa2cd",
		"49067b3616cd8856819f219d7350cb3d",
		"8b59dd3ac1b4d6c363e3be2a462eb0bb",
		"408383ab69abe25ca9730684abd047bf",
		"a2da517516e3957fbd15c2dac1162855",
		"123ef67179fb7b182c62673ce08868e5"
	};

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testCreateFileResourceIdentifierAndCreateFcsFileResourceIdentifier() throws Exception {
		ACS acs = new ACS();
		File newFile1 = TestUtils.testFile();
		
		String stringUri1 = "file:///testFile";
		
		TableOfContents tableOfContents = acs.createNextTableOfContents();
		tableOfContents.createFileResourceIdentifier(stringUri1, newFile1.getPath());
		
		FileResourceIdentifier fileResourceIdentifier = tableOfContents.getFileResourceIdentifierByUri(stringUri1);
		
		assertNotNull("TableOfContents.createFileResourceIdentifier should be able to create a FileResourceIdentifier with a String uri and String path", fileResourceIdentifier);
		assertEquals("TableOfContents.createFileResourceIdentifier should be able to create a FileResourceIdentifier with a String uri and String path that returns the correct URI", stringUri1, fileResourceIdentifier.getUri().toString());

		
		File newFile2 = TestUtils.testFile();
		String stringUri2 = stringUri1 + "2";

		tableOfContents.createFileResourceIdentifier(new URI(stringUri2), newFile2);
		
		FileResourceIdentifier fileResourceIdentifier2 = tableOfContents.getFileResourceIdentifierByUri(stringUri2);
		
		assertNotNull("TableOfContents.createFileResourceIdentifier should be able to create a FileResourceIdentifier with a URI uri and File path", fileResourceIdentifier2);
		assertEquals("TableOfContents.createFileResourceIdentifier should be able to create a FileResourceIdentifier with a URI uri and File path that returns the correct URI", stringUri2, fileResourceIdentifier2.getUri().toString());

		
		File newFile3 = TestUtils.testFile();
		
		String mime      = "mime";
		String stringUri3 = stringUri1 + "3";
		
		tableOfContents.createFileResourceIdentifier(stringUri3, newFile3.getPath(), mime);
		
		FileResourceIdentifier fileResourceIdentifier3 = tableOfContents.getFileResourceIdentifierByUri(stringUri3);
		
		assertNotNull("TableOfContents.createFileResourceIdentifier should be able to create a FileResourceIdentifier with a String uri and String path and MIME", fileResourceIdentifier3);
		assertEquals("TableOfContents.createFileResourceIdentifier should be able to create a FileResourceIdentifier with a String uri and String path and MIME that returns the correct URI", stringUri3, fileResourceIdentifier3.getUri().toString());

		
		File newFile4 = TestUtils.testFile();
		String stringUri4 = stringUri1 + "4";

		tableOfContents.createFileResourceIdentifier(new URI(stringUri4), newFile4, mime);
		
		FileResourceIdentifier fileResourceIdentifier4 = tableOfContents.getFileResourceIdentifierByUri(stringUri4);
		
		assertNotNull("TableOfContents.createFileResourceIdentifier should be able to create a FileResourceIdentifier with a URI uri and File path", fileResourceIdentifier4);
		assertEquals("TableOfContents.createFileResourceIdentifier should be able to create a FileResourceIdentifier with a URI uri and File path that returns the correct URI", stringUri4, fileResourceIdentifier4.getUri().toString());

		
		// Test createFcsFileResourceIdentifier
		File newFile5 = TestUtils.testFile();
		
		String stringUri5 = stringUri1 + "5";
		
		tableOfContents.createFcsFileResourceIdentifier(stringUri5, newFile5.getPath());
		
		FileResourceIdentifier fileResourceIdentifier5 = tableOfContents.getFileResourceIdentifierByUri(stringUri5);
		
		assertNotNull("TableOfContents.createFcsFileResourceIdentifier should be able to create a FileResourceIdentifier with a String uri and String path", fileResourceIdentifier5);
		assertEquals("TableOfContents.createFcsFileResourceIdentifier should be able to create a FileResourceIdentifier with a String uri and String path that returns the correct URI", stringUri5, fileResourceIdentifier5.getUri().toString());

		
		File newFile6 = TestUtils.testFile();
		String stringUri6 = stringUri1 + "6";

		tableOfContents.createFcsFileResourceIdentifier(new URI(stringUri6), newFile6);
		
		FileResourceIdentifier fileResourceIdentifier6 = tableOfContents.getFileResourceIdentifierByUri(stringUri6);
		
		assertNotNull("TableOfContents.createFcsFileResourceIdentifier should be able to create a FileResourceIdentifier with a URI uri and File path", fileResourceIdentifier6);
		assertEquals("TableOfContents.createFcsFileResourceIdentifier should be able to create a FileResourceIdentifier with a URI uri and File path that returns the correct URI", stringUri6, fileResourceIdentifier6.getUri().toString());

		TableOfContents reloadedTableOfContents = TestUtils.writeOutTableOfContentsAndReload(tableOfContents);
		assertTrue(StringUtils.isNotBlank(reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri1).writeRepresentedFileToString()));
		assertTrue(StringUtils.isNotBlank(reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri2).writeRepresentedFileToString()));
		assertTrue(StringUtils.isNotBlank(reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri3).writeRepresentedFileToString()));
		assertTrue(StringUtils.isNotBlank(reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri4).writeRepresentedFileToString()));
		assertTrue(StringUtils.isNotBlank(reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri5).writeRepresentedFileToString()));
		assertTrue(StringUtils.isNotBlank(reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri6).writeRepresentedFileToString()));
				
		assertEquals("The contents of the reloaded tableOfContents should match", FileUtils.fileToString(newFile1), reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri1).writeRepresentedFileToString());
		assertEquals("The contents of the reloaded tableOfContents should match", FileUtils.fileToString(newFile2), reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri2).writeRepresentedFileToString());
		assertEquals("The contents of the reloaded tableOfContents should match", FileUtils.fileToString(newFile3), reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri3).writeRepresentedFileToString());
		assertEquals("The contents of the reloaded tableOfContents should match", FileUtils.fileToString(newFile4), reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri4).writeRepresentedFileToString());
		assertEquals("The contents of the reloaded tableOfContents should match", FileUtils.fileToString(newFile5), reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri5).writeRepresentedFileToString());
		assertEquals("The contents of the reloaded tableOfContents should match", FileUtils.fileToString(newFile6), reloadedTableOfContents.getFileResourceIdentifierByUri(stringUri6).writeRepresentedFileToString());		
	}
	
	@Test
	public void testGetFileResources() throws Exception {
		TableOfContents tableOfContents = TestUtils.getAcsV2().getTableOfContents();
		FileResourceIdentifier[] fileResources = tableOfContents.getFileResourceIdentifiers();
		assertNotNull(fileResources);
		
		assertEquals((Integer) 15, (Integer) fileResources.length);
		
		for (int i=0; i<15; i++) {
			FileResourceIdentifier fileResource = fileResources[i];
			
			assertNotNull(fileResource);
			assertEquals(EXPECTED_U937_URIS[i], fileResource.getUri().toString());
			assertEquals("Checking mime type for " + fileResource.getUri().toString(), "application/vnd.isac.fcs", fileResource.getMimeType());
			
			File targetFile = File.createTempFile("fcs", ".tmp");
			fileResource.writeRepresentedFile(targetFile);
			String md5sum = TestUtils.md5sum(targetFile);
			
			assertEquals("Checking MD5sum for " + fileResource.getUri().toString(), EXPECTED_U937_MD5SUMS[i], md5sum);
		}
	}
	
	@Test
	public void getFcsFiles() throws Exception {
		TableOfContents tableOfContents = TestUtils.getAcsV2().getTableOfContents();
		FileResourceIdentifier[] fileResources = tableOfContents.getFcsFiles();
		
		for (int i=0; i<EXPECTED_U937_URIS.length; i++) {
			assertEquals(EXPECTED_U937_URIS[i], fileResources[i].getUri().toString());
		}
	}
	
	@Test
	public void addFileResourceWithInputStream() throws Exception {
		File newFile = TestUtils.testFile();

		ACS acsV2 = TestUtils.getAcsV2();
		
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		String newFileName = "file:///my_new_file";
		
		FileInputStream fileInputStream = new FileInputStream(newFile);
		
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(new URI(newFileName), fileInputStream);
		
		assertTrue("Any added fileResourceIdentifier created with a InputStream should return true on hasSourceInputStream", fileResourceIdentifier.hasSourceInputStream());
		assertFalse("Any added fileResourceIdentifier created with a InputStream should return false on hasSourceFile", fileResourceIdentifier.hasSourceFile());		
		
		assertEquals((Integer) 3, (Integer) acsV2.getCurrentVersion());
		
		assertTrue("my_new_file should have been added to the table of content's xml", newToc.toXml().indexOf("<toc:file toc:URI=\"file:///my_new_file\"/>") > -1);
	}

	@Test
	public void addFileResourceWithoutUrlFilePrefix() throws Exception {
		File newFile = TestUtils.testFile();
		
		ACS acsV2 = TestUtils.getAcsV2();

		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		String newFileName = "/my_new_file";
		
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(new URI(newFileName), newFile);
		
		assertTrue("Any added fileResourceIdentifier should return true on hasSourceInputStream", fileResourceIdentifier.hasSourceInputStream());

		assertEquals((Integer) 3, (Integer) acsV2.getCurrentVersion());
		
		// TODO Check this against the dom
		assertTrue("my_new_file should have been added to the table of content's xml", newToc.toXml().indexOf("<toc:file toc:URI=\"file:///my_new_file\"/>") != -1);
	}

	@Test
	public void addFileResourceWithMimeType() throws Exception {
		File newFile = TestUtils.testFile();
		
		ACS acsV2 = TestUtils.getAcsV2();
		
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		String newFileName = "file:///my_new_file";
		
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(new URI(newFileName), newFile, "text/plain");
		
		assertTrue("Any added fileResourceIdentifier should return true on hasSourceInputStream", fileResourceIdentifier.hasSourceInputStream());
		
		assertEquals((Integer) 3, (Integer) acsV2.getCurrentVersion());

		assertTrue("my_new_file should have been added to the table of content's xml", newToc.toXml().indexOf("<toc:file toc:URI=\"file:///my_new_file\" toc:mimeType=\"text/plain\"/>") != -1);
		
		assertEquals("The MIME type of a saved FileResourceIdentifier should be the same after a reload", "text/plain", TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier).getMimeType());
		
	}
	
	@Test
	public void addFileResourceWithFileSource() throws Exception {
		File newFile = TestUtils.testFile();

		ACS acsV2 = TestUtils.getAcsV2();
		
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		String newFileName = "file:///my_new_file";
			
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(new URI(newFileName), newFile);
		
		assertFalse("Any added fileResourceIdentifier created with a File should return false on hasSourceInputStream", fileResourceIdentifier.hasSourceInputStream());
		assertTrue("Any added fileResourceIdentifier created with a File should return true on hasSourceFile", fileResourceIdentifier.hasSourceFile());		
		
		assertEquals((Integer) 3, (Integer) acsV2.getCurrentVersion());
		
		assertTrue("my_new_file should have been added to the table of content's xml", newToc.toXml().indexOf("<toc:file toc:URI=\"file:///my_new_file\"/>") > -1);
	}
		
	@Test
	public void testDuplicateFileResourceIdentifierException() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		File newFile = TestUtils.testFile();
		newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		try {
			// Now re-add a file resource with the same name and it should throw a DuplicateFileResourceIdentifierException
			newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
			
			fail("Failed to throw DuplicateFileResourceIdentifierException");
		} catch (DuplicateFileResourceIdentifierException expectedException) {
			
		}
	}

	@Test
	public void testCaseInsensitiveDuplicateFileResourceIdentifierException() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		File newFile = TestUtils.testFile();
		newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		try {
			// Now re-add a file resource with the same name and it should throw a DuplicateFileResourceIdentifierException
			newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH.toUpperCase(), newFile);
			
			fail("Failed to throw DuplicateFileResourceIdentifierException");
		} catch (DuplicateFileResourceIdentifierException expectedException) {
			
		}
	}
	
	@Test
	public void testDuplicateFileResourceIdentifierExceptionWithUri() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		File newFile = TestUtils.testFile();
		newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		try {
			// Now re-add a file resource with the same name and it should throw a DuplicateFileResourceIdentifierException
			newToc.createFileResourceIdentifier(new URI(MY_NEW_FILE_PATH), newFile);
			fail("Failed to throw DuplicateFileResourceIdentifierException");
		} catch (DuplicateFileResourceIdentifierException expectedException) {
			
		}
	}

	@Test
	public void testGetAdditionalInfo() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		TableOfContents tableOfContentsV1 = acsV2.getTableOfContents(1);
		TableOfContents tableOfContents = acsV2.createNextTableOfContents();
		
		assertTrue("TableOfContents.getAdditionalInfo() of a newly created TableOfContents should return an empty list", tableOfContents.getAdditionalInfo().length == 0);
		
		String additionalInfoString0 = "This is <who>my</who> additional info.\nAnd stuff!!!!";
		String additionalInfoString0Escaped = "This is &lt;who&gt;my&lt;/who&gt; additional info.\nAnd stuff!!!!";

		String additionalInfoString1 = "OMG!!! Kittens!!!";

		tableOfContents.addAdditionalInfo(additionalInfoString0);
		assertTrue("TableOfContents.getAdditionalInfo() should return no additional info on TableOfContents that didn't have any", tableOfContentsV1.getAdditionalInfo().length == 0);
		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", tableOfContents.getAdditionalInfo().length == 1);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0Escaped, tableOfContents.getAdditionalInfo()[0].toString());

		tableOfContents.addAdditionalInfo(additionalInfoString1);
		assertTrue("TableOfContents.getAdditionalInfo() should return no additional info on TableOfContents that didn't have any", tableOfContentsV1.getAdditionalInfo().length == 0);
		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", tableOfContents.getAdditionalInfo().length == 2);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0Escaped, tableOfContents.getAdditionalInfo()[0].toString());
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString1, tableOfContents.getAdditionalInfo()[1].toString());

		TableOfContents reloadedTableOfContents = TestUtils.writeOutTableOfContentsAndReload(tableOfContents); 

		TableOfContents reloadedTableOfContentsV1 = reloadedTableOfContents.getAcs().getTableOfContents(1);

		assertTrue("TableOfContents.getAdditionalInfo() should return no additional info on TableOfContents that didn't have any", reloadedTableOfContentsV1.getAdditionalInfo().length == 0);
		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", reloadedTableOfContents.getAdditionalInfo().length == 2);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0Escaped, reloadedTableOfContents.getAdditionalInfo()[0].toString());
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString1, reloadedTableOfContents.getAdditionalInfo()[1].toString());		
	}
	
	@Test
	public void testRemoveAdditionalInfo() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		TableOfContents tableOfContents = acsV2.createNextTableOfContents();
		
		assertTrue("TableOfContents.getAdditionalInfo() of a newly created TableOfContents should return an empty list", tableOfContents.getAdditionalInfo().length == 0);
		
		String additionalInfoString0 = "This is <who>my</who> additional info.\nAnd stuff!!!!";
		String additionalInfoString0Escaped = "This is &lt;who&gt;my&lt;/who&gt; additional info.\nAnd stuff!!!!";

		String additionalInfoString1 = "ZOMG!!! Kittens!!!";

		tableOfContents.addAdditionalInfo(additionalInfoString0);
		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", tableOfContents.getAdditionalInfo().length == 1);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0Escaped, tableOfContents.getAdditionalInfo()[0].toString());

		tableOfContents.addAdditionalInfo(additionalInfoString1);
		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", tableOfContents.getAdditionalInfo().length == 2);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0Escaped, tableOfContents.getAdditionalInfo()[0].toString());
		assertEquals("TableOfContents.getAdditionalInfo() return any added additional info", additionalInfoString1, tableOfContents.getAdditionalInfo()[1].toString());

		TableOfContents reloadedTableOfContents = TestUtils.writeOutTableOfContentsAndReload(tableOfContents); 

		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", reloadedTableOfContents.getAdditionalInfo().length == 2);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0Escaped, reloadedTableOfContents.getAdditionalInfo()[0].toString());
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString1, reloadedTableOfContents.getAdditionalInfo()[1].toString());

		AdditionalInfo reloadedAdditionalInfo = reloadedTableOfContents.getAdditionalInfo()[0];
		assertTrue("TableOfContents.removeAdditionalInfo should be able to be removed from an AdditionalInfo from a TableOfContents and return true on success", reloadedTableOfContents.removeAdditionalInfo(reloadedAdditionalInfo));
		assertFalse("TableOfContents.removeAdditionalInfo should return false if the AdditionalInfo does not exist", reloadedTableOfContents.removeAdditionalInfo(reloadedAdditionalInfo));

		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info after an AdditionalInfo removal", reloadedTableOfContents.getAdditionalInfo().length == 1);
		assertEquals("TableOfContents.getAdditionalInfo() should return an updated list after an AdditionalInfo removal", additionalInfoString1, reloadedTableOfContents.getAdditionalInfo()[0].toString());
		
		TableOfContents reloadedTableOfContents2 = TestUtils.writeOutTableOfContentsAndReload(reloadedTableOfContents); 

		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info after an AdditionalInfo removal after a reload", reloadedTableOfContents2.getAdditionalInfo().length == 1);
		assertEquals("TableOfContents.getAdditionalInfo() should return an updated list after an AdditionalInfo removal after a reload", additionalInfoString1, reloadedTableOfContents2.getAdditionalInfo()[0].toString());		
	}
	
	@Test
	public void testCaseInsensitivityOfFileResourceIdentifierUris() throws Exception {
		String stringUri = "file:///UpperAndLowerCaseLetters";
		String stringUriLower = stringUri.toLowerCase();
		String stringUriUpper = stringUri.toUpperCase();
		
		URI uri = new URI(stringUri);
		URI uriLower = new URI(stringUriLower);		
		URI uriUpper = new URI(stringUriUpper);

		File sourceFile = TestUtils.testFile();

		
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(stringUri);
		
		TableOfContents tableOfContents = fileResourceIdentifier.getTableOfContents();

		assertEquals("TableOfContents.getFileResourceByUri should be case insensitive", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(uri));
		assertEquals("TableOfContents.getFileResourceByUri should be case insensitive", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(stringUri));

		try {
			tableOfContents.createFileResourceIdentifier(uri, sourceFile);
			fail("TableOfContents.createFileResource failed to detect a duplicate uri");
		} catch (DuplicateFileResourceIdentifierException expectedException) {}
		try {
			tableOfContents.createFileResourceIdentifier(stringUri, sourceFile);
			fail("TableOfContents.createFileResource failed to detect a duplicate uri");
		} catch (DuplicateFileResourceIdentifierException expectedException) {}
		
		assertEquals("TableOfContents.getFileResourceByUri should be case insensitive", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(uriLower));
		assertEquals("TableOfContents.getFileResourceByUri should be case insensitive", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(stringUriLower));

		try {
			tableOfContents.createFileResourceIdentifier(uriLower, sourceFile);
			fail("TableOfContents.createFileResource failed to detect a duplicate uri");
		} catch (DuplicateFileResourceIdentifierException expectedException) {}
		try {
			tableOfContents.createFileResourceIdentifier(stringUriLower, sourceFile);
			fail("TableOfContents.createFileResource failed to detect a duplicate uri");
		} catch (DuplicateFileResourceIdentifierException expectedException) {}
		
		assertEquals("TableOfContents.getFileResourceByUri should be case insensitive", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(uriUpper));
		assertEquals("TableOfContents.getFileResourceByUri should be case insensitive", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(stringUriUpper));

		try {
			tableOfContents.createFileResourceIdentifier(uriUpper, sourceFile);
			fail("TableOfContents.createFileResource failed to detect a duplicate uri");
		} catch (DuplicateFileResourceIdentifierException expectedException) {}
		try {
			tableOfContents.createFileResourceIdentifier(stringUriUpper, sourceFile);
			fail("TableOfContents.createFileResource failed to detect a duplicate uri");
		} catch (DuplicateFileResourceIdentifierException expectedException) {}				
	}
		
	@Test
	public void testUriKey() throws Exception {
		String stringUri = "file://UpperAndLowerCaseLetters";
		URI uri = new URI(stringUri);
		assertEquals("TableOfContents.uriKey should convert a uri to a lowercase String", stringUri.toLowerCase(), TableOfContents.uriKey(uri));
		assertFalse("Results of TableOfContents.uriKey should not equal a uri of the same string converted a uri to a uppercase", stringUri.toUpperCase().equals(TableOfContents.uriKey(uri)));

		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(stringUri);
		assertEquals("TableOfContents.uriKey should convert a FileResourceIdentifier uri to a lowercase String", stringUri.toLowerCase(), TableOfContents.uriKey(fileResourceIdentifier));
	}
	
	@Test
	public void testRemoveFileResourceIdentifier() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		TableOfContents tableOfContents = acsV2.createNextTableOfContents();

		File sourceFile = TestUtils.testFile();

		String addedThenRemoved = "file:///ToBeRemoved";
		
		FileResourceIdentifier fileResourceIdentifierToAddThenRemove = tableOfContents.createFileResourceIdentifier(addedThenRemoved, sourceFile);
		assertEquals("FileResourceIdentifier.getFileResourceByUri should return an added FileResourceIdentifier", fileResourceIdentifierToAddThenRemove, tableOfContents.getFileResourceIdentifierByUri(addedThenRemoved));
		tableOfContents.removeFileResourceIdentifier(fileResourceIdentifierToAddThenRemove);
		assertEquals("FileResourceIdentifier.getFileResourceByUri should not return a removed FileResourceIdentifier", null, tableOfContents.getFileResourceIdentifierByUri(addedThenRemoved));

		
		FileResourceIdentifier fileResourceIdentifier = tableOfContents.createFileResourceIdentifier(MY_NEW_FILE_PATH, sourceFile);
		assertEquals("FileResourceIdentifier.getFileResourceByUri should return an added FileResourceIdentifier", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH));

		
		TableOfContents reloadedTableOfContents = TestUtils.writeOutTableOfContentsAndReload(tableOfContents);
		
		FileResourceIdentifier reloadFileResourceIdentifier = reloadedTableOfContents.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH);
		assertTrue("FileResourceIdentifier.getFileResourceByUri should return an added FileResourceIdentifier after save and realod", reloadFileResourceIdentifier != null);
		reloadedTableOfContents.removeFileResourceIdentifier(reloadFileResourceIdentifier);
		assertEquals("FileResourceIdentifier.getFileResourceByUri should not return a removed FileResourceIdentifier", null, reloadedTableOfContents.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH));
		assertEquals("FileResourceIdentifier.getFileResourceByUri should not return a removed FileResourceIdentifier", null, reloadedTableOfContents.getFileResourceIdentifierByUri(addedThenRemoved));

		
		TableOfContents reloadedTableOfContents2 = TestUtils.writeOutTableOfContentsAndReload(reloadedTableOfContents);
		assertEquals("FileResourceIdentifier.getFileResourceByUri should not return a removed FileResourceIdentifier", null, reloadedTableOfContents2.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH));
		assertEquals("FileResourceIdentifier.getFileResourceByUri should not return a removed FileResourceIdentifier", null, reloadedTableOfContents2.getFileResourceIdentifierByUri(addedThenRemoved));
	}
	
	@Test
	public void testGetProjectWorkspaces() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		File newFile  = TestUtils.testFile();
		File newFile2 = TestUtils.testFile();

		File newFile3 = TestUtils.testFile();

		
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		assertTrue("TableOfContents.getProjectWorkspaces should return no results if there are no workspace files", newToc.getProjectWorkspaces().length == 0);
		
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		
		FileResourceIdentifier fileResourceIdentifier2 = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH2, newFile2);

		FileResourceIdentifier associatedLocalFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH3, newFile3);

		
		// Create an association from localFileResourceIdentifier to associatedLocalFileResourceIdentifier
		fileResourceIdentifier.createAssociation(associatedLocalFileResourceIdentifier, RelationshipTypes.PROJECT_WORKSPACE);


		// Create a second association from another localFileResourceIdentifier to the same associatedLocalFileResourceIdentifier
		fileResourceIdentifier2.createAssociation(associatedLocalFileResourceIdentifier, RelationshipTypes.PROJECT_WORKSPACE);

		newToc = TestUtils.writeOutTableOfContentsAndReload(newToc);
		
		FileResourceIdentifier reloadedTempFileResourceIdentifier = newToc.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH);
		
		Association[] tempFileResourceIdentifierAssociations = reloadedTempFileResourceIdentifier.getAssociations();
		
		assertTrue(tempFileResourceIdentifierAssociations.length == 1);
		
		Association tempFileResourceIdentifierAssociation = tempFileResourceIdentifierAssociations[0];
				
		assertEquals("The RelationshipType of the saved association should be retained.", RelationshipTypes.PROJECT_WORKSPACE, tempFileResourceIdentifierAssociation.getRelationship());		
	
		assertEquals("Association.getAssociatedUriTo should return the URI to the FileResourceIdentifier that the association is to", new URI(MY_NEW_FILE_PATH3), tempFileResourceIdentifierAssociation.getAssociatedUriTo());
	
		FileResourceIdentifier associatedTempFileResourceIdentifier = tempFileResourceIdentifierAssociation.getAssociatedTo();
		assertEquals("Association.getAssociatedTo() should return the FileResourceIdentifier that the Accoiation points to", new URI(MY_NEW_FILE_PATH3), associatedTempFileResourceIdentifier.getUri());

		assertTrue("TableOfContents.getProjectWorkspaces should return all FileResourceIdentifiers that are associated as workspaces without any duplicates.", newToc.getProjectWorkspaces().length == 1);

		assertEquals("TableOfContents.getProjectWorkspaces should return all FileResourceIdentifiers that are associated as workspaces.", associatedTempFileResourceIdentifier, newToc.getProjectWorkspaces()[0]);
	}
	
	@Test
	public void testGetFileResourceIdentifiersAssociatedTo() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		File newFile  = TestUtils.testFile();
		File newFile2 = TestUtils.testFile();

		File newFile3 = TestUtils.testFile();

		
		TableOfContents newToc = acsV2.createNextTableOfContents();

		FileResourceIdentifier fcsFilefileResourceIdentifier1 = newToc.getFileResourceIdentifierByUri("file:///20071001-u937.004");
		FileResourceIdentifier fcsFilefileResourceIdentifier2 = newToc.getFileResourceIdentifierByUri("file:///20071001-u937.007");

		FileResourceIdentifier workspaceFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		FileResourceIdentifier signatureFileResourceIdentifier2 = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH2, newFile2);
		FileResourceIdentifier fileResourceIdentifier3 = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH3, newFile3);

		assertTrue("getFcsFilesAssociatedTo should return no results if there are no workspace files associated to it.", newToc.getFcsFilesAssociatedTo(workspaceFileResourceIdentifier).length == 0);
		
		
		
		// Create two separate associations from FCS files to workspaces
		fcsFilefileResourceIdentifier1.createAssociation(workspaceFileResourceIdentifier, RelationshipTypes.PROJECT_WORKSPACE);

		fcsFilefileResourceIdentifier2.createAssociation(signatureFileResourceIdentifier2, RelationshipTypes.DIGITAL_SIGNATURE);

		newToc = TestUtils.writeOutTableOfContentsAndReload(newToc);
		
		fcsFilefileResourceIdentifier1 = newToc.getFileResourceIdentifierByUri("file:///20071001-u937.004");
		fcsFilefileResourceIdentifier2 = newToc.getFileResourceIdentifierByUri("file:///20071001-u937.007");

		assertNotNull("Sanity check.", fcsFilefileResourceIdentifier1);
		assertNotNull("Sanity check.", fcsFilefileResourceIdentifier2);
		
		workspaceFileResourceIdentifier = newToc.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH);
		signatureFileResourceIdentifier2 = newToc.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH2);
		fileResourceIdentifier3 = newToc.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH3);

		
		FileResourceIdentifier[] associatedToFileResourceIdentifiers = newToc.getFileResourceIdentifiersAssociatedTo(workspaceFileResourceIdentifier);
		FileResourceIdentifier[] associatedToFileResourceIdentifiers2 = newToc.getFileResourceIdentifiersAssociatedTo(signatureFileResourceIdentifier2);

		
		assertTrue("TableOfContents.getFileResourceIdentifiersAssociatedTo should return the correct number of FileResourceIdentifiers associated with another FileResourceIdentifier", associatedToFileResourceIdentifiers.length == 1);
		assertEquals("TableOfContents.getFileResourceIdentifiersAssociatedTo should return the correct FileResourceIdentifiers associated with another FileResourceIdentifier", fcsFilefileResourceIdentifier1, associatedToFileResourceIdentifiers[0]);

		assertTrue("TableOfContents.getFcsFilesAssociatedTo should return the correct number of FileResourceIdentifiers associated with another FileResourceIdentifier", associatedToFileResourceIdentifiers2.length == 1);
		assertEquals("TableOfContents.getFcsFilesAssociatedTo should return the correct FileResourceIdentifiers associated with another FileResourceIdentifier", fcsFilefileResourceIdentifier2,associatedToFileResourceIdentifiers2[0]);
		
		
		assertTrue("TableOfContents.getFileResourceIdentifiersAssociatedTo should return no FileResourceIdentifiers if there are no associations", newToc.getFcsFilesAssociatedTo(fileResourceIdentifier3).length == 0);

		assertNull("TableOfContents.getFileResourceIdentifiersAssociatedTo should return null if given a null paramater", newToc.getFcsFilesAssociatedTo(null));

	}
	
	@Test
	public void testGetFcsFilesAssociatedTo() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();

		File newFile  = TestUtils.testFile();
		File newFile2 = TestUtils.testFile();

		File newFile3 = TestUtils.testFile();

		
		TableOfContents newToc = acsV2.createNextTableOfContents();

		FileResourceIdentifier fcsFilefileResourceIdentifier1 = newToc.getFileResourceIdentifierByUri("file:///20071001-u937.004");
		FileResourceIdentifier fcsFilefileResourceIdentifier2 = newToc.getFileResourceIdentifierByUri("file:///20071001-u937.007");

		FileResourceIdentifier workspaceFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		FileResourceIdentifier signatureFileResourceIdentifier2 = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH2, newFile2);
		FileResourceIdentifier fileResourceIdentifier3 = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH3, newFile3);

		assertTrue("getFcsFilesAssociatedTo should return no results if there are no workspace files associated to it.", newToc.getFcsFilesAssociatedTo(workspaceFileResourceIdentifier).length == 0);
		
		
		
		// Create two separate associations from FCS files to workspaces
		fcsFilefileResourceIdentifier1.createAssociation(workspaceFileResourceIdentifier, RelationshipTypes.PROJECT_WORKSPACE);

		fcsFilefileResourceIdentifier2.createAssociation(signatureFileResourceIdentifier2, RelationshipTypes.DIGITAL_SIGNATURE);

		newToc = TestUtils.writeOutTableOfContentsAndReload(newToc);
		
		fcsFilefileResourceIdentifier1 = newToc.getFileResourceIdentifierByUri("file:///20071001-u937.004");
		fcsFilefileResourceIdentifier2 = newToc.getFileResourceIdentifierByUri("file:///20071001-u937.007");

		assertNotNull("Sanity check.", fcsFilefileResourceIdentifier1);
		assertNotNull("Sanity check.", fcsFilefileResourceIdentifier2);
		
		workspaceFileResourceIdentifier = newToc.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH);
		signatureFileResourceIdentifier2 = newToc.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH2);
		fileResourceIdentifier3 = newToc.getFileResourceIdentifierByUri(MY_NEW_FILE_PATH3);

		
		FileResourceIdentifier[] associatedToFileResourceIdentifiers = newToc.getFcsFilesAssociatedTo(workspaceFileResourceIdentifier);
		FileResourceIdentifier[] associatedToFileResourceIdentifiers2 = newToc.getFcsFilesAssociatedTo(signatureFileResourceIdentifier2);

		
		assertTrue("TableOfContents.getFcsFilesAssociatedTo should return the correct number of FCS files associated with another FileResourceIdentifier", associatedToFileResourceIdentifiers.length == 1);
		assertEquals("TableOfContents.getFcsFilesAssociatedTo should return the correct FCS files associated with another FileResourceIdentifier", fcsFilefileResourceIdentifier1, associatedToFileResourceIdentifiers[0]);

		assertTrue("TableOfContents.getFcsFilesAssociatedTo should return the correct number of FCS files associated with another FileResourceIdentifier", associatedToFileResourceIdentifiers2.length == 1);
		assertEquals("TableOfContents.getFcsFilesAssociatedTo should return the correct FCS files associated with another FileResourceIdentifier", fcsFilefileResourceIdentifier2,associatedToFileResourceIdentifiers2[0]);
		
		
		assertTrue("TableOfContents.getFcsFilesAssociatedTo should return no FCS files if there are no associations", newToc.getFcsFilesAssociatedTo(fileResourceIdentifier3).length == 0);

		assertNull("TableOfContents.getFcsFilesAssociatedTo should return null if given a null paramater", newToc.getFcsFilesAssociatedTo(null));

	}
	
	@After
	public void tearDown() throws Exception {
	}
}
