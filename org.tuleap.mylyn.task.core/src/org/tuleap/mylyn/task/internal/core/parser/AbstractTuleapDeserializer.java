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
package org.tuleap.mylyn.task.internal.core.parser;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tuleap.mylyn.task.internal.core.data.BoundFieldValue;
import org.tuleap.mylyn.task.internal.core.data.LiteralFieldValue;
import org.tuleap.mylyn.task.internal.core.model.AbstractTuleapConfigurableElement;

/**
 * This class is used to deserialize a JSON representation of a Tuleap object.
 * 
 * @param <T>
 *            The type of the agile element to deserialize.
 * @author <a href="mailto:cedric.notot@obeo.fr">Cedric Notot</a>
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 */
public abstract class AbstractTuleapDeserializer<T extends AbstractTuleapConfigurableElement> implements JsonDeserializer<T> {

	/**
	 * The key used for the id of the POJO.
	 */
	private static final String ID = "id"; //$NON-NLS-1$

	/**
	 * The key used for the label of the POJO.
	 */
	private static final String LABEL = "label"; //$NON-NLS-1$

	/**
	 * The key used for the URL of the POJO.
	 */
	private static final String URL = "url"; //$NON-NLS-1$

	/**
	 * The key used for the HTML URL of the POJO.
	 */
	private static final String HTML_URL = "html_url"; //$NON-NLS-1$

	/**
	 * The key used for the field values of the POJO.
	 */
	private static final String VALUES = "values"; //$NON-NLS-1$

	/**
	 * The key used to retrieve the id of a field of the POJO.
	 */
	private static final String FIELD_ID = "field_id"; //$NON-NLS-1$

	/**
	 * The key used to retrieve the value of a field of the POJO.
	 */
	private static final String FIELD_VALUE = "value"; //$NON-NLS-1$

	/**
	 * The key used to retrieve the bind value id.
	 */
	private static final String FIELD_BIND_VALUE_ID = "bind_value_id"; //$NON-NLS-1$

	/**
	 * The key used to retrieve the list of bind value ids.
	 */
	private static final String FIELD_BIND_VALUE_IDS = "bind_value_ids"; //$NON-NLS-1$

	/**
	 * The pattern used to format date following the ISO8601 standard.
	 */
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type,
	 *      com.google.gson.JsonDeserializationContext)
	 */
	public T deserialize(JsonElement rootJsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

		JsonObject jsonObject = rootJsonElement.getAsJsonObject();

		int id = jsonObject.get(ID).getAsInt();
		String label = jsonObject.get(LABEL).getAsString();
		String url = jsonObject.get(URL).getAsString();
		String htmlUrl = jsonObject.get(HTML_URL).getAsString();
		int configurationId = jsonObject.get(this.getTypeIdKey()).getAsInt();

		// TODO Fix the dates from the parsing
		T pojo = buildPojo(id, configurationId, label, url, htmlUrl, new Date(), new Date());

		JsonArray fields = jsonObject.get(VALUES).getAsJsonArray();
		for (JsonElement field : fields) {
			JsonObject jsonField = field.getAsJsonObject();
			int fieldId = jsonField.get(FIELD_ID).getAsInt();
			JsonElement jsonValue = jsonField.get(FIELD_VALUE);
			if (jsonValue != null) {
				if (jsonValue.isJsonPrimitive()) {
					JsonPrimitive primitive = jsonValue.getAsJsonPrimitive();
					if (primitive.isString()) {
						pojo.addFieldValue(new LiteralFieldValue(fieldId, primitive.getAsString()));
					} else if (primitive.isNumber()) {
						pojo.addFieldValue(new LiteralFieldValue(fieldId, String.valueOf(primitive
								.getAsNumber())));
					} else if (primitive.isBoolean()) {
						pojo.addFieldValue(new LiteralFieldValue(fieldId, String.valueOf(primitive
								.getAsBoolean())));
					}
				}
			} else {
				JsonElement jsonBindValueId = jsonField.get(FIELD_BIND_VALUE_ID);
				if (jsonBindValueId != null) {
					int bindValueId = jsonBindValueId.getAsInt();
					pojo.addFieldValue(new BoundFieldValue(fieldId, Lists.newArrayList(Integer
							.valueOf(bindValueId))));
				} else {
					JsonElement jsonBindValueIds = jsonField.get(FIELD_BIND_VALUE_IDS);
					if (jsonBindValueIds != null) {
						JsonArray jsonIds = jsonBindValueIds.getAsJsonArray();
						List<Integer> bindValueIds = new ArrayList<Integer>();
						for (JsonElement idElement : jsonIds) {
							bindValueIds.add(Integer.valueOf(idElement.getAsInt()));
						}
						pojo.addFieldValue(new BoundFieldValue(fieldId, bindValueIds));
					} else {
						// TODO Files
					}
				}
			}
		}

		return pojo;
	}

	/**
	 * Instantiates the relevant class of POJO to fill.
	 * 
	 * @param id
	 *            The identifier
	 * @param configurationId
	 *            The identifier of the configuration
	 * @param label
	 *            The label
	 * @param url
	 *            The url
	 * @param htmlUrl
	 *            The HTML URL
	 * @param creationDate
	 *            The creation date
	 * @param lastModificationDate
	 *            The last modification date
	 * @return The POJO.
	 */
	protected abstract T buildPojo(int id, int configurationId, String label, String url, String htmlUrl,
			Date creationDate, Date lastModificationDate);

	/**
	 * Returns the key of the type id.
	 * 
	 * @return The key of the type id
	 */
	protected abstract String getTypeIdKey();

}
