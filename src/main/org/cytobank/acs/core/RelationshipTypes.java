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

import org.apache.commons.lang.StringUtils;

/**
 * A constants class that defines the association types between <code>FileResourceIdentifier</code>s, as defined in the ACS specification:
 * <p>
 * <i>The value of the relationship attribute shall indicate the type of relation between the two files. ISAC 
 * is maintaining a registry of relation types (see below). If possible, one of the pre-defined relations should be http://flowcyt.sf.net/acs/latest.pdf  ACS – the Archival Cytometry Standard 
 * Version 1.0, 101013 Warning: This is a Draft of an ISAC Candidate Recommendation  11
 * used using the exact relationship wording. If there is no suitable relationship for the required association in 
 * the ISAC registry, please submit a relationship type to the ISAC Data Standards Task Force; see 
 * http://www.isac-net.org for contact details and the updated contents of the registry.</i>
 * 
 * @author Chad Rosenberg <chad@cytobank.org>
 * @see <a href="http://flowcyt.sourceforge.net/acs/latest.pdf">Archival Cytometry Standard specification</a>
 */
public class RelationshipTypes {
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a data file (e.g., a list mode data file) to a gating description file, such 
	 * as a Gating-ML file. This relationship indicates that gates described in a gating 
	 * description file are applicable to data in the data file.</i> 
	 */
	public static final String GATING_DESCRIPTION                   = "gating description";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a list mode data file to an  external file describing the fluorescence 
     * compensation that is supposed to be applied on data stored in the list mode data file. 
     * In this case, data in the list mode data file shall be stored uncompensated. This 
     * relation shall not be used if data is stored compensated already. Also, this relation is 
     * not necessary if compensation details are included as part of the list mode data file, 
     * e.g., in the $SPILLOVER keyword according to the FCS 3.1 specification [3].</i>
     */
	public static final String COMPENSATION_DESCRIPTION             = "compensation description";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from an uncompensated  data file to a compensated version of the same 
     * data.</i>
	 */
	public static final String COMPENSATED_VERSION                  = "compensated version";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a data file (e.g.,  a list mode data file) to a file with classification 
     * results, such as event-based classification (e.g., results of gating) of a list mode data 
     * file.</i>
	 */
	public static final String CLASSIFICATION_RESULTS               = "classification results";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a data file to a project (sometimes also called workspace) description 
     * file of analytical software used to analyze data in the data file.</i>
	 */
	public static final String PROJECT_WORKSPACE                    = "project/workspace";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a data file to a file describing the instrument settings used for 
     * acquisition of these data.</i>
	 */
	public static final String INSTRUMENTATION_SETTINGS_DESCRIPTION = "instrumentation settings description";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a data file to a file describing the sample (or specimen) that has been 
     * used to generate data in the data file.</i> 
	 */
	public static final String SAMPLE_SPECIMEN_DESCRIPTION          = "sample specimen description";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a data file to a file describing the methodology of the data analysis.</i> 
	 */
	public static final String ANALYSIS_DESCRIPTION                 = "analysis description";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a data file to a file describing results of the data analysis.</i>
	 */
	public static final String RESULTS_DESCRIPTION                  = "results description";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from a data file to a related publication. A Uniform Resource Name 
     * (URN) may be used in the with attribute to specify the referenced publication.</i>
	 */
	public static final String RELATED_PUBLICATION                  = "related publication";
	
	/**
	 * A <code>String</code> that describes an ACS <i>Relation from file being signed to a file with a detached digital signature provided 
     * outside of the TOC file but still within the ACS container. The format of this 
     * signature file is not specified by ACS specification; external encryption standards 
     * may be used.</i>
	 */
	public static final String DIGITAL_SIGNATURE                    = "digital signature";
	
	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "gating description" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "gating description" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#GATING_DESCRIPTION
	 */
	public static boolean isGatingDescription(String relationshipType) {		
		return testAssociationType(GATING_DESCRIPTION, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "compensation description" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "compensation description" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#COMPENSATION_DESCRIPTION
	 */
	public static boolean isCompensationDescription(String relationshipType) {
		return testAssociationType(COMPENSATION_DESCRIPTION, relationshipType);
	}
	
	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "compensated version" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "compensated version" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#COMPENSATED_VERSION
	 */
	public static boolean isCompensatedVersion(String relationshipType) {
		return testAssociationType(COMPENSATED_VERSION, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "classification results" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "classification results" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#CLASSIFICATION_RESULTS
	 */
	public static boolean isClassificationResults(String relationshipType) {
		return testAssociationType(CLASSIFICATION_RESULTS, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "project workspace" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "project workspace" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#PROJECT_WORKSPACE
	 */
	public static boolean isProjectWorkspace(String relationshipType) {
		return testAssociationType(PROJECT_WORKSPACE, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "instrumentation settings description" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "instrumentation settings description" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#INSTRUMENTATION_SETTINGS_DESCRIPTION
	 */
	public static boolean isInstrumentationSettingsDescription(String relationshipType) {
		return testAssociationType(INSTRUMENTATION_SETTINGS_DESCRIPTION, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "sample specimen description" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "sample specimen description" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#SAMPLE_SPECIMEN_DESCRIPTION
	 */
	public static boolean isSampleSpecimenDescription(String relationshipType) {
		return testAssociationType(SAMPLE_SPECIMEN_DESCRIPTION, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "analysis description" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "analysis description" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#ANALYSIS_DESCRIPTION 
	 */
	public static boolean isAnalysisDescription(String relationshipType) {
		return testAssociationType(ANALYSIS_DESCRIPTION, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "results description" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "results description" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#RESULTS_DESCRIPTION
	 */
	public static boolean isResultsDescription(String relationshipType) {
		return testAssociationType(RESULTS_DESCRIPTION, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "related publication" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "related publication" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#RELATED_PUBLICATION
	 */
	public static boolean isRelatedPublication(String relationshipType) {
		return testAssociationType(RELATED_PUBLICATION, relationshipType);
	}

	/**
	 * Returns <code>true</code> if the specified <code>String</code> is an ACS "digital signature" relationship.
	 * 
	 * @param relationshipType the <code>String</code> to test
	 * @return <code>true</code> if the specified <code>String</code> is an ACS "digital signature" relationship, <code>false</code> otherwise
	 * @see RelationshipTypes#DIGITAL_SIGNATURE
	 */
	public static boolean isDigitalSignature(String relationshipType) {
		return testAssociationType(DIGITAL_SIGNATURE, relationshipType);
	}

	/** 
	 * Returns <code>true</code> if one <code>String</code> matches another after dropping case and stripping off surrounding white spaces.
	 * @param relationshipTypeConstant a <code>String</code> representing an <code>RelationshipType</code> constant 
	 * @param relationshipType a <code>String</code> to test if it the same as the <code>RelationshipType</code> constant
	 * @return <code>true</code> if they match, <code>false</code> otherwise
	 */
	public static boolean testAssociationType(String relationshipTypeConstant, String relationshipType) {
		relationshipType = StringUtils.strip(relationshipType);
		
		return relationshipTypeConstant.equalsIgnoreCase(relationshipType);
	}
}
