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

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is a base class that extends <code>ElementWrapper</code> but also provides the ability to track additional information
 * about the instance.
 * 
 * @author Chad Rosenberg
 *
 */
public abstract class AdditionalInfoElementWrapper extends ElementWrapper {
	/** The <code>AdditionalInfo</code> about this <code>TableOfContents</code>. */
	protected Vector<AdditionalInfo> additionalInfos;

	/**
	 * A setup method to initialize the variable {@link AdditionalInfoElementWrapper#additionalInfos}
	 * that also sets up the <code>AdditionalInfo</code> by parsing the xml, if available.
	 */
	protected void setupAdditionalInfo() {
		NodeList additionalInfoNodes = element.getElementsByTagName(Constants.ADDITIONAL_INFO_ELEMENT);

		additionalInfos = new Vector<AdditionalInfo>();
		
		int numberOfAdditionalInfos = (additionalInfoNodes == null) ? 0 : additionalInfoNodes.getLength();
		
		additionalInfos = new Vector<AdditionalInfo>(numberOfAdditionalInfos);
		
		for (int i=0; i<numberOfAdditionalInfos; i++) {
			Element additionalInfoElement = (Element) additionalInfoNodes.item(i);
			AdditionalInfo additionalInfo = new AdditionalInfo(additionalInfoElement);
			trackAdditionalInfo(additionalInfo);
		}

	}
	
	
	/**
	 * Adds additional info to this instance.
	 * <p>
	 * As defined in the ACS specification: <i>Additional information may be provided in the Table of Contents XML file using the additional_info element. This element may be used either as a sub-element of the TOC element
	 * to provide additional information related to the whole ACS, or as a sub-element of the file element to provide additional information related only to a specific file within
	 * the ACS container. There may be zero, one or multiple additional_info elements related to each file in ACS. Similarly, there may be zero, one or multiple additional_info
	 * elements related to the whole ACS container. As long as the Table of Contents remains a valid XML file, the contents of the additional_info element is not restricted by this
	 * specification. However, the additional_info element shall not be used as replacement for describing associations describable by the associated element.</i>
	 * 
	 * @param info a <code>String</code> of additional info to add to this instance
	 * @return the newly created additionalInfo
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public AdditionalInfo addAdditionalInfo(String info) {
		AdditionalInfo additionalInfo = new AdditionalInfo(this, info);
		
		trackAdditionalInfo(additionalInfo);		
		element.appendChild(additionalInfo.element);
		
		return additionalInfo;
	}
	
	/**
	 * Track an <code>AdditionalInfo</code> against this instance.  This does not add the <code>AdditionalInfo</code> xml to
	 * the <code>org.w3c.dom.Element</code>, but only to the {@link AdditionalInfoElementWrapper#additionalInfos} <code>Vector<AdditionalInfo></code>.
	 * 
	 * @param additionalInfo The <code>AdditionalInfo</code> to track
	 */
	protected void trackAdditionalInfo(AdditionalInfo additionalInfo) {
		additionalInfos.add(additionalInfo);
	}
	
	/**
	 * No longer track an <code>AdditionalInfo</code> against this instance.  This does not remove the <code>AdditionalInfo</code> xml from
	 * the <code>org.w3c.dom.Element</code>, but only from the {@link AdditionalInfoElementWrapper#additionalInfos} <code>Vector<AdditionalInfo></code>.
	 * 
	 * @param additionalInfo The <code>AdditionalInfo</code> to track
	 * @return <code>true</code> if the <code>AdditionalInfo</code> instance was successfully removed, <code>false</code> otherwise
	 */
	protected boolean untrackAdditionalInfo(AdditionalInfo additionalInfo) {
		return additionalInfos.remove(additionalInfo);
	}
	
	/**
	 * Gets additional info about this instance.
	 * <p>
	 * As defined in the ACS specification: <i>Additional information may be provided in the Table of Contents XML file using the additional_info element. This element may be used either as a sub-element of the TOC element
	 * to provide additional information related to the whole ACS, or as a sub-element of the file element to provide additional information related only to a specific file within
	 * the ACS container. There may be zero, one or multiple additional_info elements related to each file in ACS. Similarly, there may be zero, one or multiple additional_info
	 * elements related to the whole ACS container. As long as the Table of Contents remains a valid XML file, the contents of the additional_info element is not restricted by this
	 * specification. However, the additional_info element shall not be used as replacement for describing associations describable by the associated element.</i>
	 * 
	 * @return an array of <code>AdditionalInfo</code>(s) about this instance
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public AdditionalInfo[] getAdditionalInfo() {
		AdditionalInfo[] results = new AdditionalInfo[additionalInfos.size()];
		additionalInfos.toArray(results);
		return results;
	}
	
	/**
	 * Removes a specific <code>AdditionalInfo</code> instance from this instance.
	 *
	 * @param additionalInfo the <code>AdditionalInfo</code> to remove 
	 * @return <code>true</code> if the <code>AdditionalInfo</code> instance was successfully removed, <code>false</code> otherwise
	 */
	public boolean removeAdditionalInfo(AdditionalInfo additionalInfo) {
		boolean success = untrackAdditionalInfo(additionalInfo);
  
	    if (success) {
			// Remove the additionalInfo from the xml 
	    	success &= removeElementWrapper(additionalInfo);
	    }
	    	
		return success;
	}
}
