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
package org.tuleap.mylyn.task.internal.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.tuleap.mylyn.task.internal.core.TuleapCoreActivator;

/**
 * Utility class containing various simple static utility methods.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 * @since 0.7
 */
public final class TuleapUtil {
	/**
	 * Max transfer count.
	 */
	private static final int MAX_TRANSFER_COUNT = 24;

	/**
	 * Transfer count.
	 */
	private static final int TRANSFER_COUNT = 1 << MAX_TRANSFER_COUNT;

	/**
	 * The constructor.
	 */
	private TuleapUtil() {
		// prevent instantiation
	}

	/**
	 * Returns a new {@link java.util.Date} from the given long representing the date in seconds.
	 * 
	 * @param seconds
	 *            The timestamp in seconds
	 * @return A new {@link java.util.Date} from the given long representing the date in seconds.
	 */
	public static Date parseDate(long seconds) {
		return new Date(seconds * 1000L);
	}

	/**
	 * Returns a new long from the given {@link java.util.Date} representing the timestamp.
	 * 
	 * @param date
	 *            The date
	 * @return A new long from the given {@link java.util.Date} representing the timestamp.
	 */
	public static long parseDate(Date date) {
		return date.getTime() / 1000L;
	}

	/**
	 * Download a file from an URL.
	 * 
	 * @param url
	 *            File to download
	 * @param file
	 *            Path to save file
	 */
	public static void download(String url, File file) {
		ReadableByteChannel rbc = null;
		FileOutputStream fos = null;
		try {
			rbc = Channels.newChannel(new URL(url).openStream());
			fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, TRANSFER_COUNT);
		} catch (UnknownHostException e) {
			TuleapCoreActivator.log(TuleapMylynTasksMessages.getString("TuleapUtil.UnknownHost", url), true); //$NON-NLS-1$
		} catch (IOException e) {
			TuleapCoreActivator.log(e, true);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					TuleapCoreActivator.log(e, true);
				} finally {
					if (rbc != null) {
						try {
							rbc.close();
						} catch (IOException e) {
							TuleapCoreActivator.log(e, true);
						}
					}
				}
			}
		}
	}

	/**
	 * Calculate the file's checksum.
	 * 
	 * @param file
	 *            File
	 * @return The checksum of the given file
	 */
	public static long getChecksum(File file) {
		long checksum = -1;

		FileInputStream inputStream = null;
		CheckedInputStream check = null;
		BufferedInputStream in = null;
		try {
			inputStream = new FileInputStream(file);
			check = new CheckedInputStream(inputStream, new CRC32());
			in = new BufferedInputStream(check);
			while (in.read() != -1) {
				// Read file in completely
			}
			checksum = check.getChecksum().getValue();
		} catch (FileNotFoundException e) {
			TuleapCoreActivator.log(e, true);
		} catch (IOException e) {
			TuleapCoreActivator.log(e, true);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (check != null) {
					check.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				TuleapCoreActivator.log(e, true);
			}
		}

		return checksum;
	}

	/**
	 * Extract the domain name URL from the repository URL.
	 * 
	 * @param repositoryUrl
	 *            The task repository url : "https://<domainName>/plugins/tracker/?group_id=<groupId>"
	 * @return The domain name URL : "https://<domainName>/"
	 */
	public static String getDomainRepositoryURL(String repositoryUrl) {
		if (repositoryUrl.contains(ITuleapConstants.TULEAP_REPOSITORY_URL_STRUCTURE)) {
			return repositoryUrl.substring(0, repositoryUrl
					.indexOf(ITuleapConstants.TULEAP_REPOSITORY_URL_STRUCTURE));
		}
		return repositoryUrl;
	}

	/**
	 * Returns the task data id from the given tracker id and the given artifact id.
	 * 
	 * @param projectName
	 *            The name of the project
	 * @param trackerId
	 *            The id of the tracker.
	 * @param artifactId
	 *            The id of the artifact
	 * @return The task data id
	 */
	public static String getTaskDataId(String projectName, String trackerId, int artifactId) {
		return projectName + ITuleapConstants.TRACKER_ID_SEPARATOR + trackerId
				+ ITuleapConstants.TASK_DATA_ID_SEPARATOR + Integer.valueOf(artifactId).toString();
	}

	/**
	 * Indicates if the URL is valid.
	 * 
	 * @param url
	 *            The URL
	 * @return <code>true</code> if the url is valid, <code>false</code> otherwise.
	 */
	public static boolean isValidUrl(String url) {
		return url.matches("https://.*"); //$NON-NLS-1$
	}
}