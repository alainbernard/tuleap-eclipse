/*******************************************************************************
 * Copyright (c) 2012, 2013 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.tuleap.core.tests.internal.repository;

import com.google.common.collect.Sets;

import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tuleap.core.internal.client.TuleapClientManager;
import org.eclipse.mylyn.tuleap.core.internal.client.rest.TuleapRestClient;
import org.eclipse.mylyn.tuleap.core.internal.data.TuleapArtifactMapper;
import org.eclipse.mylyn.tuleap.core.internal.data.TuleapTaskId;
import org.eclipse.mylyn.tuleap.core.internal.model.config.TuleapProject;
import org.eclipse.mylyn.tuleap.core.internal.model.config.TuleapServer;
import org.eclipse.mylyn.tuleap.core.internal.model.config.TuleapTracker;
import org.eclipse.mylyn.tuleap.core.internal.model.data.TuleapArtifact;
import org.eclipse.mylyn.tuleap.core.internal.model.data.TuleapArtifactWithComment;
import org.eclipse.mylyn.tuleap.core.internal.model.data.TuleapReference;
import org.eclipse.mylyn.tuleap.core.internal.repository.ITuleapRepositoryConnector;
import org.eclipse.mylyn.tuleap.core.internal.repository.TuleapTaskDataHandler;
import org.eclipse.mylyn.tuleap.core.internal.repository.TuleapTaskMapping;
import org.eclipse.mylyn.tuleap.core.internal.util.ITuleapConstants;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.Assert.fail;

/**
 * The tests class for the Tuleap task data handler.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 * @since 0.7
 */
public class TuleapTaskDataHandlerTests {

	/**
	 * The task repository.
	 */
	private TaskRepository repository;

	/**
	 * The server configuration.
	 */
	private TuleapServer tuleapServer;

	/**
	 * The identifier of a standard tracker configuration.
	 */
	private TuleapReference trackerRef = new TuleapReference(1, "trackers/1");

	/**
	 * The identifier of the second tracker.
	 */
	private int secondTrackerId = 2;

	/**
	 * The identifier of the third configuration.
	 */
	private int thirdItemTrackerId = 3;

	/**
	 * The identifier of the project.
	 */
	private TuleapReference projectRef = new TuleapReference(51, "projects/51");

	/**
	 * The identifier of the artifact.
	 */
	private int artifactId = 42;

	/**
	 * The identifier of an item.
	 */
	private int itemId;

	/**
	 * Prepare the Tuleap server configuration and the mock connector.
	 */
	@Before
	public void setUp() {
		String repositoryUrl = "https://tuleap.net";
		this.repository = new TaskRepository(ITuleapConstants.CONNECTOR_KIND, repositoryUrl);

		this.tuleapServer = new TuleapServer(repositoryUrl);
		TuleapProject project = new TuleapProject(null, projectRef.getId());

		TuleapTracker tracker = new TuleapTracker(trackerRef.getId(), null, null, null, null, 0);
		project.addTracker(tracker);

		TuleapTracker secondTracker = new TuleapTracker(secondTrackerId, null, null, null, null, 0);
		project.addTracker(secondTracker);

		TuleapTracker thirdTracker = new TuleapTracker(thirdItemTrackerId, null, null, null, null, 0);
		project.addTracker(thirdTracker);

		this.tuleapServer.addProject(project);
	}

