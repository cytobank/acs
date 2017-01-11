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

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.cytobank.acs.util.XmlUtils;

/**
 * <code>AdditionalInfo</code> represents the additional information about a {@link TableOfContents} or a {@link FileResourceIdentifier}, as defined in the ACS specification: 
 * <i>Additional information may be provided in the Table of Contents XML file using the additional_info element. This element may be used either as a sub-element of the TOC element
 * to provide additional information related to the whole ACS, or as a sub-element of the file element to provide additional information related only to a specific file within
 * the ACS container. There may be zero, one or multiple additional_info elements related to each file in ACS. Similarly, there may be zero, one or multiple additional_info
 * elements related to the whole ACS container. As long as the Table of Contents remains a valid XML file, the contents of the additional_info element is not restricted by this
 * specification. However, the additional_info element shall not be used as replacement for describing associations describable by the associated element.</i>
 * 
 * @author Chad Rosenberg <chad@cytobank.org>
 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
 */

public class AdditionalInfo extends ElementWrapper {
	/** A constant for defining a xml tag with a tag name of "keyword". */
	public static final String KEYWORD_TAG = "keyword";

	/** A constant for defining a xml tag with an attribute of "name". */
	public static final String NAME_ATTRIBUTE = "name";

	
	/**
	 * Creates an <code>AdditionalInfo</code> instance.
	 * 
	 * @param additionalInfoElementWrapper the owner of the <code>AdditionalInfo</code> to create
	 */
	public AdditionalInfo(AdditionalInfoElementWrapper additionalInfoElementWrapper) {
		this(additionalInfoElementWrapper.createElement(Constants.ADDITIONAL_INFO_ELEMENT));
	}

	/**
	 * Creates an <code>AdditionalInfo</code> instance with info.   All <code>String</code> info data will be escaped for XML.
	 * 
	 * @param additionalInfoElementWrapper the owner of the <code>AdditionalInfo</code> to create
	 * @param info Sets the info
	 */
	public AdditionalInfo(AdditionalInfoElementWrapper additionalInfoElementWrapper, String info) {
		this(additionalInfoElementWrapper.createElement(Constants.ADDITIONAL_INFO_ELEMENT));
		appendInfo(info);
	}

	/**
	 * Creates an <code>AdditionalInfo</code> instance from an existing <code>AdditionalInfo</code> <code>org.w3c.dom.Element</code>.
	 * 
	 * @param additionalInfoElement the existing <code>AdditionalInfo</code> <code>org.w3c.dom.Element</code> that describes the <code>AdditionalInfo</code>
	 * to be created
	 */
	protected AdditionalInfo(Element additionalInfoElement) {
		this.element = additionalInfoElement;
	}
	
	/**
	 * Overwrite all existing info(s) for this <code>AdditionalInfo</code> and replace it with the
	 * <code>info</code> provided.  All <code>String</code> data will be escaped for XML.
	 * 
	 * @param info the overwrite all existing info(s) with
	 */
	public void setInfo(String info) {
		clearInfo();
		Text textNode = createTextNode(info);
		element.appendChild(textNode);
	}
	
