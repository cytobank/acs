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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.cytobank.TestUtils;
import org.cytobank.acs.core.ACS;
import org.cytobank.acs.core.FileResourceIdentifier;
import org.cytobank.acs.core.TableOfContents;
import org.cytobank.acs.core.exceptions.DuplicateFileResourceIdentifierException;
import org.cytobank.acs.util.FileUtils;

public class FileResourceIdentifierTest {	
	static final String MY_NEW_FILE = "/my_new_file";
	static final String MY_NEW_FILE_PATH = "file://" + MY_NEW_FILE;
	
	static final String MY_NEW_FILE2 = "/my_new_file2";
	static final String MY_NEW_FILE_PATH2 = "file://" + MY_NEW_FILE2;
	
	@Before
	public void setUp() throws Exception {
	}
		
	@Test
	public void testGetUriAndSetUri() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		TableOfContents tableOfContents = fileResourceIdentifier.getTableOfContents();
		
		assertEquals("FileResourceIdentifier.getUri should return the uri", new URI(MY_NEW_FILE_PATH), fileResourceIdentifier.getUri());

		
		URI newUri = new URI("file:///a.new.uri");
		
		fileResourceIdentifier.setUri(newUri);

		assertEquals("FileResourceIdentifier.getUri should return the uri", newUri, fileResourceIdentifier.getUri());
		
		assertEquals("tableOfContents.getFileResourceByUri should be able to find the FileResourceIdentifier by the newUri", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(newUri));
		
