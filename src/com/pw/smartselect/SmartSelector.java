package com.pw.smartselect;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;

/**
 * @author Patrick.Wood
 */
public class SmartSelector implements IStartup {

	public static final String USE_SMART_SELECT = "Use SmartSelect";
	public static final String USE_DELAYED_SELECTION = "Use delayed selection";
	public static final boolean IS_SMART_SELECT_ENABLED_BY_DEFAULT = true;
	public static final boolean IS_DELAYED_SELECTION_ENABLED_BY_DEFAULT = true;
	
	@Override
	public void earlyStartup() {
	}
}