	/**
	 * Erases all info contained within this <code>AdditionalInfo</code>.  This method does not remove this <code>AdditionalInfo</code>.
	 */
	public void clearInfo() {
		NodeList children = element.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node toRemove = children.item(i);
			element.removeChild(toRemove);
		}
	}
	
	/**
	 * Append the info to this <code>AdditionalInfo</code>.  All <code>String</code> data will be escaped for XML.
	 * 
	 * @param info the info to set
	 */
	public void appendInfo(String info) {
		Text textNode = createTextNode(info);
		element.appendChild(textNode);
	}

	/**
	 * Append <code>org.w3c.dom.Node</code> info to this <code>AdditionalInfo</code>. This methods allows arbitrary <code>org.w3c.dom.Node</code>s to be added as a child
	 * to the <code>org.w3c.dom.Element</code> that this <code>AdditionalInfo</code> represents.
	 * <p>
	 * NOTE: This method only exists as a way to add complex hierarchical data to this <code>AdditionalInfo</code> that would otherwise be impractical to create a generic API
	 * for.  The ACS specification should be observed.  No validations will be provided to check that the data within the <code>org.w3c.dom.Node</code> info parameter follows 
	 * the ACS specification.  Use this method sparingly, or consider {@link AdditionalInfo#appendTaggedInfo(String, String)} or 
	 * {@link AdditionalInfo#appendTaggedInfo(String, Map, String)} instead.
	 * 
	 * @param info the info to set in the form of a <code>org.w3c.dom.Node</code>
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public void appendInfo(Node info) {
		element.appendChild(info);
	}

	/**
	 * Returns a live copy of the <code>org.w3c.dom.Element</code> that this <code>AdditionalInfo</code> represents.  Any modifications will show up when calling {@link #toString} or
	 * when the ACS container is written out.
	 * <p>
	 * NOTE: This method only exists as a way to add or read complex hierarchical data to this <code>AdditionalInfo</code> that would otherwise be impractical to create a generic API
	 * for.  Altering this <code>org.w3c.dom.Element</code> incorrectly, such as adding attributes, can cause a violation of the ACS specification and may not be supported in this library
	 * or other ACS implementations.  Use this method sparingly, or consider {@link AdditionalInfo#appendTaggedInfo(String, String)} or 
	 * {@link AdditionalInfo#appendTaggedInfo(String, Map, String)} instead.
	 * 
	 * @return the <code>org.w3c.dom.Element</code> that this <code>AdditionalInfo</code> represents
	 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
	 */
	public Element getElement() {
		return element;
	}
	
	/**
	 * Append tagged info for this <code>AdditionalInfo</code>.  This method appends an xml <code>org.w3c.dom.Element</code> to the <code>org.w3c.dom.Element</code>
	 *  that this <code>AdditionalInfo</code> represents.
	 * 
	 * @param tagName the name of the <code>org.w3c.dom.Element</code>
	 * @param info the value of the <code>org.w3c.dom.Element</code>
	 */
	public void appendTaggedInfo(String tagName, String info) {
		appendTaggedInfo(tagName, null, info);
	}
	
	/**
	 * Append tagged info for this <code>AdditionalInfo</code> with attributes. This method appends an xml <code>org.w3c.dom.Element</code> to the <code>org.w3c.dom.Element</code>
	 * that this <code>AdditionalInfo</code> represents.
	 * 
	 * @param tagName the name of the <code>org.w3c.dom.Element</code>
	 * @param attributes a <code>java.util.Map</code> of attributes to set
	 * @param info the value of the <code>org.w3c.dom.Element</code>
	 */
	public void appendTaggedInfo(String tagName, Map<String, String> attributes, String info) {
		if (tagName == null)
			return;

		Element taggedInfoElement = createElement(tagName);
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				String value = attributes.get(key);
				taggedInfoElement.setAttribute(key, value);
			}
		}
		taggedInfoElement.setTextContent(info);
		appendInfo(taggedInfoElement);
	}

	/**
	 * Creates a "keyword" tagged info <code>org.w3c.dom.Element</code>, removes any others by the same name, then appends it to the end of the <code>org.w3c.dom.Element</code>
	 * that this <code>AdditionalInfo</code> represents.  This is just a convenience method for calling {@link AdditionalInfo#appendTaggedInfo} with a <code>tagName</code> of "keyword", and an attribute
	 * of "name" set to the parameter of <code>name</code> for easy the setting of name/value pairs within an <code>AdditionalInfo</code>.  "keyword" is not specifically defined within the ACS specification,
	 * but still falls within it.
	 * 
	 * @param name the name attribute of the keyword tag
	 * @param value the value of the keyword tag
	 */
	public void setKeyword(String name, String value) {
		if (name == null)
			return;

		removeKeyword(name);
		
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(NAME_ATTRIBUTE, name);
		
		appendTaggedInfo(KEYWORD_TAG, attributes, value);
	}
	
	/**
	 * Removes all "keyword" tagged info <code>org.w3c.dom.Element</code>s from this <code>AdditionalInfo</code> by name.
	 * 
	 * @param name the name of the keyword to remove
	 */
	public void removeKeyword(String name) {
		if (name == null)
			return;
		
		NodeList childNodes = element.getChildNodes();
		
		for (int i=0; i<childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (KEYWORD_TAG.equals(child.getNodeName()) && child.hasAttributes()) {
				NamedNodeMap attributes = child.getAttributes();
				Node nameAttribute = attributes.getNamedItem(NAME_ATTRIBUTE);
				
				if (nameAttribute == null)
					continue;
				
				String value = nameAttribute.getNodeValue();
				if (name.equals(value)) {
					element.removeChild(child);
				}
			}
		}
	}

	/**
	 * Gets the info for this <code>AdditionalInfo</code>
	 * 
	 * return an xml <code>org.w3c.dom.NodeList</code> that contains the additional info about this node
	 */
	public NodeList getInfo() {
		return element.getChildNodes();
	}

	/**
	 * Returns a <code>String</code> with all the <code>AdditionalInfo</code> contained within this instance.
	 * 
	 * return a <code>String</code> with all the <code>AdditionalInfo</code> contained within this instance
	 */	
	public String toString() {
		String result = "";

		try {
			StringBuffer stringBuffer = new StringBuffer();
			NodeList children = element.getChildNodes();
			for (int i=0; i<children.getLength(); i++) {
				Node node = children.item(i);
				String nodeString = XmlUtils.nodeToString(node);
				stringBuffer.append(nodeString);
			}

			result = stringBuffer.toString();
		} catch (TransformerException te) {
		}
		return result;
	}

}