	/**
	 * Initialize a new task data thanks to the configuration with the given configuration identifier and
	 * check that the dispatch has created a task with the proper task kind.
	 * 
	 * @param configurationId
	 *            The identifier of the configuration
	 */
	private void testNewlyInitializedElementKind(int configurationId) {
		ITuleapRepositoryConnector repositoryConnector = new ITuleapRepositoryConnector() {

			public TuleapServer getServer(String repositoryUrl) {
				return TuleapTaskDataHandlerTests.this.tuleapServer;
			}

			public TuleapClientManager getClientManager() {
				return null;
			}

			public TuleapTracker refreshTracker(TaskRepository taskRepository, TuleapTracker configuration,
					IProgressMonitor monitor) throws CoreException {
				return null;
			}
		};

		TuleapTaskDataHandler tuleapTaskDataHandler = new TuleapTaskDataHandler(repositoryConnector);
		TuleapTracker configuration = this.tuleapServer.getProject(projectRef.getId()).getTracker(
				configurationId);

		TaskData taskData = new TaskData(tuleapTaskDataHandler.getAttributeMapper(repository),
				ITuleapConstants.CONNECTOR_KIND, this.repository.getRepositoryUrl(), "");

		ITaskMapping initializationData = new TuleapTaskMapping(configuration);

		try {
			boolean isInitialized = tuleapTaskDataHandler.initializeTaskData(repository, taskData,
					initializationData, new NullProgressMonitor());

			assertThat(isInitialized, is(true));
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test that the initialization of an already initialized task data fails.
	 */
	@Test
	public void initializeAlreadyInitializedTaskData() {
		TuleapTaskDataHandler tuleapTaskDataHandler = new TuleapTaskDataHandler(null);

		TaskData taskData = new TaskData(tuleapTaskDataHandler.getAttributeMapper(repository),
				ITuleapConstants.CONNECTOR_KIND, "", "test id");
		try {
			boolean isInitialized = tuleapTaskDataHandler.initializeTaskData(this.repository, taskData, null,
					new NullProgressMonitor());
			assertThat(isInitialized, is(false));
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test the creation of an empty task data representing a Tuleap artifact. We won't test here all the
	 * options used in the creation of said task data since other unit tests will handle it.
	 */
	@Test
	public void initializeTaskDataArtifact() {
		testNewlyInitializedElementKind(trackerRef.getId());
	}

	/**
	 * Test the creation of an empty task data representing a Tuleap milestone. We won't test here all the
	 * options used in the creation of said task data since other unit tests will handle it.
	 */
	@Test
	public void initializeTaskDataMilestone() {
		testNewlyInitializedElementKind(secondTrackerId);
	}

	/**
	 * Retrieve the artifact with the given task id and check that the task data created has the proper task
	 * kind.
	 * 
	 * @param taskId
	 *            The identifier of the task
	 */
	private void testGetTaskData(TuleapTaskId taskId) {
		final TuleapArtifact tuleapArtifact = new TuleapArtifact(artifactId, projectRef, "", "", "",
				new Date(), new Date());
		tuleapArtifact.setTracker(trackerRef);

		// Mock rest client
		final TuleapRestClient tuleapRestClient = new TuleapRestClient(null, null, null) {
			@Override
			public TuleapArtifact getArtifact(int id, TuleapServer server, IProgressMonitor monitor)
					throws CoreException {
				return tuleapArtifact;
			}
		};

		// mock client manager
		final TuleapClientManager tuleapClientManager = new TuleapClientManager() {
			@Override
			public TuleapRestClient getRestClient(TaskRepository taskRepository) {
				return tuleapRestClient;
			}
		};

		// mock repository connector
		ITuleapRepositoryConnector repositoryConnector = new ITuleapRepositoryConnector() {

			public TuleapServer getServer(String repositoryUrl) {
				return TuleapTaskDataHandlerTests.this.tuleapServer;
			}

			public TuleapClientManager getClientManager() {
				return tuleapClientManager;
			}

			public TuleapTracker refreshTracker(TaskRepository taskRepository, TuleapTracker tracker,
					IProgressMonitor monitor) throws CoreException {
				return tracker;
			}
		};

		TuleapTaskDataHandler tuleapTaskDataHandler = new TuleapTaskDataHandler(repositoryConnector);
		try {
			TaskData taskData = tuleapTaskDataHandler.getTaskData(this.repository, taskId,
					new NullProgressMonitor());

			assertThat(taskData, notNullValue());
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test the retrieval of the task data representing an existing Tuleap artifact. We won't test here all
	 * the options used during the creation of said task data since other unit tests will handle it.
	 */
	@Test
	public void testGetTaskDataArtifact() {
		this.testGetTaskData(TuleapTaskId.forArtifact(projectRef.getId(), trackerRef.getId(), artifactId));
	}

	/**
	 * Test the retrieval of the task data representing an existing Tuleap item. We won't test here all the
	 * options used during the creation of said task data since other unit tests will handle it.
	 */
	@Test
	public void testGetTaskDataItem() {
		this.testGetTaskData(TuleapTaskId.forArtifact(projectRef.getId(), thirdItemTrackerId, itemId));
	}

	/**
	 * Create and update the element with the given task data and check the identifier of the server element
	 * created and updated.
	 * 
	 * @param taskData
	 *            The task data
	 * @param taskId
	 *            The identifier of the task created and updated
	 * @param responseKind
	 *            The kind of the response
	 */
	private void testPostTaskData(TaskData taskData, final TuleapTaskId taskId, ResponseKind responseKind) {
		// Mock rest client
		final TuleapRestClient tuleapRestClient = new TuleapRestClient(null, null, null) {
			@Override
			public TuleapTaskId createArtifact(TuleapArtifact artifact, IProgressMonitor monitor)
					throws CoreException {
				return TuleapTaskId.forName(taskId.toString());
			}

			@Override
			public void updateArtifact(TuleapArtifactWithComment artifact, IProgressMonitor monitor)
					throws CoreException {
				// do nothing
			}
		};

		// mock client manager
		final TuleapClientManager tuleapClientManager = new TuleapClientManager() {
			@Override
			public TuleapRestClient getRestClient(TaskRepository taskRepository) {
				return tuleapRestClient;
			}
		};

		// mock repository connector
		ITuleapRepositoryConnector repositoryConnector = new ITuleapRepositoryConnector() {

			public TuleapServer getServer(String repositoryUrl) {
				return TuleapTaskDataHandlerTests.this.tuleapServer;
			}

			public TuleapClientManager getClientManager() {
				return tuleapClientManager;
			}

			public TuleapTracker refreshTracker(TaskRepository taskRepository, TuleapTracker tracker,
					IProgressMonitor monitor) throws CoreException {
				return tracker;
			}
		};

		TuleapTaskDataHandler tuleapTaskDataHandler = new TuleapTaskDataHandler(repositoryConnector);
		try {
			RepositoryResponse response = tuleapTaskDataHandler.postTaskData(this.repository, taskData, Sets
					.<TaskAttribute> newHashSet(), new NullProgressMonitor());
			assertThat(response.getReposonseKind(), is(responseKind));
			assertThat(response.getTaskId(), is(taskId.toString()));
		} catch (CoreException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test the submission of the new task data representing an existing Tuleap artifact. We won't test here
	 * all the options available in the task data since other unit tests will handle it.
	 */
	@Test
	public void testPostCreateTaskDataArtifact() {
		TaskData taskData = new TaskData(new TaskAttributeMapper(this.repository),
				ITuleapConstants.CONNECTOR_KIND, "", "");

		TuleapArtifactMapper mapper = new TuleapArtifactMapper(taskData, this.tuleapServer.getProject(
				projectRef.getId()).getTracker(trackerRef.getId()));
		mapper.initializeEmptyTaskData();

		TuleapTaskId taskId = TuleapTaskId.forArtifact(projectRef.getId(), trackerRef.getId(), artifactId);
		this.testPostTaskData(taskData, taskId, ResponseKind.TASK_CREATED);
	}

	/**
	 * Test the submission of the new task data representing an existing Tuleap item. We won't test here all
	 * the options available in the task data since other unit tests will handle it.
	 */
	@Test
	public void testPostCreateTaskDataItem() {
		TaskData taskData = new TaskData(new TaskAttributeMapper(this.repository),
				ITuleapConstants.CONNECTOR_KIND, "", "");

		TuleapArtifactMapper mapper = new TuleapArtifactMapper(taskData, this.tuleapServer.getProject(
				projectRef.getId()).getTracker(thirdItemTrackerId));
		mapper.initializeEmptyTaskData();

		TuleapTaskId taskId = TuleapTaskId.forArtifact(projectRef.getId(), thirdItemTrackerId, itemId);
		this.testPostTaskData(taskData, taskId, ResponseKind.TASK_CREATED);
	}

	/**
	 * Test the submission of the existing task data representing an existing Tuleap artifact. We won't test
	 * here all the options available in the task data since other unit tests will handle it.
	 */
	@Test
	public void testPostUpdateTaskDataArtifact() {
		TuleapTaskId taskId = TuleapTaskId.forArtifact(projectRef.getId(), trackerRef.getId(), artifactId);
		TaskData taskData = new TaskData(new TaskAttributeMapper(this.repository),
				ITuleapConstants.CONNECTOR_KIND, "", taskId.toString());

		TuleapArtifactMapper mapper = new TuleapArtifactMapper(taskData, this.tuleapServer.getProject(
				projectRef.getId()).getTracker(trackerRef.getId()));
		mapper.initializeEmptyTaskData();

		this.testPostTaskData(taskData, taskId, ResponseKind.TASK_UPDATED);
	}

}