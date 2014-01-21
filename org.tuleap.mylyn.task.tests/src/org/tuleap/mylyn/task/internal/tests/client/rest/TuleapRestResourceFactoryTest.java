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
package org.tuleap.mylyn.task.internal.tests.client.rest;

import com.google.gson.Gson;

import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;
import org.tuleap.mylyn.task.internal.core.client.rest.RestResource;
import org.tuleap.mylyn.task.internal.core.client.rest.RestResourceFactory;
import org.tuleap.mylyn.task.internal.tests.TestLogger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests of {@link RestResourceFactory}.
 * 
 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
 * @author <a href="mailto:firas.bacha@obeo.fr">Firas Bacha</a>
 */
public class TuleapRestResourceFactoryTest {

	private RestResourceFactory factory;

	private MockRestConnector connector;

	private Gson gson;

	/**
	 * Checks the basic properties of {@link RestResourceFactory#artifact(int)}.
	 */
	@Test
	public void testGetArtifactsById() {
		RestResource r = factory.artifact(123);
		assertNotNull(r);
		assertEquals("/api/v12.5/artifacts/123", r.getUrl());
	}

	/**
	 * Checks the basic properties of {@link RestResourceFactory#artifacts()}.
	 */
	@Test
	public void testGetArtifacts() {
		RestResource r = factory.artifacts();
		assertNotNull(r);
		assertEquals("/api/v12.5/artifacts", r.getUrl());
	}

	/**
	 * Checks that GET is not supported by the operation returned by {@link RestResourceFactory#artifacts()}.
	 * 
	 * @throws CoreException
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testGetArtifactsForbidden() throws CoreException {
		RestResource r = factory.artifacts();
		r.get();
	}

	/**
	 * Checks that PUT is not supported by the operation returned by {@link RestResourceFactory#artifacts()}.
	 * 
	 * @throws CoreException
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testPutArtifactsForbidden() throws CoreException {
		RestResource r = factory.artifacts();
		r.put();
	}

	/**
	 * Checks the basic properties of {@link RestResourceFactory#projects()}.
	 */
	@Test
	public void testGetProjects() {
		RestResource r = factory.projects();
		assertNotNull(r);
		assertEquals("/api/v12.5/projects", r.getUrl());
	}

	/**
	 * Checks the basic properties of {@link RestResourceFactory#projectsTrackers(int)}.
	 */
	@Test
	public void testGetProjectTrackers() {
		RestResource r = factory.projectsTrackers(321);
		assertNotNull(r);
		assertEquals("/api/v12.5/projects/321/trackers", r.getUrl());
	}

	/**
	 * Checks the basic properties of {@link RestResourceFactory#user()}.
	 */
	@Test
	public void testGetUser() {
		RestResource r = factory.user();
		assertNotNull(r);
		assertEquals("/api/v12.5/user", r.getUrl());
	}

	/**
	 * Set up the tests.
	 */
	@Before
	public void setUp() {
		connector = new MockRestConnector();
		gson = new Gson();
		factory = new RestResourceFactory("v12.5", connector, gson, new TestLogger());
	}
}
