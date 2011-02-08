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

import org.w3c.dom.Element;

public class AdditionalInfo extends ElementWrapper {
	public AdditionalInfo(AdditionalInfoElementWrapper additionalInfoElementWrapper, String info) {
		this(additionalInfoElementWrapper.createElement(Constants.ADDITIONAL_INFO_ELEMENT));
		setInfo(info);
	}

	protected AdditionalInfo(Element additionalInfoElement) {
		this.element = additionalInfoElement;
	}
	
	/**
	 * Sets the info for this <code>AdditionalInfo</code>
	 * 
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		element.setTextContent(info);
	}

	/**
	 * Gets the info for this <code>AdditionalInfo</code>
	 * 
	 * return info the info to set
	 */
	public String getInfo() {
		return element.getTextContent();
	}

	/**
	 * Gets the info for this <code>AdditionalInfo</code>
	 * 
	 * return info the info to set
	 */	
	public String toString() {
		return getInfo();
	}

}
