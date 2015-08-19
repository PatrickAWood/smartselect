/**
 * 
 */
package com.pw.smartselect.preferences;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

import com.pw.smartselect.Activator;
import com.pw.smartselect.SmartSelector;

/**
 * @author Patrick.Wood
 *
 */
public class SmartSelectPrefs extends PreferencePage implements IWorkbenchPreferencePage {

	private Button smartSelectEnabledCheckBox;
	
    /**
     * Creates a new checkbox instance and sets the default layout data.
     *
     * @param group  the composite in which to create the checkbox
     * @param label  the string to set into the checkbox
     * @return the new checkbox
     */
    private Button createCheckBox(final Composite group, final String label) {
        Button button = new Button(group, SWT.CHECK | SWT.LEFT);
        button.setText(label);
        GridData data = new GridData();
        button.setLayoutData(data);
        return button;
    }

    /**
     * Creates composite control and sets the default layout data.
     *
     * @param parent  the parent of the new composite
     * @param numColumns  the number of columns for the new composite
     * @return the newly-created composite
     */
    private Composite createComposite(Composite parent, int numColumns) {
        Composite composite = new Composite(parent, SWT.NULL);

        //GridLayout
        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;
        composite.setLayout(layout);

        //GridData
        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(data);
        return composite;
    }
    
    @Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite_tab = createComposite(parent, 1);
		Composite composite_checkBox = createComposite(composite_tab, 1);
		smartSelectEnabledCheckBox = createCheckBox(composite_checkBox, "Use SmartSelect"); 
		initializeValues();
		return new Composite(parent, SWT.NULL);
	}
	
	private void initializeValues() {
		IPreferenceStore store = getPreferenceStore();
		if ("true".equals(store.getString(SmartSelector.PREF_STORE_INITIALISED))) {
			smartSelectEnabledCheckBox.setSelection(store.getBoolean(SmartSelector.USE_SMART_SELECT_PREF));
		} else {
			smartSelectEnabledCheckBox.setSelection(store.getDefaultBoolean(SmartSelector.USE_SMART_SELECT_PREF));
		}
	}

    @Override
	protected void performDefaults() {
        super.performDefaults();
        IPreferenceStore store = getPreferenceStore();
        smartSelectEnabledCheckBox.setSelection(store.getDefaultBoolean(SmartSelector.USE_SMART_SELECT_PREF));
    }

    @Override
	public boolean performOk() {
    	IPreferenceStore store = getPreferenceStore();
    	store.setValue(SmartSelector.USE_SMART_SELECT_PREF, smartSelectEnabledCheckBox.getSelection());
    	store.setValue(SmartSelector.PREF_STORE_INITIALISED, "true");
    	try {
    		// save updated prefs
			InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException bse) {
			Activator.getDefault().getLog()
				.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Error persisting preferences for SmartSelector", bse));
		}
		final IWorkbench workbench = PlatformUI.getWorkbench();
        if (smartSelectEnabledCheckBox.getSelection()) {
            for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
            	// add has no effect if listener already registered
            	window.getSelectionService().addPostSelectionListener(Activator.SMART_SELECTION_LISTENER);
            }
        } else {
            for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
            	// remove has no effect if listener not already registered
            	window.getSelectionService().removePostSelectionListener(Activator.SMART_SELECTION_LISTENER);
            }
        }
        return true;
    }

	@Override
	public void init(IWorkbench arg0) {
		// do nothing
	}
}
