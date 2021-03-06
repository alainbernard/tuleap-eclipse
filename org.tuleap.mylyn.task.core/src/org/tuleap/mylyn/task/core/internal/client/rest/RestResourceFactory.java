/*******************************************************************************
 * Copyright (c) 2013, 2016 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *     A. Bernard - remove use of API version but still keep two constructors
 *         for compatibility with tests
 *******************************************************************************/
package org.tuleap.mylyn.task.core.internal.client.rest;

import static org.tuleap.mylyn.task.core.internal.client.rest.RestResource.DELETE;
import static org.tuleap.mylyn.task.core.internal.client.rest.RestResource.GET;
import static org.tuleap.mylyn.task.core.internal.client.rest.RestResource.POST;
import static org.tuleap.mylyn.task.core.internal.client.rest.RestResource.PUT;

import com.google.gson.Gson;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ILog;
import org.tuleap.mylyn.task.core.internal.client.rest.RestResource.URL;

/**
 * Builder class that instantiates the accessible JSON services.
 *
 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
 * @author <a href="mailto:firas.bacha@obeo.fr">Firas Bacha</a>
 */
public final class RestResourceFactory {
	/**
	 * The prefix of the api url.
	 */
	public static final String API_PREFIX = "/api/"; //$NON-NLS-1$

	/**
	 * v1.
	 */
	public static final String V1 = "v1"; //$NON-NLS-1$

	/**
	 * The best version of the API supported by the connector.
	 */
	public static final String BEST_VERSION = V1;

	/**
	 * The connector to use.
	 */
	private final IRestConnector connector;

	/**
	 * The logger.
	 */
	private final ILog logger;

	/**
	 * The {@link Gson} to use.
	 */
	private final Gson gson;

	/**
	 * Constructor.
	 *
	 * @param apiVersion
	 *            the API version to use.
	 * @param connector
	 *            The connector to use.
	 * @param gson
	 *            The {@link Gson} to use.
	 * @param logger
	 *            The logger to use.
	 */
	public RestResourceFactory(String apiVersion, IRestConnector connector, Gson gson, ILog logger) {
		Assert.isNotNull(connector);
		this.connector = connector;
		Assert.isNotNull(gson);
		this.gson = gson;
		Assert.isNotNull(logger);
		this.logger = logger;
	}

	/**
	 * Constructor.
	 *
	 * @param connector
	 *            The connector to use.
	 * @param gson
	 *            The {@link Gson} to use.
	 * @param logger
	 *            The logger to use.
	 */
	public RestResourceFactory(IRestConnector connector, Gson gson, ILog logger) {
		Assert.isNotNull(connector);
		this.connector = connector;
		Assert.isNotNull(gson);
		this.gson = gson;
		Assert.isNotNull(logger);
		this.logger = logger;
	}

	/**
	 * Provides access to the {code /api/{version}} HTTP resource.
	 *
	 * @return A resource that gives access to the {code /api/{version}} HTTP resource.
	 */
	public RestResource api() {
		return resource(GET, ""); //$NON-NLS-1$
	}

	/**
	 * Provides access to the {code /login} HTTP resource.
	 *
	 * @return A resource that gives access to the {code /login} HTTP resource.
	 */
	public RestResource user() {
		return resource(GET, URL.USER);
	}

	/**
	 * Provides access to the {code /tokens} HTTP resource.
	 *
	 * @return A resource that gives access to the {code /tokens} HTTP resource.
	 */
	public RestResource tokens() {
		return resource(POST, URL.TOKENS);
	}

	/**
	 * Provides access to the {code /projects} HTTP resource.
	 *
	 * @return A resource that gives access to the {code /projects} HTTP resource.
	 */
	public RestResource projects() {
		return resource(GET, URL.PROJECTS);
	}

	/**
	 * Provides access to the {code /artifacts} HTTP resource.
	 *
	 * @return A resource that gives access to the {code /artifacts} HTTP resource.
	 */
	public RestResource artifacts() {
		return resource(POST, URL.ARTIFACTS);
	}

	/**
	 * Provides access to the {code /artifacts/:id} HTTP resource.
	 *
	 * @param artifactId
	 *            Id of the artifact.
	 * @return A resource that gives access to the {code /artifacts/:id} HTTP resource.
	 */
	public RestResource artifact(int artifactId) {
		return resource(GET | PUT, URL.ARTIFACTS, Integer.toString(artifactId));
	}

	/**
	 * Provides access to the {code /artifacts/:id/changesets} HTTP resource.
	 *
	 * @param artifactId
	 *            Id of the artifact.
	 * @return A resource that gives access to the {code /artifacts/:id/changesets} HTTP resource.
	 */
	public RestResource artifactChangesets(int artifactId) {
		return resource(GET, URL.ARTIFACTS, Integer.toString(artifactId), URL.CHANGESETS);
	}

	/**
	 * Provides access to the {code /artifact_files/:id} HTTP resource.
	 *
	 * @param fileId
	 *            Id of the artifact file
	 * @return A resource that gives access to the {code /artifact_files/:id} HTTP resource.
	 */
	public RestResource artifactFile(int fileId) {
		return resource(GET, URL.ARTIFACT_FILES, Integer.toString(fileId));
	}

