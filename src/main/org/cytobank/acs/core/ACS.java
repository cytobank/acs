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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.cytobank.acs.core.exceptions.AcsException;
import org.cytobank.acs.core.exceptions.DuplicateFileResourceIdentifierException;
import org.cytobank.acs.core.exceptions.InvalidArchiveException;
import org.cytobank.acs.core.exceptions.InvalidAssociationException;
import org.cytobank.acs.core.exceptions.InvalidIndexException;
import org.cytobank.acs.core.exceptions.UnexpectedVersionException;
import org.cytobank.acs.util.FileUtils;
import org.xml.sax.SAXException;


/**
 * This class is a Java implementation the Archival Cytometry Standard (ACS)
 * based on the ACS specification v1.0, 101013 Draft (<a href="http://flowcyt.sourceforge.net/acs/latest.pdf">http://flowcyt.sourceforge.net/acs/latest.pdf</a>) by the International 
 * Society for Advancement of Cytometry.
 * 
 * @author Chad Rosenberg <chad@cytobank.org>
 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
 */
public class ACS {
	
	/** A constant holding the versions of the ACS specification supported by this library. */
	protected static final String[] SUPPORTED_ACS_FILE_VERSIONS = new String[]{"1.0"};

	/** A <code>File</code> pointing to the ACS file that this <code>ACS</code> instance represents. */
	protected File acsFile;

	/** A <code>Vector<String></code> of the zip file contents of the ACS container */
	protected Vector<String> inventory = new Vector<String>();

	/** A <code>Vector<File></code> of all the temp files opened by this <code>ACS</code> instance so they can later be deleted on a close. */
	protected Vector<File> tempFiles = new Vector<File>();

	/** A <code>HashMap<Integer, TableOfContents></code> representing the <code>TableOfContents</code> indexed by version. */
	protected HashMap<Integer, TableOfContents> tablesOfContentsByVersion = new HashMap<Integer, TableOfContents>();

	/** The current highest <code>TableOfContents</code> version contained within this <code>ACS</code> instance. */
	protected int highestTableOfContentsVersion = 0;
	
	/**
	 * Creates an <code>ACS</code> instance with no contents.
	 */
	public ACS() {

	}

	/**
	 * Creates an <code>ACS</code> instance from an existing ACS container.
	 * 
	 * @param acsFilePath the <code>String</code> file path of an existing ACS container to create an <code>ACS</code> instance around.
	 * @throws FileNotFoundException If the file could not be found.
	 * @throws AcsException If there was a problem with the ACS container specified in the <code>acsFilePath</code>.
	 * @throws URISyntaxException If there was a problem with the <code>acsFilePath</code>.
	 * @throws SAXException If there was a problem parsing the xml
	 */
	public ACS(String acsFilePath) throws FileNotFoundException, AcsException, URISyntaxException, SAXException {
		this(new File(acsFilePath));
	}
			
	/**
	 * Creates an <code>ACS</code> instance from an existing ACS container.
	 * 
	 * @param acsFile the <code>File</code> pointing to an existing ACS container to create an <code>ACS</code> instance around.
	 * @throws FileNotFoundException If the file could not be found.
	 * @throws AcsException If there was a problem with the ACS container specified in the <code>acsFilePath</code>.
	 * @throws URISyntaxException If there was a problem with the <code>acsFilePath</code>.
	 * @throws SAXException If there was a problem parsing the xml
	 */
	public ACS(File acsFile) throws FileNotFoundException, AcsException, URISyntaxException, SAXException {
		this.acsFile = acsFile;
		setupInventory();
	}
	
	/**
	 * Gets the version number of highest version of a <code>TableOfContents</code> that is contained within this <code>ACS</code> instance.
	 * 
	 * @return the version number of the highest version of a <code>TableOfContents</code> that is contained within this <code>ACS</code> instance.
	 * @see TableOfContents
	 */
	public int getCurrentVersion() {
		return highestTableOfContentsVersion;
	}
	
