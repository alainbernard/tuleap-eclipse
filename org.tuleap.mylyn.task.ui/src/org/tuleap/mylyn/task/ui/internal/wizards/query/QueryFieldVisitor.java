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
package org.tuleap.mylyn.task.ui.internal.wizards.query;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.mylyn.commons.workbench.forms.DatePicker;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.tuleap.mylyn.task.core.internal.client.ITuleapQueryConstants;
import org.tuleap.mylyn.task.core.internal.model.config.AbstractTuleapField;
import org.tuleap.mylyn.task.core.internal.model.config.field.AbstractTuleapFieldVisitor;
import org.tuleap.mylyn.task.core.internal.model.config.field.AbstractTuleapSelectBox;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapArtifactLink;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapComputedValue;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapDate;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapFileUpload;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapFloat;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapInteger;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapMultiSelectBox;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapOpenList;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapSelectBox;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapSelectBoxItem;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapString;
import org.tuleap.mylyn.task.core.internal.model.config.field.TuleapText;
import org.tuleap.mylyn.task.core.internal.model.data.IQueryCriterion;
import org.tuleap.mylyn.task.core.internal.model.data.TuleapQueryCriterion;
import org.tuleap.mylyn.task.ui.internal.TuleapTasksUIPlugin;

/**
 * Visitor of tuleap fields to create the wizard's page.
 *
 * @author <a href="mailto:laurent.delaigue@obeo.fr">Laurent Delaigue</a>
 */
public class QueryFieldVisitor extends AbstractTuleapFieldVisitor {

	/**
	 * The query form graphical elements.
	 */
	private final List<AbstractTuleapCustomQueryElement<?>> elements;

	/**
	 * Current query criteria.
	 */
	private final JsonObject currentQueryCriteria;

	/**
	 * The composite that contains the created fields in the wizard.
	 */
	private final Composite group;

	/**
	 * The Gson to use to deserialize query parameters.
	 */
	private final Gson gson;

	/**
	 * The wizard page.
	 */
	private final TuleapCustomQueryPage page;

