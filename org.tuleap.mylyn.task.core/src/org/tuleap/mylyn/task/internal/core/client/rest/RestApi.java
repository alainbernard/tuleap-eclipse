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
package org.tuleap.mylyn.task.internal.core.client.rest;

import org.eclipse.core.runtime.ILog;

/**
 * JSON Resource for the {@code /api/<version>} URL.
 * 
 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
 */
public class RestApi extends RestResource {

	/**
	 * Constructor.
	 * 
	 * @param serverUrl
	 *            URL of the rest API on the server.
	 * @param apiVersion
	 *            Version of the REST API to use.
	 * @param connector
	 *            the connector to use.
	 * @param logger
	 *            The logger to use.
	 */
	protected RestApi(String serverUrl, String apiVersion, IRestConnector connector, ILog logger) {
		super(serverUrl, apiVersion, "", GET, connector, logger); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}<br/>
	 * The URL returned here is empty since this resource represents the /api/&lt;version> URL.
	 * 
	 * @see org.tuleap.mylyn.task.internal.core.client.rest.RestResource#getUrl()
	 */
	@Override
	public String getUrl() {
		return ""; //$NON-NLS-1$
	}

}
