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

package org.cytobank;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.util.Random;

import org.cytobank.acs.core.ACS;
import org.cytobank.acs.core.FileResourceIdentifier;
import org.cytobank.acs.core.TableOfContents;

import com.twmacinta.util.MD5;

public class TestUtils {
	
	public static final String ACS_TEST_FILE_PATH = "/acs_files/";
	
	public static final String U937_ACS_TEST_FILE1 = "u937_v1.acs";

	public static final String U937_ACS_TEST_FILE2 = "u937_v2.acs";
	
	public static final Random random = new Random();
	
	/*
	 * Generate a test file
	 */
	public static File testFile() throws Exception {
		File result = File.createTempFile("AcsTest", "tmp");

		PrintStream printStream = new PrintStream(result);
		String contents = "This is a test. " + System.currentTimeMillis() + "\nAnd a random number for good measure: " + random.nextDouble();
		printStream.append(contents);
		printStream.close();
			
		return result;
	}
	
	/**
	 * Return an md4sum string 
	 * @param file The file to return the md5sum of
	 * @return The resulting md5sum
	 * @throws IOException If there was a problem
	 */
	public static String md5sum(File file) throws IOException {
		return MD5.asHex(MD5.getHash(file));
	}
	
	
	/**
	 * Return an <code>ACS</code> instance from the unit test acs_files directory.
	 * 
	 * @param testAcsFilename the name of the ACS file
	 * @return an <code>ACS</code> instance
	 * @throws Exception if there was a problem
	 */
	public static ACS getTestAcsInstance(String testAcsFilename) throws Exception {
		URL u937_acs_url_v2 = TestUtils.class.getResource(new File(ACS_TEST_FILE_PATH, testAcsFilename).toString());
		return new ACS(u937_acs_url_v2.getPath());
	}
	
	/**
	 * Return an <code>ACS</code> pointing to u937_v1.acs in the acs_file directory in the unit tests.
	 * 
	 * @return an <code>ACS</code> pointing to u937_v1.acs
	 * @throws Exception  if there was a problem
	 */
	public static ACS getAcsV1() throws Exception {
		return getTestAcsInstance(U937_ACS_TEST_FILE1);
	}

	/**
	 * Return an <code>ACS</code> pointing to u937_v2.acs in the acs_file directory in the unit tests.
	 * 
	 * @return an <code>ACS</code> pointing to u937_v2.acs
	 * @throws Exception  if there was a problem
	 */
	public static ACS getAcsV2() throws Exception {
		return getTestAcsInstance(U937_ACS_TEST_FILE2);
	}
	
	/**
	 * Returns a new <code>FileResourceIdentifier</code> file build around the {@link TestUtils#U937_ACS_TEST_FILE2} file found in the unit test acs_files directory.
	 * 
	 * @param resourcePath the path of the new <code>FileResourceIdentifier</code>
	 * @return a new <code>FileResourceIdentifier</code>
	 * @throws Exception if there was a problem
	 */
	public static FileResourceIdentifier newFileResourceIdentifier(String resourcePath) throws Exception {
		ACS testACSFile = getTestAcsInstance(U937_ACS_TEST_FILE2);
		File testFile = TestUtils.testFile();

		TableOfContents testToc = testACSFile.createNextTableOfContents();
		
		FileResourceIdentifier testFileResourceIdentifier = testToc.createFileResourceIdentifier(resourcePath, testFile);
		
		return testFileResourceIdentifier;
	}
	
	
	/**
	 * Writes an <code>ACS</code> instance out to a temp file and returns the <code>File</code>.
	 * 
	 * @param acs the <code>ACS</code> instance to write out
	 * @return the <code>File</code> that points to the ACS container on disk
	 * @throws Exception if there was a problem
	 */
	public static File writeAcsToTempFile(ACS acs) throws Exception {
		File tempAcs = File.createTempFile("Test", ".acs");		
		acs.writeAcsContainer(tempAcs);
		
		return tempAcs;
	}
	
	/**
	 * Writes an <code>ACS</code> instance out to a temp file and reloads the ACS file from a new <code>ACS</code> instance.
	 * 
	 * @param acs the <code>ACS</code> instance to write out to a temp file
	 * @return the new <code>ACS</code> that points to the temp file from the old
	 * @throws Exception if there was a problem
	 */
	public static ACS writeAcsAndReload(ACS acs) throws Exception {
		File tempAcs = File.createTempFile("Test", ".acs");		
		acs.writeAcsContainer(tempAcs);
		
		return new ACS(tempAcs);
	}

	/**
	 * Saves a <code>TableOfContents</code>'s <code>ACS</code> to disk then reloads a copy of the <code>TableOfContents</code> from the new <code>ACS</code> instance on disk.  
	 * This is useful in making sure changes stick after an <code>ACS</code> is written to disk.
	 * 
	 * @param tableOfContents the <code>TableOfContents</code> to save a reload
	 * @return a reloaded version of the <code>TableOfContents</code>
	 * @throws Exception if something went wrong
	 */
	public static TableOfContents writeOutTableOfContentsAndReload(TableOfContents tableOfContents) throws Exception {
		int tableOfContentsVersion = tableOfContents.getVersion();
		
		// Create a new ACS instance around that temp file of the old
		ACS tempAcs = writeAcsAndReload(tableOfContents.getAcs());
		
		// Return the same fileResourceIdentifier by URI from tempAcs  
		return tempAcs.getTableOfContents(tableOfContentsVersion);
	}

	
	/**
	 * Saves FileResourceIdentifier's <code>ACS</code> to disk then reloads a copy of the FileResourceIdentifier from the new <code>ACS</code> instance on disk.  
	 * This is useful in making sure changes stick after an <code>ACS</code> is written to disk.
	 * 
	 * @param fileResourceIdentifier the <code>FileResourceIdentifier</code> to save a reload
	 * @return a reloaded version of the <code>FileResourceIdentifier</code>
	 * @throws Exception if something went wrong
	 */
	public static FileResourceIdentifier writeOutFileResourceIdentifierAndReload(FileResourceIdentifier fileResourceIdentifier) throws Exception {
		URI originalFileResourceIdentifierUri = fileResourceIdentifier.getUri();
		
		// Create a new ACS instance around that temp file of the old
		ACS tempAcs = writeAcsAndReload(fileResourceIdentifier.getAcs());
		
		// Return the same fileResourceIdentifier by URI from tempAcs  
		return tempAcs.getTableOfContents().getFileResourceIdentifierByUri(originalFileResourceIdentifierUri);
	}
}
