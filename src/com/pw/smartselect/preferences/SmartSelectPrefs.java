/**
 * 
 */
package com.pw.smartselect.preferences;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;

import com.pw.smartselect.Activator;
import com.pw.smartselect.SmartSelector;
import com.pw.smartselect.util.MessageUtil;

/**
 * @author Patrick.Wood
 *
 */
public class SmartSelectPrefs extends PreferencePage implements IWorkbenchPreferencePage, SelectionListener {

	private Button smartSelectEnabledCheckBox;
	private Button delayedSelectionRadioButton;
	private Button realTimeSelectionRadioButton;

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
        button.addSelectionListener(this);
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
	protected Control createContents(Composite parent) {
		Composite composite1 = createComposite(parent, 1);
		Composite composite_checkBox = createComposite(composite1, 1);
		smartSelectEnabledCheckBox = createCheckBox(composite_checkBox, "Use SmartSelector");
		//
		Composite composite2 = createComposite(parent, 2);
		//
		tabForward(composite2);
		Composite composite_radioButton = createComposite(composite2, 1);
		delayedSelectionRadioButton = createRadioButton(composite_radioButton, MessageUtil.getString("Delayed-Selection-Message-Button-Label")); 
		realTimeSelectionRadioButton = createRadioButton(composite_radioButton, MessageUtil.getString("Real-Time-Selection-Message-Button-Label")); 
		initialiseValues();
		return new Composite(parent, SWT.NULL);
	}

    /**
     * Utility method that creates a radio button instance
     * and sets the default layout data.
     *
     * @param parent  the parent for the new button
     * @param label  the label for the new button
     * @return the newly-created button
     */
    private Button createRadioButton(Composite parent, String label) {
        Button button = new Button(parent, SWT.RADIO | SWT.LEFT);
        button.setText(label);
        GridData data = new GridData();
        button.setLayoutData(data);
        return button;
    }

    /**
     * Creates a tab of one horizontal spans.
     *
     * @param parent  the parent in which the tab should be created
     */
    private void tabForward(Composite parent) {
        Label vfiller = new Label(parent, SWT.LEFT);
        GridData gridData = new GridData();
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessVerticalSpace = false;
        vfiller.setLayoutData(gridData);
    }
	
	private void initialiseValues() {
		IEclipsePreferences store = Activator.getDefault().getEclipsePreferenceStore();
		smartSelectEnabledCheckBox.setSelection(store.getBoolean(SmartSelector.USE_SMART_SELECT, SmartSelector.IS_SMART_SELECT_ENABLED_BY_DEFAULT));
		delayedSelectionRadioButton.setSelection(store.getBoolean(SmartSelector.USE_DELAYED_SELECTION, SmartSelector.IS_DELAYED_SELECTION_ENABLED_BY_DEFAULT));
		realTimeSelectionRadioButton.setSelection(!store.getBoolean(SmartSelector.USE_DELAYED_SELECTION, SmartSelector.IS_DELAYED_SELECTION_ENABLED_BY_DEFAULT));
		realTimeSelectionRadioButton.setEnabled(smartSelectEnabledCheckBox.getSelection());
		delayedSelectionRadioButton.setEnabled(smartSelectEnabledCheckBox.getSelection());
	}

    @Override
	protected void performDefaults() {
        super.performDefaults();
        smartSelectEnabledCheckBox.setSelection(SmartSelector.IS_SMART_SELECT_ENABLED_BY_DEFAULT);
        delayedSelectionRadioButton.setSelection(SmartSelector.IS_DELAYED_SELECTION_ENABLED_BY_DEFAULT);
        realTimeSelectionRadioButton.setSelection(!SmartSelector.IS_DELAYED_SELECTION_ENABLED_BY_DEFAULT);
        toggleRadioButtons();
    }

    @Override
	public boolean performOk() {
    	IEclipsePreferences store = Activator.getDefault().getEclipsePreferenceStore();
    	store.putBoolean(SmartSelector.USE_SMART_SELECT, smartSelectEnabledCheckBox.getSelection());
    	store.putBoolean(SmartSelector.USE_DELAYED_SELECTION, delayedSelectionRadioButton.getSelection());
    	try {
    		// save updated prefs
    		store.flush();
		} catch (BackingStoreException bse) {
			Activator.getDefault().getLog()
				.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Error persisting preferences for SmartSelector", bse));
		}
//		final IWorkbench workbench = PlatformUI.getWorkbench();
        if (smartSelectEnabledCheckBox.getSelection()) {
        	Activator.getDefault().addListener();
//            for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
//            	// add has no effect if listener already registered
//            	window.getSelectionService().addPostSelectionListener(Activator.SMART_SELECTION_LISTENER);
//            }
        } else {
        	Activator.getDefault().removeListener();
//            for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
//            	// remove has no effect if listener not already registered
//            	window.getSelectionService().removePostSelectionListener(Activator.SMART_SELECTION_LISTENER);
//            }
        }
        return true;
    }

	@Override
	public void init(IWorkbench arg0) {
		// do nothing
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent selectionEvent) {
		// TODO Auto-generated method stub
		
	}

	private void toggleRadioButtons() {
		realTimeSelectionRadioButton.setEnabled(smartSelectEnabledCheckBox.getSelection());
		delayedSelectionRadioButton.setEnabled(smartSelectEnabledCheckBox.getSelection());
	}
	
	@Override
	public void widgetSelected(SelectionEvent selectionEvent) {
		toggleRadioButtons();
	}
}
