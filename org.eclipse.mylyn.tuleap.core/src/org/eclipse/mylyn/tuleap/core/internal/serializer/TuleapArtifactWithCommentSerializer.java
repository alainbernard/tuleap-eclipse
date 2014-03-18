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
package org.eclipse.mylyn.tuleap.core.internal.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

import org.eclipse.mylyn.tuleap.core.internal.model.config.AbstractTuleapField;
import org.eclipse.mylyn.tuleap.core.internal.model.data.TuleapArtifactWithComment;
import org.eclipse.mylyn.tuleap.core.internal.util.ITuleapConstants;

/**
 * This class is used to serialize the JSON representation of a {@link TuleapArtifactWithComment}.
 * 
 * @author <a href="mailto:firas.bacha@obeo.fr">Firas Bacha</a>
 */
public class TuleapArtifactWithCommentSerializer extends AbstractTuleapSerializer<TuleapArtifactWithComment> {

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
	 *      com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(TuleapArtifactWithComment commentedArtifact, Type type,
			JsonSerializationContext context) {
		JsonObject elementObject = (JsonObject)super.serialize(commentedArtifact, type, context);
		elementObject.remove(ITuleapConstants.ID);
		String comment = commentedArtifact.getNewComment();
		if (comment != null && !comment.isEmpty()) {
			JsonObject commentObject = new JsonObject();
			elementObject.add(ITuleapConstants.COMMENT, commentObject);
			commentObject.add(ITuleapConstants.BODY, new JsonPrimitive(comment));
			commentObject.add(ITuleapConstants.FORMAT, new JsonPrimitive("text")); //$NON-NLS-1$
		}
		return elementObject;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.mylyn.tuleap.core.internal.serializer.AbstractTuleapSerializer#mustSerialize(org.eclipse.mylyn.tuleap.core.internal.model.config.AbstractTuleapField)
	 */
	@Override
	protected boolean mustSerialize(AbstractTuleapField field) {
		// Only fields valid for update are submitted
		return super.mustSerialize(field) && field.isUpdatable();
	}
}