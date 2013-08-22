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
package org.tuleap.mylyn.task.internal.core.server;

import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.tuleap.mylyn.task.internal.core.server.rest.ICredentials;

/**
 * Credentials that fetch user data in a task repository.
 * 
 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
 */
public class TaskRepositoryCredentials implements ICredentials {

	/**
	 * The task repository where to look for credentials information.
	 */
	private final TaskRepository repository;

	/**
	 * Constructor with task repository.
	 * 
	 * @param repository
	 *            The task repository where to look for credentials information
	 */
	public TaskRepositoryCredentials(TaskRepository repository) {
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.tuleap.mylyn.task.internal.core.server.rest.ICredentials#getUserName()
	 */
	public String getUserName() {
		return repository.getCredentials(AuthenticationType.REPOSITORY).getUserName();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.tuleap.mylyn.task.internal.core.server.rest.ICredentials#getPassword()
	 */
	public String getPassword() {
		return repository.getCredentials(AuthenticationType.REPOSITORY).getPassword();
	}

}
