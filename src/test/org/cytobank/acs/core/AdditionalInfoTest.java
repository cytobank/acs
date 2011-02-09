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

import java.util.HashMap;

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
		assertEquals("AdditionalInfo.toString should return the set additional info text", testText, additionalInfo.toString());
		
		String newText = "This is my new text";
		additionalInfo.setInfo(newText);
		assertEquals("AdditionalInfo.toString should return the set additional info text", newText, additionalInfo.toString());
		
		FileResourceIdentifier reloadedFileResourceIdentifier = TestUtils.writeOutFileResourceIdentifierAndReload(fileResourceIdentifier);
		AdditionalInfo reloadedAdditionalInfo = reloadedFileResourceIdentifier.getAdditionalInfo()[0];

		assertEquals("AdditionalInfo.toString should return the set additional info text", newText, reloadedAdditionalInfo.toString());
	}

	@Test
	public void testEmbededTags() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		
		String testText = "This is information about a file.";
		
		AdditionalInfo additionalInfo = fileResourceIdentifier.addAdditionalInfo();
		assertEquals("An empty AdditionalInfo.toString() should return an empty String", "", additionalInfo.toString());

		additionalInfo.appendInfo(testText);
		
		assertEquals("AdditionalInfo.toString should return the set additional info text", testText, additionalInfo.toString());

		additionalInfo.appendTaggedInfo("foo", "stuff");

		String taggedInfo = "<foo>stuff</foo>";
		
		assertEquals("AdditionalInfo.toString should return the set additional tagged info", testText + taggedInfo, additionalInfo.toString());
		
		HashMap<String, String> attributes = new HashMap<String, String>();
		
		attributes.put("bar", "baz");
		
		additionalInfo.appendTaggedInfo("foo", attributes, "stuff");
		
		String attributedTaggedInfo = "<foo bar=\"baz\">stuff</foo>";
				
		assertEquals("AdditionalInfo.toString should return the set additional tagged info with attributes", testText + taggedInfo + attributedTaggedInfo, additionalInfo.toString());
		
		additionalInfo.setKeyword("bird", "parakeet");
		
		String keywordInfo = "<keyword name=\"bird\">parakeet</keyword>";

		assertEquals("AdditionalInfo.toString should return the set additional keyword info", testText + taggedInfo + attributedTaggedInfo + keywordInfo, additionalInfo.toString());
	}
	
	@Test
	public void testSetAndRemoveKeywords() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		
		String testText = "This is information about a file.";
		
		AdditionalInfo additionalInfo = fileResourceIdentifier.addAdditionalInfo();
		assertEquals("An empty AdditionalInfo.toString() should return an empty String", "", additionalInfo.toString());

		additionalInfo.appendInfo(testText);
		
		assertEquals("AdditionalInfo.toString should return the set additional info text", testText, additionalInfo.toString());

		additionalInfo.setKeyword("bird", "parakeet");
		
		String parakeetKeywordInfo = "<keyword name=\"bird\">parakeet</keyword>";

		assertEquals("AdditionalInfo.toString should return the set additional keyword info", testText + parakeetKeywordInfo, additionalInfo.toString());
		
		additionalInfo.removeKeyword("bird");
		
		additionalInfo.setKeyword("bird", "parakeet");

		assertEquals("AdditionalInfo.toString should return the set additional keyword info", testText + parakeetKeywordInfo, additionalInfo.toString());

		additionalInfo.setKeyword("bird", "eagle");

		String eagleKeywordInfo = "<keyword name=\"bird\">eagle</keyword>";

		assertEquals("AdditionalInfo.setKeyword should overwrite previous keywords by the same name", testText + eagleKeywordInfo, additionalInfo.toString());
	}


	@Test
	public void testClearInfo() throws Exception {
		FileResourceIdentifier fileResourceIdentifier = TestUtils.newFileResourceIdentifier(MY_NEW_FILE_PATH);
		
		String testText = "This is information about a file.";
		
		AdditionalInfo additionalInfo = fileResourceIdentifier.addAdditionalInfo();
		assertEquals("An empty AdditionalInfo.toString() should return an empty String", "", additionalInfo.toString());

		additionalInfo.appendInfo(testText);
		
		assertEquals("AdditionalInfo.toString should return the set additional info text", testText, additionalInfo.toString());

		additionalInfo.clearInfo();

		assertEquals("AdditionalInfo.toString should return an empty String after AdditionalInfo.clearInfo() is called", "", additionalInfo.toString());
	}
	
	@After
	public void tearDown() throws Exception {
	}


}