		FileResourceIdentifier tempFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier); 
		assertEquals("FileResourceIdentifier.getUri should return the uri", newUri, tempFileResourceIdentifier.getUri());		
	}

	@Test
	public void testGetUriAndSetUriWithStringUri() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		TableOfContents tableOfContents = fileResourceIdentifier.getTableOfContents();
		
		assertEquals("FileResourceIdentifier.getUri should return the uri", new URI(MY_NEW_FILE_PATH), fileResourceIdentifier.getUri());

		
		String stringUri = "file:///a.new.uri";
		
		URI newUri = new URI(stringUri);
		
		fileResourceIdentifier.setUri(stringUri);

		assertEquals("FileResourceIdentifier.getUri should return the uri", newUri, fileResourceIdentifier.getUri());
		
		assertEquals("tableOfContents.getFileResourceByUri should be able to find the FileResourceIdentifier by the newUri", fileResourceIdentifier, tableOfContents.getFileResourceIdentifierByUri(newUri));
		
		FileResourceIdentifier tempFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier); 
		assertEquals("FileResourceIdentifier.getUri should return the uri", newUri, tempFileResourceIdentifier.getUri());		
	}

	
	@Test
	public void testDuplicateFileResourceIdentifierExceptionOnDuplicateSetUri() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		File newFile = TestUtils.testFile();
		newToc.createFileResourceIdentifier("file:///lala", newFile);
		try {
			FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
			fileResourceIdentifier.setUri(new URI("file:///lala"));
		
			fail("Setting a duplicate URI on a FileResourceIdentifier should have caused a DuplicateFileResourceIdentifierException");
		} catch (DuplicateFileResourceIdentifierException expectedException) {
		}
	}
	
	@Test
	public void testDuplicateFileResourceIdentifierExceptionOnDuplicateSetUriWithStringUri() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		File newFile = TestUtils.testFile();
		newToc.createFileResourceIdentifier("file:///lala", newFile);
		try {
			FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
			fileResourceIdentifier.setUri("file:///lala");
		
			fail("Setting a duplicate URI on a FileResourceIdentifier should have caused a DuplicateFileResourceIdentifierException");
		} catch (DuplicateFileResourceIdentifierException expectedException) {
		}
	}
	
	@Test
	public void testGetAcs() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		FileResourceIdentifier resourceIdentifiers = newToc.getFileResourceIdentifiers()[0];
		
		assertEquals("FileResourceIdentifier.getAcs() should return the owned ACS instance", acsV2, resourceIdentifiers.getAcs());
	}
	
	@Test
	public void getMimeTypeAndSetMimeType() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		
		assertEquals("A new fileResourceIdentifier should have an empty String MIME type", "", fileResourceIdentifier.getMimeType());

		String mimeType = "foo/mime.type";
		
		fileResourceIdentifier.setMimeType(mimeType);
		assertEquals("fileResourceIdentifier.getMimeType should return the set MIME type", mimeType, fileResourceIdentifier.getMimeType());
		
		FileResourceIdentifier tempFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier); 
		assertEquals("fileResourceIdentifier.getMimeType should return the set MIME type after ACS save and reload", mimeType, tempFileResourceIdentifier.getMimeType());
	}
	
	@Test
	public void testIsFcsFile() throws Exception {		
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		File newFile = TestUtils.testFile();
		
		FileInputStream fis1 =  new FileInputStream(newFile);
		FileInputStream fis2 =  new FileInputStream(newFile);
		FileInputStream fis3 =  new FileInputStream(newFile);
		FileInputStream fis4 =  new FileInputStream(newFile);
		
		try {
			assertFalse(newToc.createFileResourceIdentifier("file:///my_fcs_file1", newFile).isFcsFile());

			assertTrue(newToc.createFileResourceIdentifier("file:///my_fcs_file2.fcs", newFile).isFcsFile());
			assertTrue(newToc.createFileResourceIdentifier("file:///my_fcs_file3.FCS", newFile).isFcsFile());
			
			assertTrue(newToc.createFileResourceIdentifier("file:///my_fcs_file4", newFile, Constants.FCS_FILE_MIME_TYPE).isFcsFile());
			assertTrue(newToc.createFileResourceIdentifier("file:///my_fcs_file5.fcs", newFile, Constants.FCS_FILE_MIME_TYPE).isFcsFile());
			
			assertTrue(newToc.createFileResourceIdentifier(new URI("file:///my_fcs_file6"), newFile, Constants.FCS_FILE_MIME_TYPE).isFcsFile());
			assertTrue(newToc.createFileResourceIdentifier(new URI("file:///my_fcs_file7"), fis1, Constants.FCS_FILE_MIME_TYPE).isFcsFile());
			assertTrue(newToc.createFileResourceIdentifier(new URI("file:///my_fcs_file8.fcs"), fis2, Constants.FCS_FILE_MIME_TYPE).isFcsFile());
			assertTrue(newToc.createFileResourceIdentifier(new URI("file:///my_fcs_file9.fcs"), fis3).isFcsFile());
			
			assertFalse(newToc.createFileResourceIdentifier(new URI("file:///my_fcs_file10"), fis4).isFcsFile());

		} finally {
			fis1.close();
			fis2.close();
			fis3.close();
			fis4.close();
		}
	}
	
	@Test
	public void testGetDescriptionAndSetDescription() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		
		assertEquals("A new fileResourceIdentifier should have an empty String description", "", fileResourceIdentifier.getDescription());
		
		String description = "My awesome description";
		fileResourceIdentifier.setDescription(description);
		assertEquals("FileResourceIdentifier.getDescription should return its description", description, fileResourceIdentifier.getDescription());
		
		FileResourceIdentifier tempFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier); 
		assertEquals("FileResourceIdentifier.getDescription should return its description after ACS save and reload", description, tempFileResourceIdentifier.getDescription());
	}
	
	@Test
	public void testGetDescriptionAndSetDescriptionWithWeirdCharacters() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		
		String description = "My awesome description <>\"\' () .* & &lt; &#60";
		fileResourceIdentifier.setDescription(description);
		assertEquals("FileResourceIdentifier.getDescription should return its description with characters that would need to be escaped in xml attributes", description, fileResourceIdentifier.getDescription());
		
		FileResourceIdentifier tempFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier); 
		assertEquals("FileResourceIdentifier.getDescription should return its description after ACS save and reload with characters that would need to be escaped in xml attributes", description, tempFileResourceIdentifier.getDescription());
	}

	@Test
	public void testGetFileResourceIdentifiers() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		File newFile = TestUtils.testFile();
		newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		
		FileResourceIdentifier[] resourceIdentifiers = newToc.getFileResourceIdentifiers();
		boolean newFileFound = false;
		for (FileResourceIdentifier fileResourceIdentifier : resourceIdentifiers) {
			String path = fileResourceIdentifier.getUri().getPath();

			if (MY_NEW_FILE.equals(path)) {
				newFileFound = true;
				break;
			}
		}

		assertTrue("A file resource with " + MY_NEW_FILE_PATH + "  should exist", newFileFound);
	}
	
	@Test
	public void testIsExternalReference() throws Exception {
		assertFalse("file:// should not show up as an external resource", TestUtils.newFileResourceIdentifier("file:///foo").isExternalReference());
		assertFalse("FILE:// should not show up as an external resource", TestUtils.newFileResourceIdentifier("FILE:///foo").isExternalReference());

		assertTrue("http:// should show up as an external resource", TestUtils.newFileResourceIdentifier("http:///foo").isExternalReference());
		assertTrue("https:// should show up as an external resource", TestUtils.newFileResourceIdentifier("https:///foo").isExternalReference());
		assertTrue("ftp:// should show up as an external resource", TestUtils.newFileResourceIdentifier("ftp:///foo").isExternalReference());
		assertTrue("HTTP:// should show up as an external resource", TestUtils.newFileResourceIdentifier("HTTP:///foo").isExternalReference());
		assertTrue("HTTPS:// should show up as an external resource", TestUtils.newFileResourceIdentifier("HTTPS:///foo").isExternalReference());
		assertTrue("FTP:// should show up as an external resource", TestUtils.newFileResourceIdentifier("FTP:///foo").isExternalReference());		
	}
	
	@Test
	public void testResourceScheme() throws Exception {
		assertEquals("file", TestUtils.newFileResourceIdentifier("file:///foo").resourceScheme());
		assertEquals("FILE", TestUtils.newFileResourceIdentifier("FILE:///foo").resourceScheme());
	}
	
	@Test
	public void testHasSourceInputStream() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		FileResourceIdentifier firstFileResourceIdentifier = newToc.getFileResourceIdentifiers()[0];
		
		assertFalse("FileResourceIdentifier.hasSourceInputStream() should be false for existing FileResourceIdentifiers", firstFileResourceIdentifier.hasSourceInputStream());
		
		File newFile = TestUtils.testFile();
		FileInputStream fis = new FileInputStream(newFile);
		
		
		FileResourceIdentifier localFileResourceIdentifier; 
		try {			
			localFileResourceIdentifier = newToc.createFileResourceIdentifier(new URI(MY_NEW_FILE_PATH), fis);
			File writtenFile = File.createTempFile("write_test", "tmp");
			localFileResourceIdentifier.writeRepresentedFile(writtenFile);

			assertTrue("FileResourceIdentifier.hasSourceInputStream() should be false for new FileResourceIdentifiers", localFileResourceIdentifier.hasSourceInputStream());
		} finally {
			fis.close();
		}
	}
	
	@Test
	public void testGetSourceInputStream() throws Exception {
				
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		File newFile = TestUtils.testFile();
		FileResourceIdentifier localFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);

		// Write out the contents to getSourceInputStream
		File writtenFile = File.createTempFile("write_test", "tmp");
		FileOutputStream fos = new FileOutputStream(writtenFile);		
		FileUtils.writeInputStreamToOutputStream(localFileResourceIdentifier.getSourceInputStream(), fos);		
		fos.close();
		
		assertEquals(TestUtils.md5sum(newFile), TestUtils.md5sum(writtenFile));
	}

	@Test
	public void testWriteOfNewResource() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		File newFile = TestUtils.testFile();
		FileResourceIdentifier localFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
				
		File writtenFile = File.createTempFile("write_test", "tmp");
		localFileResourceIdentifier.writeRepresentedFile(writtenFile);
		assertEquals(TestUtils.md5sum(newFile), TestUtils.md5sum(writtenFile));
	}
	
	@Test
	public void testHasAssociations() throws Exception { 
		ACS acsV2 = TestUtils.getAcsV2();

		File newFile  = TestUtils.testFile();
		File newFile2 = TestUtils.testFile();

		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		FileResourceIdentifier associatedLocalFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH2, newFile2);

		assertFalse("FileResourceIdentifier.hasAssociations should return false on a newly created FileResourceIdentifier", fileResourceIdentifier.hasAssociations());		
		assertFalse("FileResourceIdentifier.hasAssociations should return false on a newly created FileResourceIdentifier", associatedLocalFileResourceIdentifier.hasAssociations());

		fileResourceIdentifier.createAssociation(associatedLocalFileResourceIdentifier, RelationshipTypes.ANALYSIS_DESCRIPTION);

		assertTrue("FileResourceIdentifier.hasAssociations should return true after an association was created", fileResourceIdentifier.hasAssociations());
		assertFalse("FileResourceIdentifier.hasAssociations should return false on a newly created FileResourceIdentifier", associatedLocalFileResourceIdentifier.hasAssociations());
	}

	@Test
	public void testGetAssociations() throws Exception { 
		ACS acsV2 = TestUtils.getAcsV2();

		File newFile  = TestUtils.testFile();
		File newFile2 = TestUtils.testFile();

		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		FileResourceIdentifier fileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH, newFile);
		FileResourceIdentifier associatedLocalFileResourceIdentifier = newToc.createFileResourceIdentifier(MY_NEW_FILE_PATH2, newFile2);

		assertTrue("FileResourceIdentifier.getAssociations should return an empty Assciation array", fileResourceIdentifier.getAssociations().length == 0);		
		assertTrue("FileResourceIdentifier.getAssociations should return an empty Assciation array", associatedLocalFileResourceIdentifier.getAssociations().length == 0);		

		Association createdAssociation = fileResourceIdentifier.createAssociation(associatedLocalFileResourceIdentifier, RelationshipTypes.ANALYSIS_DESCRIPTION);

		assertTrue("FileResourceIdentifier.getAssociations should not return an empty Assciation array after an association was created", fileResourceIdentifier.getAssociations().length == 1);		
		assertTrue("FileResourceIdentifier.getAssociations should return an empty Assciation array", associatedLocalFileResourceIdentifier.getAssociations().length == 0);		

		assertEquals("FileResourceIdentifier.getAssociations should return all Assciations", createdAssociation, fileResourceIdentifier.getAssociations()[0]);
	}
			
	@Test
	public void testWriteOfNewResourceFromStream() throws Exception {
		ACS acsV2 = TestUtils.getAcsV2();
		TableOfContents newToc = acsV2.createNextTableOfContents();
		
		File newFile = TestUtils.testFile();
		FileInputStream fis = new FileInputStream(newFile);
		
		
		FileResourceIdentifier localFileResourceIdentifier; 
		try {			
			localFileResourceIdentifier = newToc.createFileResourceIdentifier(new URI(MY_NEW_FILE_PATH), fis);
			File writtenFile = File.createTempFile("write_test", "tmp");
			localFileResourceIdentifier.writeRepresentedFile(writtenFile);
			
			assertEquals(TestUtils.md5sum(newFile), TestUtils.md5sum(writtenFile));
		} finally {
			fis.close();
		}
	}
	
	@Test
	public void testIsUriSchemeAllowed() {
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed("file"));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed("http"));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed("https"));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed("ftp"));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed("FILE"));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed("HTTP"));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed("HTTPS"));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed("FTP"));

		assertFalse(FileResourceIdentifier.isUriSchemeAllowed("scp"));
	}
	
	@Test
	public void testIsUriSchemeAllowedWithUris() throws URISyntaxException {
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed(new URI("file:///foo")));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed(new URI("http:///foo")));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed(new URI("https:///foo")));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed(new URI("ftp:///foo")));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed(new URI("FILE:///foo")));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed(new URI("HTTP:///foo")));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed(new URI("HTTPS:///foo")));
		assertTrue(FileResourceIdentifier.isUriSchemeAllowed(new URI("FTP:///foo")));

		assertFalse(FileResourceIdentifier.isUriSchemeAllowed(new URI("scp:///foo")));
	}
	
	@Test
	public void testGetAdditionalInfo() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		
		assertTrue("FileResourceIdentifier.getAdditionalInfo() of a newly created TableOfContents should return an empty list", fileResourceIdentifier.getAdditionalInfo().length == 0);
		
		String additionalInfoString0 = "This is <who>my</who> additional info.\nAnd stuff!!!!";
		String additionalInfoString1 = "ZOMG!!! Kittens!!!";

		fileResourceIdentifier.addAdditionalInfo(additionalInfoString0);
		assertTrue("FileResourceIdentifier.getAdditionalInfo() should return any added additional info", fileResourceIdentifier.getAdditionalInfo().length == 1);
		assertEquals("FileResourceIdentifier.getAdditionalInfo() should return any added additional info", additionalInfoString0, fileResourceIdentifier.getAdditionalInfo()[0].getInfo());

		fileResourceIdentifier.addAdditionalInfo(additionalInfoString1);
		assertTrue("FileResourceIdentifier.getAdditionalInfo() should return any added additional info", fileResourceIdentifier.getAdditionalInfo().length == 2);
		assertEquals("FileResourceIdentifier.getAdditionalInfo() should return any added additional info", additionalInfoString0, fileResourceIdentifier.getAdditionalInfo()[0].getInfo());
		assertEquals("FileResourceIdentifier.getAdditionalInfo() should return any added additional info", additionalInfoString1, fileResourceIdentifier.getAdditionalInfo()[1].getInfo());

		FileResourceIdentifier reloadedFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier);

		assertTrue("FileResourceIdentifier.getAdditionalInfo() should return any added additional info", reloadedFileResourceIdentifier.getAdditionalInfo().length == 2);
		assertEquals("FileResourceIdentifier.getAdditionalInfo() should return any added additional info", additionalInfoString0, reloadedFileResourceIdentifier.getAdditionalInfo()[0].getInfo());
		assertEquals("FileResourceIdentifier.getAdditionalInfo() should return any added additional info", additionalInfoString1, reloadedFileResourceIdentifier.getAdditionalInfo()[1].getInfo());
	}
	
	@Test
	public void testRemoveAdditionalInfo() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);

		String additionalInfoString0 = "This is <who>my</who> additional info.\nAnd stuff!!!!";
		String additionalInfoString1 = "ZOMG!!! Kittens!!!";

		fileResourceIdentifier.addAdditionalInfo(additionalInfoString0);
		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", fileResourceIdentifier.getAdditionalInfo().length == 1);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0, fileResourceIdentifier.getAdditionalInfo()[0].getInfo());

		fileResourceIdentifier.addAdditionalInfo(additionalInfoString1);
		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", fileResourceIdentifier.getAdditionalInfo().length == 2);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0, fileResourceIdentifier.getAdditionalInfo()[0].getInfo());
		assertEquals("TableOfContents.getAdditionalInfo() return any added additional info", additionalInfoString1, fileResourceIdentifier.getAdditionalInfo()[1].getInfo());

		FileResourceIdentifier reloadedFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier);

		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info", reloadedFileResourceIdentifier.getAdditionalInfo().length == 2);
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString0, reloadedFileResourceIdentifier.getAdditionalInfo()[0].getInfo());
		assertEquals("TableOfContents.getAdditionalInfo() should return any added additional info", additionalInfoString1, reloadedFileResourceIdentifier.getAdditionalInfo()[1].getInfo());

		AdditionalInfo reloadedAdditionalInfo = reloadedFileResourceIdentifier.getAdditionalInfo()[0];
		assertTrue("TableOfContents.removeAdditionalInfo should be able to be removed from an AdditionalInfo from a TableOfContents and return true on success", reloadedFileResourceIdentifier.removeAdditionalInfo(reloadedAdditionalInfo));
		assertFalse("TableOfContents.removeAdditionalInfo should return false if the AdditionalInfo does not exist", reloadedFileResourceIdentifier.removeAdditionalInfo(reloadedAdditionalInfo));

		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info after an AdditionalInfo removal", reloadedFileResourceIdentifier.getAdditionalInfo().length == 1);
		assertEquals("TableOfContents.getAdditionalInfo() should return an updated list after an AdditionalInfo removal", additionalInfoString1, reloadedFileResourceIdentifier.getAdditionalInfo()[0].getInfo());
		
		FileResourceIdentifier reloadedFileResourceIdentifier2 = TestUtils.writeOutFileResourceIdentifierAndReload(reloadedFileResourceIdentifier); 

		assertTrue("TableOfContents.getAdditionalInfo() should return any added additional info after an AdditionalInfo removal after a reload", reloadedFileResourceIdentifier2.getAdditionalInfo().length == 1);
		assertEquals("TableOfContents.getAdditionalInfo() should return an updated list after an AdditionalInfo removal after a reload", additionalInfoString1, reloadedFileResourceIdentifier2.getAdditionalInfo()[0].getInfo());		
	}
	
	@Test
	public void testGetAcsPath() throws Exception {
		//String path = "UpperAndLowerCaseLetters";
		//String stringUri = "file://" + path;

		String pathWithSlash = "/UpperAndLowerCaseLetters";
		String stringUriWithSlash = "file://" + pathWithSlash;

		String pathOneDeep = "/one/UpperAndLowerCaseLetters";
		String stringUriOneDeep = "file://" + pathOneDeep;

		String pathTwoDeep = "/one/two/UpperAndLowerCaseLetters";
		String stringUriTwoDeep = "file://" + pathTwoDeep;
		
		//assertEquals("FileResourceIdentifier.getAcsFilePath should return a path to the URI correcting for a missing / after file:// (eg file://foo)", pathWithSlash, TestUtils.newFileResourceIdentifier(stringUri).getAcsFilePath());
		assertEquals("FileResourceIdentifier.getAcsFilePath should return a path to the URI", pathWithSlash, TestUtils.newFileResourceIdentifier(stringUriWithSlash).getAcsFilePath());
		assertEquals("FileResourceIdentifier.getAcsFilePath should return a path to the URI one path deep", pathOneDeep, TestUtils.newFileResourceIdentifier(stringUriOneDeep).getAcsFilePath());
		assertEquals("FileResourceIdentifier.getAcsFilePath should return a path to the URI two paths deep", pathTwoDeep, TestUtils.newFileResourceIdentifier(stringUriTwoDeep).getAcsFilePath());

	}
	
	@After
	public void tearDown() throws Exception {
	}

}
