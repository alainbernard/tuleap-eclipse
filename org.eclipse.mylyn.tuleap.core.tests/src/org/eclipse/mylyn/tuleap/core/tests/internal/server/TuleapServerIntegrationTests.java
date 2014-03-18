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
package org.eclipse.mylyn.tuleap.core.tests.internal.server;

import com.google.gson.Gson;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tuleap.core.internal.client.rest.RestResourceFactory;
import org.eclipse.mylyn.tuleap.core.internal.client.rest.TuleapRestClient;
import org.eclipse.mylyn.tuleap.core.internal.client.rest.TuleapRestConnector;
import org.eclipse.mylyn.tuleap.core.internal.model.data.agile.TuleapBacklogItem;
import org.eclipse.mylyn.tuleap.core.internal.model.data.agile.TuleapMilestone;
import org.eclipse.mylyn.tuleap.core.internal.parser.TuleapGsonProvider;
import org.eclipse.mylyn.tuleap.core.internal.util.ITuleapConstants;
import org.eclipse.mylyn.tuleap.core.tests.internal.AbstractTuleapTests;
import org.eclipse.mylyn.tuleap.core.tests.internal.TestLogger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class will contain integration tests that have to be run against a valid Tuleap REST-based server.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 */
public class TuleapServerIntegrationTests extends AbstractTuleapTests {

	private TaskRepository taskRepository;

	private AbstractWebLocation location;

	private Gson gson;

	/**
	 * We will try to connect to the server with valid credentials.
	 */
	@Test
	public void testValidAuthentication() {
		TestLogger logger = new TestLogger();
		TuleapRestConnector tuleapRestConnector = new TuleapRestConnector(location, logger);
		RestResourceFactory restResourceFactory = new RestResourceFactory("v3.14", tuleapRestConnector, gson,
				new TestLogger());
		TuleapRestClient tuleapServer = new TuleapRestClient(restResourceFactory, gson, this.repository);
		try {
			IStatus connectionStatus = tuleapServer.validateConnection(new NullProgressMonitor());
			assertEquals(IStatus.OK, connectionStatus.getSeverity());
			assertEquals(0, logger.getStatus().size());
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * We will try to connect to the server with invalid credentials.
	 */
	@Test
	public void testInvalidAuthentication() {
		// Setting a wrong password for the test
		this.repository.setCredentials(AuthenticationType.REPOSITORY, new AuthenticationCredentials(
				"admin", "wrong"), false); //$NON-NLS-1$ //$NON-NLS-2$
		TestLogger logger = new TestLogger();
		TuleapRestConnector tuleapRestConnector = new TuleapRestConnector(location, logger);
		RestResourceFactory restResourceFactory = new RestResourceFactory("v3.14", tuleapRestConnector, gson,
				new TestLogger());
		TuleapRestClient tuleapServer = new TuleapRestClient(restResourceFactory, gson, this.repository);
		try {
			tuleapServer.validateConnection(new NullProgressMonitor());
			fail("A CoreException should have been thrown"); //$NON-NLS-1$
		} catch (CoreException e) {
			assertEquals(IStatus.ERROR, e.getStatus().getSeverity());
			assertEquals("Error 401: Unauthorized", e.getMessage()); //$NON-NLS-1$ 
		}
	}

	/**
	 * Test of backlog items retrieval.
	 */
	@Test
	public void testGetMilestones() {
		TestLogger logger = new TestLogger();
		TuleapRestConnector restConnector = new TuleapRestConnector(location, logger);
		RestResourceFactory restResourceFactory = new RestResourceFactory("v3.14", restConnector, gson,
				new TestLogger());
		TuleapRestClient tuleapServer = new TuleapRestClient(restResourceFactory, gson, this.repository);
		try {
			List<TuleapMilestone> milestoneItems = tuleapServer.getSubMilestones(200, null);
			assertEquals(3, milestoneItems.size());

			List<TuleapBacklogItem> backlogItems = tuleapServer.getMilestoneBacklog(200, null);
			assertEquals(0, backlogItems.size());

			milestoneItems = tuleapServer.getSubMilestones(201, null);
			assertEquals(0, milestoneItems.size());

			backlogItems = tuleapServer.getMilestoneBacklog(201, null);
			assertEquals(2, backlogItems.size());
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.mylyn.tuleap.core.tests.internal.AbstractTuleapTests#getServerUrl()
	 */
	@Override
	public String getServerUrl() {
		// TODO Use properties in order to be able to customize the unit test from jenkins/hudson
		return "http://localhost:3001"; //$NON-NLS-1$
	}

	@Override
	@Before
	public void setUp() {
		taskRepository = new TaskRepository(ITuleapConstants.CONNECTOR_KIND, "https://this.is.a.test"); //$NON-NLS-1$
		AuthenticationCredentials credentials = new AuthenticationCredentials("admin", "password"); //$NON-NLS-1$//$NON-NLS-2$
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, credentials, true);
		credentials = new AuthenticationCredentials("admin", "password"); //$NON-NLS-1$//$NON-NLS-2$
		taskRepository.setCredentials(AuthenticationType.HTTP, credentials, true);
		location = new TaskRepositoryLocationFactory().createWebLocation(taskRepository);
		gson = TuleapGsonProvider.defaultGson();
	}
}