package com.pw.smartselect;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.pw.smartselect.listener.SmartSelectionListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.pw.smartselect"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	public static final SmartSelectionListener SMART_SELECTION_LISTENER = new SmartSelectionListener();

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(SmartSelector.USE_SMART_SELECT_PREF, SmartSelector.useSmartSelect());
		if (smartSelectEnabled()) {
			IWindowListener listener = new IWindowListener() {
	
				@Override
				public void windowActivated(IWorkbenchWindow window) {
				}
	
				@Override
				public void windowClosed(IWorkbenchWindow window) {
					window.getSelectionService().removePostSelectionListener(SMART_SELECTION_LISTENER);
				}
	
				@Override
				public void windowDeactivated(IWorkbenchWindow window) {
				}
	
				@Override
				public void windowOpened(IWorkbenchWindow window) {
				}
			};
			final IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.addWindowListener(listener);
			
	/*		for (IViewDescriptor viewDesc : workbench.getViewRegistry().getViews()) {
				System.out.println("View ID = " + viewDesc.getId());
				//org.eclipse.jdt.ui.SourceView
			}
	
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			final IPartListener2 pl2 = new IPartListener2() {
	
				@Override
				public void partActivated(IWorkbenchPartReference arg0) {
					System.out.println("Part activated: " + arg0.getId());
				}
	
				@Override
				public void partBroughtToTop(IWorkbenchPartReference arg0) {
					System.out.println("Part brought to top: " + arg0.getId());
				}
	
				@Override
				public void partClosed(IWorkbenchPartReference arg0) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void partDeactivated(IWorkbenchPartReference arg0) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void partHidden(IWorkbenchPartReference arg0) {
					// TODO Auto-generated method stub
					
				}
	
				@Override
				public void partInputChanged(IWorkbenchPartReference arg0) {
					System.out.println("Part input changed: " + arg0.getId());
				}
	
				@Override
				public void partOpened(IWorkbenchPartReference arg0) {
					System.out.println("Part opened: " + arg0.getId());
				}
	
				@Override
				public void partVisible(IWorkbenchPartReference arg0) {
					// TODO Auto-generated method stub
					
				}
	
			};*/
	        workbench.getDisplay().asyncExec(new Runnable() {
	            public void run() {
	                for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
	                	window.getSelectionService().addPostSelectionListener(SMART_SELECTION_LISTENER);
	/*                	window.getPartService().addPartListener(pl2);
	            		// ITextEditor editor;
	            		((StyledText)workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor().getAdapter(org.eclipse.swt.widgets.Control.class)).addKeyListener(new KeyListener() {
	
	            		    @Override
	            		    public void keyReleased(KeyEvent e) {
	            		        System.out.println("key released");
	            		    }
	
	            		    @Override
	            		    public void keyPressed(KeyEvent e) {
	            		        System.out.println("key pressed ");
	            		    }
	            		});*/
	//                	window.getSelectionService().addSelectionListener("org.eclipse.ui.DefaultTextEditor", smartSelectionListener);
	//                	window.getSelectionService().addSelectionListener("org.eclipse.jdt.ui.CompilationUnitEditor", smartSelectionListener);
	                }
	            }
	        });
		}
	}

	private boolean smartSelectEnabled() {
		boolean smartSelectEnabled = false;
		IPreferenceStore store = getPreferenceStore();
		if ("true".equals(store.getString(SmartSelector.PREF_STORE_INITIALISED))) {
			smartSelectEnabled = store.getBoolean(SmartSelector.USE_SMART_SELECT_PREF);
		} else {
			smartSelectEnabled = store.getDefaultBoolean(SmartSelector.USE_SMART_SELECT_PREF);
		}
		return smartSelectEnabled;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
