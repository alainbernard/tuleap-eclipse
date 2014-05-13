/*******************************************************************************
 * Copyright (c) 2013 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.tuleap.mylyn.task.core.tests.internal.serializer;

import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.tuleap.mylyn.task.core.internal.model.data.ArtifactReference;
import org.tuleap.mylyn.task.core.internal.model.data.TuleapReference;
import org.tuleap.mylyn.task.core.internal.model.data.agile.TuleapMilestone;
import org.tuleap.mylyn.task.core.internal.serializer.TuleapMilestoneSerializer;

import static org.junit.Assert.assertEquals;

/**
 * Tests the serialization of the Tuleap milestone.
 * 
 * @author <a href="mailto:firas.bacha@obeo.fr">Firas Bacha</a>
 */
public class TuleapMilestoneSerializerTests {
	/**
	 * The gson converter.
	 */
	private com.google.gson.Gson gson;

	/**
	 * Initialize the gson converter.
	 */
	@Before
	public void setUp() {
		gson = new GsonBuilder().registerTypeAdapter(TuleapMilestone.class, new TuleapMilestoneSerializer())
				.create();
	}

	/**
	 * Test the serialization of a milestone with a parent identifier.
	 * 
	 * @throws ParseException
	 *             the exception
	 */
	@Test
	public void testSerializeMilestoneEmptyWithParentId() throws ParseException {
		TuleapReference projectRef = new TuleapReference(123, "p/123");
		TuleapMilestone milestone = new TuleapMilestone(200, projectRef);

		TuleapReference trackerRef = new TuleapReference(500, "tracker/500");
		ArtifactReference milestoneParent = new ArtifactReference(901, "m/901", trackerRef);

		milestone.setParent(milestoneParent);
		milestone.setCapacity("100");

		milestone.setLabel("milestone label");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //$NON-NLS-1$
		milestone.setStartDate(dateFormat.parse("2013-09-23T11:44:18.963Z")); //$NON-NLS-1$
		milestone.setEndDate(dateFormat.parse("2013-09-25T11:44:18.963Z")); //$NON-NLS-1$

		String emptyMilestone = gson.toJsonTree(milestone).toString();
		String expectedResult = "{\"id\":200,\"label\":\"milestone label\",\"parent\":{\"tracker\":{\"id\":500,\"uri\":\"tracker/500\"},\"id\":901,\"uri\":\"m/901\"},\"start_date\":\"2013-09-23T11:44:18.963Z\",\"end_date\":\"2013-09-25T11:44:18.963Z\",\"capacity\":\"100\"}"; //$NON-NLS-1$
		assertEquals(expectedResult, emptyMilestone);
	}

	/**
	 * Test the serialization of a milestone without a parent identifier.
	 * 
	 * @throws ParseException
	 *             the exception
	 */
	@Test
	public void testSerializeMilestoneEmptyWithoutParentId() throws ParseException {
		TuleapReference projectRef = new TuleapReference(123, "p/123");
		TuleapMilestone milestone = new TuleapMilestone(200, projectRef);
		milestone.setCapacity("100");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //$NON-NLS-1$
		milestone.setStartDate(dateFormat.parse("2013-09-23T11:44:18.963Z")); //$NON-NLS-1$
		milestone.setEndDate(dateFormat.parse("2013-09-25T11:44:18.963Z")); //$NON-NLS-1$

		String emptyMilestone = gson.toJsonTree(milestone).toString();
		String expectedResult = "{\"id\":200,\"start_date\":\"2013-09-23T11:44:18.963Z\",\"end_date\":\"2013-09-25T11:44:18.963Z\",\"capacity\":\"100\"}"; //$NON-NLS-1$
		assertEquals(expectedResult, emptyMilestone);
	}

	/**
	 * Test the serialization of a milestone without capacity.
	 * 
	 * @throws ParseException
	 *             the exception
	 */
	@Test
	public void testSerializeMilestoneEmptyWithoutCapacity() throws ParseException {
		TuleapReference projectRef = new TuleapReference(123, "p/123");
		TuleapMilestone milestone = new TuleapMilestone(200, projectRef);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //$NON-NLS-1$
		milestone.setStartDate(dateFormat.parse("2013-09-23T11:44:18.963Z")); //$NON-NLS-1$
		milestone.setEndDate(dateFormat.parse("2013-09-25T11:44:18.963Z")); //$NON-NLS-1$

		String emptyMilestone = gson.toJsonTree(milestone).toString();
		String expectedResult = "{\"id\":200,\"start_date\":\"2013-09-23T11:44:18.963Z\",\"end_date\":\"2013-09-25T11:44:18.963Z\"}"; //$NON-NLS-1$
		assertEquals(expectedResult, emptyMilestone);
	}

	/**
	 * Test the serialization of an empty milestone without start date.
	 * 
	 * @throws ParseException
	 *             the exception
	 */
	@Test
	public void testSerializeMilestoneEmptyWithoutStartDate() throws ParseException {
		TuleapReference projectRef = new TuleapReference(123, "p/123");
		TuleapMilestone milestone = new TuleapMilestone(200, projectRef);
		milestone.setCapacity("100");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //$NON-NLS-1$
		milestone.setEndDate(dateFormat.parse("2013-09-25T11:44:18.963Z")); //$NON-NLS-1$

		String emptyMilestone = gson.toJsonTree(milestone).toString();
		String expectedResult = "{\"id\":200,\"end_date\":\"2013-09-25T11:44:18.963Z\",\"capacity\":\"100\"}"; //$NON-NLS-1$
		assertEquals(expectedResult, emptyMilestone);
	}

	/**
	 * Test the serialization of an empty milestone without end date.
	 * 
	 * @throws ParseException
	 *             the exception
	 */
	@Test
	public void testSerializeMilestoneEmptyWithoutEndDate() throws ParseException {
		TuleapReference projectRef = new TuleapReference(123, "p/123");
		TuleapMilestone milestone = new TuleapMilestone(200, projectRef);
		milestone.setCapacity("100");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //$NON-NLS-1$
		milestone.setStartDate(dateFormat.parse("2013-09-23T11:44:18.963Z")); //$NON-NLS-1$

		String emptyMilestone = gson.toJsonTree(milestone).toString();
		String expectedResult = "{\"id\":200,\"start_date\":\"2013-09-23T11:44:18.963Z\",\"capacity\":\"100\"}"; //$NON-NLS-1$
		assertEquals(expectedResult, emptyMilestone);
	}

}