	/**
	 * Gets the instance of the highest version of a <code>TableOfContents</code> that is contained within this <code>ACS</code> instance.
	 * <p>
	 * NOTE: instances of <code>TableOfContents</code> should be treated as immutable. If a modification needs to occur then the {@link #addTableOfContents(TableOfContents)}
	 * should be called and the modifications should occur on that instance.  Modifications to <code>TableOfContents</code> instances received from this
	 * method should be used sparingly and generally avoided, as per the ACS specification on versioning.
	 * 
	 * @return the instance of the highest version of a <code>TableOfContents</code> that is contained within this <code>ACS</code> instance.
	 * @see ACS#addTableOfContents(TableOfContents)
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public TableOfContents getTableOfContents() {
		return tablesOfContentsByVersion.get(highestTableOfContentsVersion);
	}

	/**
	 * Gets an instance of <code>TableOfContents</code> by a specific version number.
	 * <p>
	 * NOTE: instances of <code>TableOfContents</code> should be treated as immutable. If a modification needs to occur then the <code>addTableOfContents</code>
	 * should be called and the modifications should occur on that instance.  Modifications to <code>TableOfContents</code> instances received from this
	 * method should be used sparingly and generally avoided, as per the ACS specification on versioning.
	 * 
	 * @param version the version number of the <code>TableOfContents</code>
	 * @return a <code>TableOfContents</code> by a specific version number or <code>null</code> if the version number could not be found. 
	 */
	public TableOfContents getTableOfContents(int version) {
		return tablesOfContentsByVersion.get(version);
	}
	
	/**
	 * Gets a <code>String</code> array of the zip file contents of the ACS container.
	 * 
	 * @return zip file contents
	 */
	public String[] getInventory() {
		String[] results = new String[inventory.size()];
		
		inventory.toArray(results);
		return results;
	}
	
	/**
	 * Writes out a file contained within this <code>ACS</code> container to the specified target <code>File</code>.
	 * 
	 * @param uri the uri representing a file within this <code>ACS</code> instance.
	 * @param targetFile the file to write the requested file to
	 * @throws IOException If an input or output exception occurred
	 */
	public void extractFile(URI uri, File targetFile) throws IOException {
		// Check that this is a file:/// so that we know the uri exists within
		// this zip
		if (Constants.FILE_SCHEME.equals(uri.getScheme()))
			extractFile(uri.getPath(), targetFile);
	}
	
