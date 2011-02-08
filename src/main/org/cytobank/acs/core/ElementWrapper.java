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
import org.w3c.dom.Node;

/**
 * A simple base class that wraps and abstracts an </code>org.w3c.dom.Element</code>. 
 *  
 * @author Chad Rosenberg <chad@cytobank.org>
 * @see org.w3c.dom.Element
 */
public abstract class ElementWrapper {
	/** The wrappedElement. */
	protected Element element;
	
	/**
	 * Gets the <code>String</code> value of an attribute for the specified attribute name.
	 *
	 * @param attributeName the attribute name
	 * @return The value or </code>null</code> if an attribute by that name cannot be found. 
	 */
	protected String getAttributeValue(String attributeName) {
		return element.getAttribute(attributeName);
	}
	
	/**
	 * Sets an attribute value by name.
	 * 
	 * @param attributeName the attribute name
	 * @param value the value to set
	 */
	protected void setAttributeValue(String attributeName, String value) {
		element.setAttribute(attributeName, value);
	}
	
	/**
	 * Removes an attribute by name.
	 * 
	 * @param attributeName the attribute name to remove
	 */
	protected void removeAttribute(String attributeName) {
		element.removeAttribute(attributeName);
	}
	
	/**
	 * Removes an <code>ElementWrapper</code>'s element from this <code>ElementWrapper</code>'s wrapped element.
	 * 
	 * @param elementWrapper <code>ElementWrapper</code> to remove
	 * @return <code>true</code> if the element was removed, <code>false</code> otherwise
	 */
	protected boolean removeElementWrapper(ElementWrapper elementWrapper) {
		Node node = element.removeChild(elementWrapper.element);
		return (node != null);
	}
	
	/**
	 * Creates a new xml element at the top level of this <code>ElementWrapper</code>'s
	 * xml document.
	 * 
	 * @param name the name of the element to create
	 * @return the created element
	 */
	protected Element createElement(String name) {
		return element.getOwnerDocument().createElement(name);
	}
}