	/**
	 * Provides access to the {code /artifact_temporary_files/:id} HTTP resource.
	 *
	 * @param fileId
	 *            Id of the artifact file.
	 * @return A resource that gives access to the {code /artifact_temporary_files/:id} HTTP resource.
	 */
	public RestResource artifactTemporaryFile(int fileId) {
		return resource(GET | DELETE | PUT, URL.ARTIFACT_TEMPORARY_FILES, Integer.toString(fileId));
	}

	/**
	 * Provides access to the {code /artifact_temporary_files} HTTP resource.
	 *
	 * @return A resource that gives access to the {code /artifact_temporary_files} HTTP resource.
	 */
	public RestResource artifactTemporaryFiles() {
		return resource(GET | POST, URL.ARTIFACT_TEMPORARY_FILES);
	}

	/**
	 * Provides access to the {code /trackers/:id} HTTP resource.
	 *
	 * @param trackerId
	 *            Id of the tracker.
	 * @return A resource that gives access to the {code /trackers/:id} HTTP resource.
	 */
	public RestResource tracker(int trackerId) {
		return resource(GET, URL.TRACKERS, Integer.toString(trackerId));
	}

	/**
	 * Provides access to the {code /projects/:projectId/trackers} HTTP resource.
	 *
	 * @param projectId
	 *            the project id
	 * @return A resource that gives access to the {code /projects/:projectId/trackers} HTTP resource.
	 */
	public RestResource projectsTrackers(int projectId) {
		return resource(GET, URL.PROJECTS, Integer.toString(projectId), URL.TRACKERS);
	}

	/**
	 * Provides access to the {code /projects/:id/user_groups} HTTP resource to get a project user groups.
	 *
	 * @param projectId
	 *            the project id
	 * @return A resource that gives access to the {code /projects/:id/user_groups} HTTP resource.
	 */
	public RestResource projectUserGroups(int projectId) {
		return resource(GET, URL.PROJECTS, Integer.toString(projectId), URL.USER_GROUPS);
	}

	/**
	 * Provides access to the {code /user_groups/:id/users} HTTP resource to get a user group users.
	 *
	 * @param userGroupId
	 *            the project id
	 * @return A resource that gives access to the {code /user_groups/:id/users} HTTP resource.
	 */
	public RestResource userGroupUsers(String userGroupId) {
		return resource(GET, URL.USER_GROUPS, userGroupId, URL.USERS);
	}

	/**
	 * Obtain a resource from its URL fragments.
	 *
	 * @param supportedMethods
	 *            Flags for supported methods.
	 * @param urlFragments
	 *            Fragments that will be concatenated to build the resource URL.
	 * @return A new resource with the URL built from the given fragments, and a serverURL and apiVersion
	 *         equal to those of this {@link RestResourceFactory} instance.
	 */
	public RestResource resource(int supportedMethods, String... urlFragments) {
		final String url = url(urlFragments);
		return new RestResource(url, supportedMethods, connector, gson, logger);
	}

	/**
	 * Provides access to the {code /trackers/:id/tracker_reports} HTTP resource.
	 *
	 * @param trackerId
	 *            the tracker id
	 * @return A resource that gives access to the {code /trackers/:id/tracker_reports} HTTP resource.
	 */
	public RestResource trackerReports(int trackerId) {
		return resource(GET, URL.TRACKERS, Integer.toString(trackerId), URL.TRACKER_REPORTS);
	}

	/**
	 * Provides access to the {code /tracker_reports/:id/artifacts } HTTP resource.
	 *
	 * @param trackerReportId
	 *            the tracker report id
	 * @return A resource that gives access to the {code /tracker_reports/:id/artifacts} HTTP resource.
	 */
	public RestResource trackerReportArtifacts(int trackerReportId) {
		return resource(GET, URL.TRACKER_REPORTS, Integer.toString(trackerReportId), URL.ARTIFACTS);
	}

	/**
	 * Provides access to the {code /trackers/:id/artifacts } HTTP resource. This resource should be used with
	 * query parameters!
	 *
	 * @param trackerId
	 *            the tracker id
	 * @return A resource that gives access to the {code /trackers/:id/artifacts} HTTP resource.
	 */
	public RestResource trackerArtifacts(int trackerId) {
		return resource(GET, URL.TRACKERS, Integer.toString(trackerId), URL.ARTIFACTS);
	}

	/**
	 * Build a URL by concatenating the given fragments.
	 *
	 * @param urlFragments
	 *            Fragments to concatenate.
	 * @return The URL built by concatenating the given fragments, separated by a slash ('/') character. If
	 *         the first fragment does not start with a slash, a slash in prepended to ensure the URI built
	 *         starts with a slash.
	 */
	private String url(String... urlFragments) {
		Assert.isNotNull(urlFragments);
		Assert.isTrue(urlFragments.length > 0);
		StringBuilder b = new StringBuilder();
		b.append(API_PREFIX);// .append(apiVersion);
		String string = urlFragments[0];
		// if (string.charAt(0) != '/') {
		// b.append('/');
		// }
		b.append(string);
		int i = 1;
		while (i < urlFragments.length) {
			b.append('/').append(urlFragments[i++]);
		}
		return b.toString();
	}
}
