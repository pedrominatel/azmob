/**
 * Created by Pedro Minatel
 * pminatel@gmail.com
 */
package com.thinken.azmobmeter;

/**
 * @author piiiters
 *
 */

/**
 * Encapsulates information about a news entry
 */
public final class MeterObjectsEntry {
	private final String objectName;
	private final String objectDesc;
	private final int icon;

	public MeterObjectsEntry(final String title, final String description, final int icon) {
		this.objectName = title;
		this.objectDesc = description;
		this.icon = icon;
	}

	/**
	 * @return Object Name
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @return Object Description
	 */
	public String getObjectDesc() {
		return objectDesc;
	}

	/**
	 * @return Icon
	 */
	public int getIcon() {
		return icon;
	}

}