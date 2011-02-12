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

package org.cytobank.acs.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipInputStream;

public class FileUtils {
	/** The buffer size in bytes to create when copying streams. */
	public static final int BYTE_BUFFER_SIZE = 102400;

	/**
	 * Copies one file to another.
	 * 
	 * @param sourceFile the source file to copy
	 * @param destinationFile the destination file to copy to
	 * @throws IOException If an input or output exception occurred
	 */
	public static void copy(File sourceFile, File destinationFile) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
		try {
			sourceChannel = new FileInputStream(sourceFile).getChannel();
			destinationChannel = new FileOutputStream(destinationFile).getChannel();
			destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
		}
		finally {
			if(sourceChannel != null) {
				try {
					sourceChannel.close();
				} catch (Throwable ignore) {}
			}
			if(destinationChannel != null) {
				try {
					destinationChannel.close();
				} catch (Throwable ignore) {}
			}
		}
	}
	
	/**
	 * Writes a <code>ZipInputStream</code> to a <code>File</code>.
	 * 
	 * @param zipInputStream the stream to read
	 * @param outputFile the <code>File</code> to write to
	 * @throws IOException If an input or output exception occurred
	 */
	public static void writeFileToOutputStream(ZipInputStream zipInputStream, File outputFile) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		try {
			writeInputStreamToOutputStream(zipInputStream, fileOutputStream);
		} finally {
			try {
				fileOutputStream.close();
			} catch (Throwable ignore) {}
		}
	}
	
	/**
	 * Writes an <code>InputStream</code> to an <code>OutputStream</code>.
	 * 
	 * @param inputStream the <code>InputStream</code> to read
	 * @param outputStream the <code>OutputStream</code> to write to
	 * @return the number of bytes written
	 * @throws IOException If an input or output exception occurred
	 */
	public static long writeInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {		
		final byte[] byteBuffer = new byte[BYTE_BUFFER_SIZE];
		
		int read;		
		long written = 0;
		
		while ((read = inputStream.read(byteBuffer, 0, BYTE_BUFFER_SIZE)) > 0) {
			outputStream.write(byteBuffer, 0, read); 
			written += read;
		}
		
		return written;
	}
	
	/**
	 * Read in the contents of a file and return it as a String.
	 * 
	 * @param file The <code>File</code> to read in as a String
	 * @return the resulting string
	 * @throws IOException If an input or output exception occurred
	 */
	public static String fileToString(File file) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		FileInputStream fileInputStream = new FileInputStream(file);
		
		try {
			writeInputStreamToOutputStream(fileInputStream, byteArrayOutputStream);
		} finally {
			try {
				fileInputStream.close();
			} catch (Throwable ignore) {}
		}
		
		return byteArrayOutputStream.toString();
	}
}
