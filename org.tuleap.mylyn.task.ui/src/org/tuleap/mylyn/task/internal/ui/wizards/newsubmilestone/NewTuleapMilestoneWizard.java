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
package org.tuleap.mylyn.task.internal.ui.wizards.newsubmilestone;

import org.eclipse.jface.wizard.Wizard;
import org.tuleap.mylyn.task.agile.core.IMilestoneMapping;
import org.tuleap.mylyn.task.internal.core.model.config.TuleapProject;
import org.tuleap.mylyn.task.internal.core.model.config.TuleapTracker;
import org.tuleap.mylyn.task.internal.core.repository.TuleapMilestoneMapping;
import org.tuleap.mylyn.task.internal.ui.wizards.TuleapTrackerPage;

/**
 * This wizard is used to create a new milestone. It will return a milestone mapping in order to initialize a
 * new milestone.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 */
public class NewTuleapMilestoneWizard extends Wizard {

	/**
	 * The configuration of the project.
	 */
	private TuleapProject project;

	/**
	 * The identifier of the parent milestone.
	 */
	private String parentMilestoneId;

	/**
	 * The mapping to return in order to create the new milestone.
	 */
	private IMilestoneMapping milestoneMapping;

	/**
	 * The page used to select a tracker.
	 */
	private TuleapTrackerPage trackerPage;

	/**
	 * The constructor.
	 * 
	 * @param project
	 *            The configuration of the project.
	 * @param parentMilestoneId
	 *            The identifier of the parent milestone.
	 */
	public NewTuleapMilestoneWizard(TuleapProject project, String parentMilestoneId) {
		this.project = project;
		this.parentMilestoneId = parentMilestoneId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		this.trackerPage = new TuleapTrackerPage(this.project);
		this.addPage(this.trackerPage);
	}

	/**
	 * Returns the mapping used to create the new milestone.
	 * 
	 * @return The mapping used to create the new milestone
	 */
	public IMilestoneMapping getMilestoneMapping() {
		return this.milestoneMapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		TuleapTracker tracker = this.trackerPage.getTrackerSelected();
		this.milestoneMapping = new TuleapMilestoneMapping(tracker, this.parentMilestoneId);

		return tracker != null;
	}
}