	/**
	 * Constructor.
	 *
	 * @param group
	 *            The parent composite.
	 * @param gson
	 *            The Gson to use to deserialize query parameters.
	 * @param page
	 *            The wizard page.
	 */
	public QueryFieldVisitor(Composite group, Gson gson, final TuleapCustomQueryPage page) {
		this.group = group;
		this.gson = gson;
		this.page = page;
		IRepositoryQuery currentQuery = page.getQuery();
		String jsonCriteria = null;
		if (currentQuery != null) {
			jsonCriteria = currentQuery.getAttribute(ITuleapQueryConstants.QUERY_CUSTOM_CRITERIA);
		}
		JsonElement jsonElement;
		if (jsonCriteria != null) {
			jsonElement = new JsonParser().parse(jsonCriteria);
		} else {
			jsonElement = JsonNull.INSTANCE;
		}
		if (jsonElement.isJsonObject()) {
			currentQueryCriteria = jsonElement.getAsJsonObject();
		} else {
			currentQueryCriteria = new JsonObject();
		}
		elements = new ArrayList<AbstractTuleapCustomQueryElement<?>>();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapArtifactLink)
	 */
	@Override
	public void visit(TuleapArtifactLink field) {
		Text text = createGroupContentForString(field.getLabel(), field);
		loadValueForString(field, text);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapComputedValue)
	 */
	@Override
	public void visit(TuleapComputedValue field) {
		Text text = createGroupContentForString(field.getLabel(), field);
		loadValueForString(field, text);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapDate)
	 */
	@Override
	public void visit(TuleapDate field) {
		Label label = new Label(group, SWT.NONE);
		label.setText(field.getLabel());

		Combo combo = new Combo(group, SWT.SINGLE | SWT.READ_ONLY);
		combo.setItems(new String[] {IQueryCriterion.OP_LT, IQueryCriterion.OP_EQ, IQueryCriterion.OP_GT,
				IQueryCriterion.OP_BETWEEN, });
		combo.setText(IQueryCriterion.OP_LT);

		// Retrieval of values for existing query
		Calendar initialMin = null;
		Calendar initialMax = null;
		if (currentQueryCriteria.has(field.getName())) {
			JsonElement json = currentQueryCriteria.get(field.getName());
			// CHECKSTYLE:OFF
			Type criterionType = new TypeToken<TuleapQueryCriterion<Date[]>>() {
				// Nothing
			}.getType();
			// CHECKSTYLE:ON
			try {
				TuleapQueryCriterion<Date[]> criterion = gson.fromJson(json, criterionType);
				combo.setText(criterion.getOperator());
				Date[] dates = criterion.getValue();
				if (dates.length > 0 && dates[0] != null) {
					initialMin = GregorianCalendar.getInstance();
					initialMin.setTime(dates[0]);
					if (dates.length > 1 && dates[1] != null) {
						initialMax = GregorianCalendar.getInstance();
						initialMax.setTime(dates[1]);
					}
				}
				// CHECKSTYLE:OFF We need to prevent any exception to block here
			} catch (Exception e) {
				// CHECKSYLE:ON
				TuleapTasksUIPlugin.log(e, false);
			}

		}
		Composite composite = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		DatePicker from = new DatePicker(composite, SWT.SINGLE | SWT.BORDER, "", false, 0); //$NON-NLS-1$
		from.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final DatePicker to = new DatePicker(composite, SWT.SINGLE | SWT.BORDER, "", false, 0); //$NON-NLS-1$
		to.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		from.setDate(initialMin);
		to.setDate(initialMax);
		to.setVisible(IQueryCriterion.OP_BETWEEN.equals(combo.getText()));
		combo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (IQueryCriterion.OP_BETWEEN.equals(((Combo)e.widget).getText())) {
					to.setVisible(true);
				} else {
					to.setVisible(false);
				}
			}
		});
		combo.addModifyListener(page.getModifyListener());
		from.addPickerSelectionListener(page.getSelectionListener());
		to.addPickerSelectionListener(page.getSelectionListener());
		elements.add(new TuleapDateQueryElement(field, combo, from, to));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapFileUpload)
	 */
	@Override
	public void visit(TuleapFileUpload field) {
		// Nothing to do for upload fields.
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapFloat)
	 */
	@Override
	public void visit(TuleapFloat field) {
		Text text = createGroupContentForString(field.getLabel(), field);
		// CHECKSTYLE:OFF
		Type criterionType = new TypeToken<TuleapQueryCriterion<Double>>() {
			// Nothing
		}.getType();
		// CHECKSTYLE:ON
		if (currentQueryCriteria.has(field.getName())) {
			JsonElement json = currentQueryCriteria.get(field.getName());
			try {
				TuleapQueryCriterion<?> criterion = gson.fromJson(json, criterionType);
				text.setText(String.valueOf(criterion.getValue()));
				// CHECKSTYLE:OFF We need to prevent any exception to block here
			} catch (Exception e) {
				// CHECKSYLE:ON
				TuleapTasksUIPlugin.log(e, false);
			}
		}
		elements.add(new TuleapDoubleQueryElement(field, text));
		text.addModifyListener(page.getModifyListener());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapInteger)
	 */
	@Override
	public void visit(TuleapInteger field) {
		Text text = createGroupContentForString(field.getLabel(), field);
		// CHECKSTYLE:OFF
		Type criterionType = new TypeToken<TuleapQueryCriterion<Integer>>() {
			// Nothing
		}.getType();
		// CHECKSTYLE:ON
		if (currentQueryCriteria.has(field.getName())) {
			JsonElement json = currentQueryCriteria.get(field.getName());
			try {
				TuleapQueryCriterion<?> criterion = gson.fromJson(json, criterionType);
				text.setText(String.valueOf(criterion.getValue()));
				// CHECKSTYLE:OFF We need to prevent any exception to block here
			} catch (Exception e) {
				// CHECKSYLE:ON
				TuleapTasksUIPlugin.log(e, false);
			}
		}
		elements.add(new TuleapIntegerQueryElement(field, text));
		text.addModifyListener(page.getModifyListener());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapMultiSelectBox)
	 */
	@Override
	public void visit(TuleapMultiSelectBox field) {
		createGroupContentForSelectBox(field);
	}

	/**
	 * Select items in a list widget.
	 *
	 * @param widget
	 *            List widget
	 * @param selectedItems
	 *            Indexes of items to select in the widget
	 */
	private void selectItems(org.eclipse.swt.widgets.List widget, List<Integer> selectedItems) {
		if (selectedItems == null || selectedItems.isEmpty()) {
			widget.setSelection(0);
		} else {
			int[] array = new int[selectedItems.size()];
			for (int i = 0; i < selectedItems.size(); i++) {
				array[i] = selectedItems.get(i).intValue();
			}
			widget.setSelection(array);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapOpenList)
	 */
	@Override
	public void visit(TuleapOpenList field) {
		Text text = createGroupContentForString(field.getLabel(), field);
		loadValueForString(field, text);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapSelectBox)
	 */
	@Override
	public void visit(TuleapSelectBox field) {
		createGroupContentForSelectBox(field);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapString)
	 */
	@Override
	public void visit(TuleapString field) {
		Text text = createGroupContentForString(field.getLabel(), field);
		loadValueForString(field, text);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor#visit(org.tuleap.mylyn.task.core.internal.model.config.field.TuleapText)
	 */
	@Override
	public void visit(TuleapText field) {
		// Create a multi line text for each Tuleap text
		Label label = new Label(group, SWT.NONE);
		label.setText(field.getLabel());

		Combo combo = new Combo(group, SWT.SINGLE | SWT.READ_ONLY);
		combo.setItems(new String[] {ITuleapQueryOptions.StringOptions.OPTION_CONTAINS, });
		combo.setText(ITuleapQueryOptions.StringOptions.OPTION_CONTAINS);

		Text text = new Text(group, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gridData);
		loadValueForString(field, text);
	}

	/**
	 * Creates the dedicated type of widget for the Tuleap string kind field.
	 *
	 ** @param tuleapLabel
	 *            The label
	 * @param field
	 *            The tuleap field
	 * @return The Text widget created.
	 */
	private Text createGroupContentForString(String tuleapLabel, AbstractTuleapField field) {
		Label label = new Label(group, SWT.NONE);
		label.setText(tuleapLabel);

		Combo combo = new Combo(group, SWT.SINGLE | SWT.READ_ONLY);
		combo.setItems(new String[] {ITuleapQueryOptions.StringOptions.OPTION_CONTAINS, });
		combo.setText(ITuleapQueryOptions.StringOptions.OPTION_CONTAINS);

		Text text = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gridData);
		return text;
	}

	/**
	 * @param field
	 * @param text
	 */
	private void loadValueForString(AbstractTuleapField field, Text text) {
		// CHECKSTYLE:OFF
		Type criterionType = new TypeToken<TuleapQueryCriterion<String>>() {
			// Nothing
		}.getType();
		// CHECKSTYLE:ON
		if (currentQueryCriteria.has(field.getName())) {
			JsonElement json = currentQueryCriteria.get(field.getName());
			try {
				TuleapQueryCriterion<?> criterion = gson.fromJson(json, criterionType);
				text.setText(String.valueOf(criterion.getValue()));
				// CHECKSTYLE:OFF We need to prevent any exception to block here
			} catch (Exception e) {
				// CHECKSYLE:ON
				TuleapTasksUIPlugin.log(e, false);
			}
		}
		elements.add(new TuleapLiteralQueryElement(field, text));
	}

	/**
	 * Creates the selection criterion for select boxes (single or multi).
	 *
	 * @param field
	 *            The select box.
	 */
	private void createGroupContentForSelectBox(AbstractTuleapSelectBox field) {
		Label label = new Label(group, SWT.NONE);
		label.setText(field.getLabel());

		Combo combo = new Combo(group, SWT.SINGLE | SWT.READ_ONLY);
		combo.setItems(new String[] {ITuleapQueryOptions.StringOptions.OPTION_CONTAINS, });
		combo.setText(ITuleapQueryOptions.StringOptions.OPTION_CONTAINS);

		org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(group, SWT.MULTI | SWT.READ_ONLY
				| SWT.V_SCROLL | SWT.BORDER);
		List<String> items = new ArrayList<String>();
		items.add(""); //$NON-NLS-1$

		TuleapQueryCriterion<String[]> criterion = null;
		if (currentQueryCriteria.has(field.getName())) {
			JsonElement json = currentQueryCriteria.get(field.getName());
			// CHECKSTYLE:OFF
			Type boundCriterionType = new TypeToken<TuleapQueryCriterion<String[]>>() {
				// Nothing
			}.getType();
			// CHECKSTYLE:ON
			try {
				criterion = gson.fromJson(json, boundCriterionType);
				// CHECKSTYLE:OFF We need to prevent any exception to block here
			} catch (Exception e) {
				// CHECSKTYLE:ON
				TuleapTasksUIPlugin.log(e, false);
			}
		}

		// We need to work with criteria values instead of internal Tuleap integer IDs
		int index = 0;
		List<Integer> selectedItems = new ArrayList<Integer>();
		Collection<TuleapSelectBoxItem> selectBoxItems = field.getItems();
		for (TuleapSelectBoxItem item : selectBoxItems) {
			index++;
			String itemLabel = item.getLabel();
			items.add(itemLabel);
			if (criterion != null) {
				try {
					for (String criterionId : criterion.getValue()) {
						if (itemLabel.equals(criterionId)) {
							selectedItems.add(Integer.valueOf(index));
							break;
						}
					}
					// CHECKSTYLE:OFF We need to prevent any exception to block here
				} catch (Exception e) {
					// CHECSKTYLE:ON
					TuleapTasksUIPlugin.log(e, false);
				}
			}
		}
		list.setItems(items.toArray(new String[items.size()]));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		list.setLayoutData(gridData);

		selectItems(list, selectedItems);
		combo.addModifyListener(page.getModifyListener());

		elements.add(new TuleapSelectBoxQueryElement(field, list));
	}

	/**
	 * Provides a read-only view of the query elements built by this visitor.
	 *
	 * @return An unmodifiable list of the query elements.
	 */
	public List<AbstractTuleapCustomQueryElement<?>> getQueryElements() {
		return Collections.unmodifiableList(elements);
	}
}
