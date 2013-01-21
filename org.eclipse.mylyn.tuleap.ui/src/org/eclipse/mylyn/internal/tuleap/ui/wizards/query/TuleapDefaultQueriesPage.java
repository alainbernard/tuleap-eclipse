/*******************************************************************************
 * Copyright (c) 2012 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.tuleap.ui.wizards.query;

import java.util.List;

import org.eclipse.mylyn.commons.workbench.forms.SectionComposite;
import org.eclipse.mylyn.internal.tuleap.core.model.TuleapTrackerReport;
import org.eclipse.mylyn.internal.tuleap.core.util.ITuleapConstants;
import org.eclipse.mylyn.internal.tuleap.ui.TuleapTasksUIPlugin;
import org.eclipse.mylyn.internal.tuleap.ui.util.ITuleapUIConstants;
import org.eclipse.mylyn.internal.tuleap.ui.util.TuleapMylynTasksUIMessages;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * The default queries page.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 * @since 1.0
 */
public class TuleapDefaultQueriesPage extends AbstractRepositoryQueryPage2 {

	/**
	 * The task repository.
	 */
	private TaskRepository repository;

	/**
	 * The tracker.
	 */
	private String tracker;

	/**
	 * The list of the reports available.
	 */
	private Combo reportSelectionCombo;

	/**
	 * The reports available on the tracker.
	 */
	private List<TuleapTrackerReport> trackerReport;

	/**
	 * The radio button used to select the "download all artifacts from the project" query.
	 */
	private Button projectsQueryButton;

	/**
	 * The radio button used to select the "run the selected report" query.
	 */
	private Button reportsButton;

	/**
	 * The constructor.
	 * 
	 * @param taskRepository
	 *            The task repository
	 * @param selectedTracker
	 *            The selected tracker
	 * @param reports
	 *            The reports available on the selected tracker.
	 */
	public TuleapDefaultQueriesPage(TaskRepository taskRepository, String selectedTracker,
			List<TuleapTrackerReport> reports) {
		super(TuleapMylynTasksUIMessages.getString("TuleapDefaultQueriesPage.Name"), taskRepository, null); //$NON-NLS-1$
		this.repository = taskRepository;
		this.tracker = selectedTracker;
		this.trackerReport = reports;
		this.setTitle(TuleapMylynTasksUIMessages.getString("TuleapDefaultQueriesPage.Title")); //$NON-NLS-1$
		this.setDescription(TuleapMylynTasksUIMessages.getString("TuleapDefaultQueriesPage.Description")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2#createPageContent(org.eclipse.mylyn.commons.workbench.forms.SectionComposite)
	 */
	@Override
	protected void createPageContent(SectionComposite parent) {
		Group defaultQueriesGroup = new Group(parent.getContent(), SWT.NONE);
		defaultQueriesGroup.setText(TuleapMylynTasksUIMessages
				.getString("TuleapDefaultQueriesPage.DefaultQueriesGroup.Name")); //$NON-NLS-1$
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = false;
		defaultQueriesGroup.setLayoutData(gridData);
		defaultQueriesGroup.setLayout(new GridLayout(1, false));

		Composite groupComposite = new Composite(defaultQueriesGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = false;
		groupComposite.setLayoutData(gridData);
		groupComposite.setLayout(new GridLayout(2, false));

		// Reports
		reportsButton = new Button(groupComposite, SWT.RADIO);
		reportsButton.setText(TuleapMylynTasksUIMessages.getString("TuleapDefaultQueriesPage.ReportsLabel")); //$NON-NLS-1$
		reportSelectionCombo = new Combo(groupComposite, SWT.SINGLE);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		reportSelectionCombo.setLayoutData(gd);
		for (TuleapTrackerReport tuleapTrackerReport : this.trackerReport) {
			reportSelectionCombo.add(tuleapTrackerReport.toString());
		}
		// Select the first report by default
		if (this.trackerReport.size() > 0) {
			reportSelectionCombo.setText(this.trackerReport.get(0).toString());
		}

		// Download all button
		projectsQueryButton = new Button(groupComposite, SWT.RADIO);
		projectsQueryButton.setText(TuleapMylynTasksUIMessages
				.getString("TuleapDefaultQueriesPage.ProjectsQueryButton.Name") + this.tracker); //$NON-NLS-1$
		projectsQueryButton.setSelection(true);
		gd = new GridData();
		gd.horizontalSpan = 2;
		projectsQueryButton.setLayoutData(gd);

		// Select the report by default
		projectsQueryButton.setSelection(false);
		reportsButton.setSelection(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2#doRefreshControls()
	 */
	@Override
	protected void doRefreshControls() {
		// TODO refresh the configuration of the repository and the controls that go along
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2#hasRepositoryConfiguration()
	 */
	@Override
	protected boolean hasRepositoryConfiguration() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2#restoreState(org.eclipse.mylyn.tasks.core.IRepositoryQuery)
	 */
	@Override
	protected boolean restoreState(IRepositoryQuery query) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#applyTo(org.eclipse.mylyn.tasks.core.IRepositoryQuery)
	 */
	@Override
	public void applyTo(IRepositoryQuery query) {
		int indexStart = this.tracker.indexOf(ITuleapConstants.TRACKER_ID_START_DELIMITER);
		int indexEnd = this.tracker.indexOf(ITuleapConstants.TRACKER_ID_END_DELIMITER);
		if (indexStart != -1 && indexEnd != -1 && indexStart < indexEnd) {
			String id = this.tracker.substring(indexStart
					+ ITuleapConstants.TRACKER_ID_START_DELIMITER.length(), indexEnd);
			query.setSummary(this.getQueryTitle());
			query.setAttribute(ITuleapConstants.QUERY_TRACKER_ID, id);

			if (this.projectsQueryButton.getSelection()) {
				query.setAttribute(ITuleapConstants.QUERY_KIND, ITuleapConstants.QUERY_KIND_ALL_FROM_TRACKER);
			} else if (this.reportsButton.getSelection()) {
				query.setAttribute(ITuleapConstants.QUERY_KIND, ITuleapConstants.QUERY_KIND_REPORT);

				// Report id?
				String reportSelected = this.reportSelectionCombo.getText();
				for (TuleapTrackerReport tuleapTrackerReport : this.trackerReport) {
					if (reportSelected.equals(tuleapTrackerReport.toString())) {
						query.setAttribute(ITuleapConstants.QUERY_REPORT_ID, Integer.valueOf(
								tuleapTrackerReport.getId()).toString());
						break;
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#getImage()
	 */
	@Override
	public Image getImage() {
		return TuleapTasksUIPlugin.getDefault().getImage(ITuleapUIConstants.Icons.WIZARD_TULEAP_LOGO_48);
	}
}
