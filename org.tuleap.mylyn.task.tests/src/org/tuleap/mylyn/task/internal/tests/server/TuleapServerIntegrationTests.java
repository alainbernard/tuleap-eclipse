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
package org.tuleap.mylyn.task.internal.tests.server;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.junit.Test;
import org.tuleap.mylyn.task.internal.core.parser.TuleapJsonParser;
import org.tuleap.mylyn.task.internal.core.parser.TuleapJsonSerializer;
import org.tuleap.mylyn.task.internal.core.server.TuleapServer;
import org.tuleap.mylyn.task.internal.core.server.rest.TuleapRestConnector;
import org.tuleap.mylyn.task.internal.tests.AbstractTuleapTests;
import org.tuleap.mylyn.task.internal.tests.TestLogger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class will contain integration tests that have to be run against a valid Tuleap REST-based server.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 */
public class TuleapServerIntegrationTests extends AbstractTuleapTests {

	/**
	 * We will try to connect to the server with valid credentials.
	 */
	@Test
	public void testValidAuthentication() {
		TestLogger logger = new TestLogger();
		TuleapRestConnector tuleapRestConnector = new TuleapRestConnector(this.getServerUrl(), "v3.14", //$NON-NLS-1$
				logger);
		TuleapJsonParser tuleapJsonParser = new TuleapJsonParser();
		TuleapJsonSerializer tuleapJsonSerializer = new TuleapJsonSerializer();

		TuleapServer tuleapServer = new TuleapServer(tuleapRestConnector, tuleapJsonParser,
				tuleapJsonSerializer, this.repository, logger);
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
		TuleapRestConnector tuleapRestConnector = new TuleapRestConnector(this.getServerUrl(), "v3.14", //$NON-NLS-1$
				logger);
		TuleapJsonParser tuleapJsonParser = new TuleapJsonParser();
		TuleapJsonSerializer tuleapJsonSerializer = new TuleapJsonSerializer();

		TuleapServer tuleapServer = new TuleapServer(tuleapRestConnector, tuleapJsonParser,
				tuleapJsonSerializer, this.repository, logger);
		try {
			tuleapServer.validateConnection(new NullProgressMonitor());
			fail("A CoreException should have been thrown"); //$NON-NLS-1$
		} catch (CoreException e) {
			assertEquals(IStatus.ERROR, e.getStatus().getSeverity());
			assertEquals("Error 401: Unauthorized", e.getMessage()); //$NON-NLS-1$ 
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.tuleap.mylyn.task.internal.tests.AbstractTuleapTests#getServerUrl()
	 */
	@Override
	public String getServerUrl() {
		// TODO Use properties in order to be able to customize the unit test from jenkins/hudson
		return "http://localhost:3001"; //$NON-NLS-1$
	}
}