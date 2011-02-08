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

public class Constants {
	/** The ACS xml TOC element. */
	public static final String TOC_ELEMENT = "toc:TOC";
	
	/** The ACS xml TOC description attribute. */
	public static final String DESCRIPTION_ATTRIBUTE = "toc:description";
		
	/** The ACS xml TOC additional information attribute. */
	public static final String ADDITIONAL_INFO_ELEMENT = "toc:additional_info";

	/** The ACS xml TOC file element. */
	public static final String FILE_ELEMENT = "toc:file";

	/** The ACS xml TOC URI attribute. */
	public static final String URI_ATTRIBUTE = "toc:URI";

	/** The ACS xml TOC MIME type attribute. */
	public static final String MIME_TYPE_ATTRIBUTE = "toc:mimeType";
	
	/** The file name of the new TOC xml file used to generate new <code>TableOfContents</code> instances. */
	public static final String NEW_TOC_TEMPLATE = "new_toc_template.xml";
	
	/** The TOC prefix used to generate new <code>TableOfContents</code> xml files. */
	public static final String TOC_PREFIX = "TOC";
	
	/** The TOC suffix used to generate new <code>TableOfContents</code> xml files. */
	public static final String TOC_SUFFIX = ".xml";
	
	/** The ACS xml TOC associated attribute. */
	public static final String ASSOCIATED_ELEMENT = "toc:associated";
	
	/** The ACS xml TOC with attribute. */
	public static final String WITH_ATTRIBUTE = "toc:with";
	
	/** The ACS xml TOC relationship attribute. */
	public static final String RELATIONSHIP_ATTRIBUTE = "toc:relationship";
	
	/** The MIME type used to represent an FCS file according to the ACS specification. */ 
	public static final String FCS_FILE_MIME_TYPE = "application/vnd.isac.fcs";
	
	/** The usual file extension of and FCS file. */
	public static final String FCS_FILE_EXTENSION = ".fcs";
	
	/** The URI scheme to that represents a file. */
	public static final String FILE_SCHEME = "file";

	/** The full prefix to a URI representing a file. */
	public static final String FILE_URI = FILE_SCHEME + "://";

}
	
	
