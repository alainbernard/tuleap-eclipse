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
package org.tuleap.mylyn.task.core.internal.model.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.tuleap.mylyn.task.core.internal.model.config.field.ITuleapFieldVisitor;
import org.tuleap.mylyn.task.core.internal.model.data.AbstractFieldValue;
import org.tuleap.mylyn.task.core.internal.model.data.LiteralFieldValue;

/**
 * Tuleap fields represents date, textfield or combo box.
 *
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 * @since 0.7
 */
public abstract class AbstractTuleapField extends AbstractTuleapFormElement {

	/**
	 * The serialization ID.
	 */
	private static final long serialVersionUID = 1254204722841825488L;

	/**
	 * The constructor.
	 *
	 * @param formElementIdentifier
	 *            The identifier of the form element
	 */
	public AbstractTuleapField(int formElementIdentifier) {
		super(formElementIdentifier);
	}

	/**
	 * Returns the kind of task attribute that Mylyn should use to represent the Tuleap field.
	 *
	 * @return The kind of task attribute that Mylyn should use to represent the Tuleap field.
	 */
	public abstract String getMetadataKind();

	/**
	 * Returns the type of task attribute that Mylyn should use to represent the Tuleap field.
	 *
	 * @return The type of task attribute that Mylyn should use to represent the Tuleap field.
	 */
	public abstract String getMetadataType();

	/**
	 * Returns the default value of the task attribute that Mylyn will use.
	 *
	 * @return The default value of the task attribute that Mylyn will use.
	 */
	public abstract Object getDefaultValue();

	/**
	 * Indicates whether this field must provoke the creation of a TaskAttribute in the TaskData during its
	 * initialization. This method is intended to be overridden in subclasses.
	 *
	 * @return Returns {@code true} if a task attribute must be created, and {@code false} otherwise. The
	 *         default implementation returns systematically {@code true}.
	 */
	public boolean needsTaskAttributeForInitialization() {
		return true;
	}

	/**
	 * Creates an returns a new TaskAttribute using this field. This method is meant to be overridden in the
	 * subclasses to provide the relevant behavior depending on the type of tuleap field.
	 *
	 * @param parent
	 *            The parent task attribute in which the new task attribute must be created.
	 * @return The created task attribute.
	 */
	public TaskAttribute createTaskAttribute(TaskAttribute parent) {
		int id = getIdentifier();
		TaskAttribute attribute = this.getWriteableAttribute(parent, String.valueOf(id), getMetadataType());
		if (attribute != null) {
			Object defaultValue = getDefaultValue();
			if (defaultValue != null) {
				parent.getTaskData().getAttributeMapper().setValue(attribute, defaultValue.toString());
			}
			initializeMetaData(attribute.getMetaData());
			initializeAttribute(attribute);
		}

		return attribute;
	}

	/**
	 * Initializes the given attribute with relevant default value, and options if need be.
	 *
	 * @param attribute
	 *            The attribute
	 */
	public void initializeAttribute(TaskAttribute attribute) {
		updateDefaultValue(attribute);
		afterTaskAttributeCreation(attribute);
	}

	/**
	 * Instantiates the right kind of {@link AbstractFieldValue} for the given {@link TaskAttribute}. The
	 * default implementation returns a new {@link LiteralFieldValue}.
	 *
	 * @param attribute
	 *            The {@link TaskAttribute}
	 * @param fieldId
	 *            the ID of the field in the tuleap configuration.
	 * @return A new {@link AbstractFieldValue} of the relevant type.
	 */
	public AbstractFieldValue createFieldValue(TaskAttribute attribute, int fieldId) {
		String value = null;
		if (!attribute.getValues().isEmpty()) {
			value = attribute.getValue();
		}
		return new LiteralFieldValue(fieldId, value);
	}

	/**
	 * Sets the given value into the given {@link TaskAttribute}, in the relevant way.
	 *
	 * @param attribute
	 *            The task attribute to set
	 * @param value
	 *            The value to set
	 */
	public abstract void setValue(TaskAttribute attribute, AbstractFieldValue value);

	/**
	 * Initializes a newly created TaskAttribute's metadata. The default behavior is simply to set the kind to
	 * the kind of this field, but this method should be subclassed to implement the correct behavior.
	 *
	 * @param metaData
	 *            The newly created task attribute's metadata.
	 */
	protected void initializeMetaData(TaskAttributeMetaData metaData) {
		metaData.setKind(getMetadataKind());
		metaData.setLabel(getLabel());
		metaData.setReadOnly(!isSubmitable() && !isUpdatable());
	}

	/**
	 * Update a newly created task attribute's default value.
	 *
	 * @param attribute
	 *            The task attribute
	 */
	protected void updateDefaultValue(TaskAttribute attribute) {
		// TODO Check whether this strange mechanism of multiple default values is really useful
		Object defaultValue = getDefaultValue();
		if (defaultValue instanceof String) {
			attribute.setValue((String)defaultValue);
		} else if (defaultValue instanceof List<?>) {
			List<?> list = (List<?>)defaultValue;

			List<String> strList = new ArrayList<String>();
			for (Object object : list) {
				if (object instanceof String) {
					strList.add((String)object);
				}
			}

			attribute.setValues(strList);
		}
	}

	/**
	 * Callback method available to allow subclasses to plug-in additional treatments after the task
	 * attribute's creation.
	 *
	 * @param attribute
	 *            The newly created and initialized task attribute
	 */
	protected void afterTaskAttributeCreation(TaskAttribute attribute) {
		// Nothing to do, useful in subclasses.
	}

	/**
	 * Returns the writeable attribute with the given key and the given type.
	 * <p>
	 * If we can create non existing attribute (see constructor) and if an attribute with the given key does
	 * not exists, a new one will be created.
	 * </p>
	 *
	 * @param parent
	 *            The parent attribute (for instance, taskData.getRoot())
	 * @param attributeKey
	 *            The key of the attribute
	 * @param type
	 *            The type of the attribute
	 * @return The writeable attribute with the given key and the given type
	 */
	protected TaskAttribute getWriteableAttribute(TaskAttribute parent, String attributeKey, String type) {
		TaskAttribute attribute = parent.getMappedAttribute(attributeKey);
		if (attribute == null) {
			Field field = DefaultTaskSchema.getField(attributeKey);
			if (field != null) {
				attribute = field.createAttribute(parent);
			} else {
				attribute = parent.createMappedAttribute(attributeKey);
				if (type != null) {
					attribute.getMetaData().defaults().setType(type);
				}
			}
		}
		return attribute;
	}

	/**
	 * Method for visitor pattern, must be implemented in sub-classes.
	 *
	 * @param visitor
	 *            The visitor.
	 */
	public abstract void accept(ITuleapFieldVisitor visitor);
}