	/**
	 * Writes out a file contained within this <code>ACS</code> container to the specified target <code>File</code>.
	 * 
	 * @param filePath the <code>String</code> path representing a file within this <code>ACS</code> instance.
	 * @param outputFile the <code>File</code> to write the requested file to
	 * @throws IOException If an input or output exception occurred
	 */
	public void extractFile(String filePath, File outputFile) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		extractFile(filePath, fileOutputStream);
		fileOutputStream.close();
	}
	
	/**
	 * Writes out a file contained within this <code>ACS</code> container to a <code>String</code>.  This method is provided as a convenience to be able
	 * to work with small text files without the hassle of writing them out to disk first.  It should not be used with large files or FCS files.
	 * 
	 * @param uri the uri representing a file within this <code>ACS</code> instance.
	 * @throws IOException If an input or output exception occurred
	 */
	public String extractFileToString(URI uri) throws IOException {
		if (!Constants.FILE_SCHEME.equals(uri.getScheme()))
			return null;
		
		return extractFileToString(uri.getPath());
	}
	
	/**
	 * Writes out a file contained within this <code>ACS</code> container to a <code>String</code>.  This method is provided as a convenience to be able
	 * to work with small text files without the hassle of writing them out to disk first.  It should not be used with large files or FCS files.
	 * 
	 * @param filePath the <code>String</code> path representing a file within this <code>ACS</code> instance.
	 * @throws IOException If an input or output exception occurred
	 */
	public String extractFileToString(String filePath) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		extractFile(filePath, byteArrayOutputStream);
		
		return byteArrayOutputStream.toString();
	}
	
	/**
	 * Writes out a file contained within this <code>ACS</code> container to the specified <code>OutputStream</code>.
	 * 
	 * @param uri the uri representing a file within this <code>ACS</code> instance.
	 * @param outputStream to write the requested file to
	 * @throws IOException If an input or output exception occurred
	 */
	public void extractFile(URI uri, OutputStream outputStream) throws IOException {
		if (Constants.FILE_SCHEME.equals(uri.getScheme()))
			extractFile(uri.getPath(), outputStream);
	}
	
	/**
	 * Writes out a file contained within this <code>ACS</code> instance to the specified <code>OutputStream</code>.
	 * 
	 * @param filePath the <code>String</code> path representing a file within this <code>ACS</code> instance.
	 * @param outputStream to write the requested file to
	 * @throws IOException If an input or output exception occurred
	 */
	public void extractFile(String filePath, OutputStream outputStream) throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(acsFile));
		
		if (filePath.startsWith("/"))
			filePath = filePath.substring(1);
		
		ZipEntry zipEntry;
		while ((zipEntry = zipInputStream.getNextEntry()) != null) {			
			String name = zipEntry.getName();
			
			if (name.equals(filePath) && !zipEntry.isDirectory()) {			
				FileUtils.writeInputStreamToOutputStream(zipInputStream, outputStream);
				break;	
			}
			
			zipInputStream.closeEntry();
		}
	}
	
	/**
	 * Parses the table of contents version number from a table of contents xml file as per the ACS specification.
	 * 
	 * @param tocFilename the name of a file to parse.  It does not have to exist or accessible.
	 * @return the version number of the table of contents xml file
	 * @throws InvalidIndexException If the file version could not be parsed, or does not conform to the spec.
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public static int parseTocVersion(String tocFilename) throws InvalidIndexException {
		String version = tocFilename.substring(Constants.TOC_PREFIX.length(), tocFilename.length() - Constants.TOC_SUFFIX.length());
		try {
			return Integer.parseInt(version);
		} catch (NumberFormatException nfe) {
			throw new InvalidIndexException("An invaid index version number was found on TOC file: " + tocFilename);
		}
	}
	
	/**
	 * Adds a <code>TableOfContents</code> to <code>ACS</code> instance, the version of which will be set from the version contained within the 
	 * {@link TableOfContents#getVersion()} method and must be one greater than the maximum version contained within this <code>ACS</code> instance.
	 * 
	 * @param tableOfContents the <code>TableOfContents</code> to add
	 * @throws UnexpectedVersionException If the table of <code>TableOfContents</code> version is not one greater than the maximum version contained within this <code>ACS</code> instance.
	 * @see TableOfContents#getVersion
	 */
	public void addTableOfContents(TableOfContents tableOfContents) throws UnexpectedVersionException {
		int expectedVersion = getCurrentVersion() + 1;
		int version  = tableOfContents.getVersion();
		if (version != expectedVersion)
			throw new UnexpectedVersionException("Expected TableOfContents version: " + expectedVersion + ", got: " + version);

		tableOfContents.setAcs(this);

		tablesOfContentsByVersion.put(version, tableOfContents);
		highestTableOfContentsVersion = version;
	}
	
	/**
	 * Creates a new <code>TableOfContents</code> with the version number set to one higher than the maximum version contained within this <code>ACS</code> instance.
	 * If this instance of <code>ACS</code> contains any <code>TableOfContents</code> instances then the new <code>TableOfContents</code> returned will contain
	 * all the contents of the <code>TableOfContents</code> with the previous highest version number.  If no <code>TableOfContents</code>s previously existed, the newly created
	 * <code>TableOfContents</code> will have a version of <code>1</code>.
	 * <p>
	 * NOTE: This is the preferred method for creating and working with <code>TableOfContents</code> instances.
	 * 
	 * @return a new instance of <code>TableOfContents</code>
	 */
	public TableOfContents createNextTableOfContents() {
		try {			
			TableOfContents result = null;
			
			if (highestTableOfContentsVersion == 0)
				result = TableOfContents.newInstance();
			else
				result = getTableOfContents().nextVersion();
			
			addTableOfContents(result);

			return result;
		} catch (Exception ignore) {
			return null;
		}
	}
	
	/**
	 * Writes out this <code>ACS</code> container to a <code>String</code> file path.  If this instance was created from an existing ACS container, that file will not be modified and only 
	 * the contents specified within the previous ACS container's <code>TableOfContents</code>s' <code>FileResourceIdentifier</code>s will be copied over, in addition
	 * to a new <code>TableOfContents</code>'s <code>FileResourceIdentifier</code>s created.
	 * <p>
	 * NOTE: The current implementation only allows one call to <code>writeAcsContainer</code> to be made since the {@link FileResourceIdentifier#sourceFileStream} will
	 * have already been read from and exhausted.
	 * 
	 * @param filePath The <code>String</code> file path to write the ACS container to.  This should not be original ACS container that this <code>ACS</code> instance
	 * is based in (if there is one).
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code>
	 */
	public void writeAcsContainer(String filePath) throws IOException, InvalidIndexException, URISyntaxException {		
		File targetFile = new File(filePath);		
		writeAcsContainer(targetFile);
	}
	
	/**
	 * Writes out this <code>ACS</code> container to a <code>File</code>.  If this instance was created from an existing ACS container, that file will not be modified and only 
	 * the contents specified within the previous ACS container's <code>TableOfContents</code>s' <code>FileResourceIdentifier</code>s will be copied over, in addition
	 * to a new <code>TableOfContents</code>'s <code>FileResourceIdentifier</code>s created.
	 * <p>
	 * NOTE: The current implementation only allows one call to <code>writeAcsContainer</code> to be made since the {@link FileResourceIdentifier#sourceFileStream} will
	 * have already been read from and exhausted.
	 * 
	 * @param targetFile The <code>File</code> to write the ACS container to.  This should not be original ACS container that this <code>ACS</code> instance
	 * is based in (if there is one).
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code>
	 */
	public void writeAcsContainer(File targetFile) throws IOException, InvalidIndexException, URISyntaxException {		
		FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
		try {
			writeAcsContainer(fileOutputStream);
		} finally {
			fileOutputStream.close();
		}
	}
	
	/**
	 * Writes out this <code>ACS</code> container to an <code>OutputStream</code>.  If this instance was created from an existing ACS container, that file will not be modified and only 
	 * the contents specified within the previous ACS container's <code>TableOfContents</code>s' <code>FileResourceIdentifier</code>s will be copied over, in addition
	 * to a new <code>TableOfContents</code>'s <code>FileResourceIdentifier</code>s created.
	 * <p>
	 * NOTE: The current implementation only allows one call to <code>writeAcsContainer</code> to be made since the {@link FileResourceIdentifier#sourceFileStream} will
	 * have already been read from and exhausted.
	 * 
	 * @param outputStream The <code>OutputStream</code> to write the ACS container to.  This should not be original ACS container that this <code>ACS</code> instance
	 * is based in (if there is one).
	 * @throws IOException If an input or output exception occurred
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code> 
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code>
	 */
	public void writeAcsContainer(OutputStream outputStream) throws IOException, InvalidIndexException, URISyntaxException {		
		// TODO worry about rewriting the same file and sourceInputStreams pointers being at eof
		// TODO only use lower case names in the zip file or make sure inventory.contains(filePath) can
		// work around mixed case
		// TODO write starting the the highest TableOfContents version down to the lowest

		ZipOutputStream zipArchiveOutputStream = new ZipOutputStream(outputStream);

		try {
			HashSet<String> writtenPaths = new HashSet<String>();

			// Write out each TableOfContents and their respective fileResourceIdentifiers contents to the target zip file
			for (TableOfContents tableOfContents : tablesOfContentsByVersion.values()) {
				ZipEntry zipEntry = new ZipEntry(tableOfContents.getFileName());
				zipArchiveOutputStream.putNextEntry(zipEntry);
				tableOfContents.writeXml(zipArchiveOutputStream);

				// Write each fileResourceIdentifiers contents to the target zip file
				for (FileResourceIdentifier fileResourceIdentifier : tableOfContents.getFileResourceIdentifiers()) {
					// External resources - anything without a file:// (url:// and the like) - are tracked outside
					// the zip file and don't need to be added
					if (fileResourceIdentifier.isExternalReference()) 
						continue;

					String filePath = fileResourceIdentifier.getAcsFilePath();

					// Strip the starting '/' off the filePath, if it has one
					if (filePath.startsWith("/"))
						filePath = filePath.substring(1);

					// Don't re-write files with the same filePath
					if (writtenPaths.contains(filePath))
						continue;

					// If we've made it here then the filePath needs to be added to the new zip file
					// by creating a ZipEntry for it.
					ZipEntry fileResourceZipEntry = new ZipEntry(filePath);
					zipArchiveOutputStream.putNextEntry(fileResourceZipEntry);

					// Write the file resource to new zip archive.  fileResourceIdentifier will figure
					// out how to deal with a file that is in an old archive or a newly added file
					fileResourceIdentifier.writeRepresentedFile(zipArchiveOutputStream);

					// Track that this filePath was already written to so we don't have any duplicates/overwrites
					writtenPaths.add(filePath);
				}
			}
		} finally {
			if (zipArchiveOutputStream != null)
				zipArchiveOutputStream.close();
		}
	}
	
	
	/**
	 * Deletes and references to temp files, and closes any open <code>InputStreams</code> held in {@link FileResourceIdentifier}s.  While temp files would be deleted on VM exit,
	 * it is not assumed that will happen when any given <code>ACS</code> instance is no longer needed.
	 * @see TableOfContents#close()
	 * @see FileResourceIdentifier#close()
	 */
	public void close() {
		File file = null;
		while ((file = (File) tempFiles.listIterator()) != null) {
			try {
				file.delete();
			} catch (Throwable ignore) {}
		}
		
		for (TableOfContents tableOfContents : tablesOfContentsByVersion.values())
			tableOfContents.close();
	}
	
	/**
	 * Sets up the inventory in this <code>ACS</code> instance from an ACS container on disk.
	 * 
	 * @throws FileNotFoundException If the ACS container was not found
	 * @throws AcsException If a problem occurred with versions or tables of contents
	 * @throws URISyntaxException If a problem occurred parsing URIs
	 * @throws SAXException If there was a problem parsing the xml
	 */
	protected void setupInventory() throws FileNotFoundException, AcsException, URISyntaxException, SAXException {
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(acsFile));
		
		try {
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {			
				
				if (zipEntry.isDirectory())
					continue;
				
				String name = zipEntry.getName();
				if (name.startsWith(Constants.TOC_PREFIX) && name.endsWith(Constants.TOC_SUFFIX)) {
					int tocVersion =  parseTocVersion(name);
					TableOfContents tableOfContents = createTableOfContents(zipInputStream, tocVersion);	
					tablesOfContentsByVersion.put(tocVersion, tableOfContents);
					
					if (highestTableOfContentsVersion < tocVersion)
						highestTableOfContentsVersion = tocVersion;
				}
					
				inventory.add(name);
			}
			zipInputStream.close();
		} catch (IOException ioe) {
			try {
				zipInputStream.close();
			} catch (IOException ignore) {}
			throw new InvalidArchiveException(ioe.toString());
		}
	}

	/**
	 * Creates a <code>TableOfContents</code> from a <code>ZipInputStream</code> for use with building inventories.
	 * 
	 * @param zipInputStream The <code>InputStream</code> to read
	 * @param tocVersion The version of the <code>TableOfContents</code> to be created
	 * @return the created <code>TableOfContents</code>
	 * @throws InvalidIndexException If there is a problem with one of the <code>TableOfContents</code> 
	 * @throws IOException If an input or output exception occurred
	 * @throws URISyntaxException If there is a problem with any of the URIs contained within the <code>TableOfContents</code>
	 * @throws InvalidAssociationException if there is an invalid association
	 * @throws DuplicateFileResourceIdentifierException If any of the URIs contained within the <code>TableOfContents</code> is a duplicate 
	 * @throws SAXException If there was a problem parsing the xml
	 */
	protected TableOfContents createTableOfContents(ZipInputStream zipInputStream, int tocVersion) throws InvalidIndexException, IOException, URISyntaxException, InvalidAssociationException, DuplicateFileResourceIdentifierException, SAXException {
		File tocFile = tempFile();

		FileUtils.writeFileToOutputStream(zipInputStream, tocFile);
		return new TableOfContents(tocFile, this, tocVersion);
	}
	
	/**
	 * Creates a temp file that will be deleted when {@link ACS#close()} is called.
	 * 
	 * @return a new temp <code>File</code>
	 * @throws IOException If an input or output exception occurred
	 */
	
	protected File tempFile() throws IOException {
		File file = File.createTempFile("acs", "tmp");
		tempFiles.add(file);
		return file;
	}
	
	/**
	 * Returns a <code>String</code> array of the versions of the ACS specification supported by this library.
	 * 
	 * @return a <code>String</code> array of the versions of the ACS specification supported by this library
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public static String[] supportedAcsVersions() {
		return SUPPORTED_ACS_FILE_VERSIONS;
	}

}
