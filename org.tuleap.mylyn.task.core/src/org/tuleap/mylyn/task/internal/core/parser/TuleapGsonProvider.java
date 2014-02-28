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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import org.tuleap.mylyn.task.internal.core.model.config.TuleapTracker;
import org.tuleap.mylyn.task.internal.core.model.config.TuleapWorkflowTransition;
import org.tuleap.mylyn.task.internal.core.model.data.TuleapArtifact;
import org.tuleap.mylyn.task.internal.core.model.data.agile.TuleapCard;
import org.tuleap.mylyn.task.internal.core.model.data.agile.TuleapCardwall;
import org.tuleap.mylyn.task.internal.core.model.data.agile.TuleapMilestone;
import org.tuleap.mylyn.task.internal.core.serializer.TuleapCardSerializer;
import org.tuleap.mylyn.task.internal.core.serializer.TuleapMilestoneSerializer;

/**
 * Utility class to configure the Gson instance(s) used by the connector for Tuleap.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 */
public final class TuleapGsonProvider {

	/**
	 * Private constructor, to prevent instantiation.
	 */
	private TuleapGsonProvider() {
		// Prevent instantiation of utility class
	}

	/**
	 * Provides the default Gson properly configured to parse JSON structures from Tuleap and serialize them
	 * too.
	 * 
	 * @return A properly configured new instance of Gson.
	 */
	public static Gson defaultGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(TuleapArtifact.class, new TuleapArtifactDeserializer());
		gsonBuilder.registerTypeAdapter(TuleapCardwall.class, new TuleapCardwallDeserializer());
		gsonBuilder.registerTypeAdapter(TuleapCard.class, new TuleapCardDeserializer());
		gsonBuilder.registerTypeAdapter(TuleapCard.class, new TuleapCardSerializer());
		gsonBuilder.registerTypeAdapter(TuleapMilestone.class, new TuleapMilestoneSerializer());
		gsonBuilder.registerTypeAdapter(TuleapTracker.class, new TuleapTrackerDeserializer());
		gsonBuilder.registerTypeAdapter(TuleapWorkflowTransition.class,
				new TuleapWorkflowTransitionDeserializer());
		gsonBuilder.registerTypeAdapter(Date.class, new DateIso8601Adapter());
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		return gsonBuilder.create();
	}
}
