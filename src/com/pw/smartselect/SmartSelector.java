package com.pw.smartselect;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;

/**
 * @author Patrick.Wood
 */
public class SmartSelector implements IStartup {

	private static boolean useSmartSelect = true;
	public static final String USE_SMART_SELECT_PREF = "Use SmartSelect";
	public static final String PREF_STORE_INITIALISED = "SmartSelect pref store initialised";
	
	public static boolean useSmartSelect() {
		return useSmartSelect;
	}

	@Override
	public void earlyStartup() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		useSmartSelect = store.getBoolean(USE_SMART_SELECT_PREF);
	}
}
