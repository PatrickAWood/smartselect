/**
 * 
 */
package com.pw.smartselect.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.pw.smartselect.Activator;
import com.pw.smartselect.SmartSelector;

/**
 * @author Patrick.Wood
 */
public class SmartSelectionListener implements ISelectionListener {

	public static final Map<String, String[]> DELIMITER_MAP = new HashMap<String, String[]>() {{
		put("<",  new String[] {"<", ">"});
		put("\"", new String[] {"\\\"", "\""});
		put("(",  new String[] {"\\(", ")"});
		put("[",  new String[] {"\\[", "]"});
		put("{",  new String[] {"\\{", "}"});
		put("'",  new String[] {"'", "'"});
	}};

	public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
		if (Activator.getDefault().getPreferenceStore().getBoolean(SmartSelector.USE_SMART_SELECT_PREF)) {
			if (sourcepart instanceof ITextEditor) {
				ITextEditor editor = (ITextEditor)sourcepart;
				if (selection instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection) selection;
					if (!textSelection.isEmpty()) { // this could be true but text could be "" so check
						String selectedText = textSelection.getText();
						if (!"".equals(selectedText) && selectedText.indexOf("\n") == -1) { // don't process multi-line selections
							String openingDelimiter = findFirstOpenDelimiter(selectedText);
							if (openingDelimiter != null) {
								String extendedText = 
									matchDelimiter(openingDelimiter, getTextToEndOfLine(editor, textSelection), selectedText);
								if (extendedText != null && !extendedText.equals(selectedText)) {
									editor.selectAndReveal(textSelection.getOffset(), extendedText.length());
								}
							}
						}
					}
				}
			}
		}
	}
	
	public String findFirstOpenDelimiter(final String selection) {
		Set<String> closedDelimiters = new HashSet<String>();
		String openingDelimiter = null;
		for (int i = 0; i < selection.length(); i++) {
			String str = selection.substring(i, i + 1);
			if (!closedDelimiters.contains(str) && DELIMITER_MAP.containsKey(str)) {
				if (isOpen(str, selection.substring(i + 1))) {
					openingDelimiter = str;
					break;
				} else {
					closedDelimiters.add(str);
				}
			}
		}
		return openingDelimiter;
	}
	
	public boolean isOpen(final String openingDelimiter, final String selection) {
		String closingDelimiter = DELIMITER_MAP.get(openingDelimiter)[1];
		int openingNo = 1;
		int closingNo = 0;
		for (int i = 0; i < selection.length(); i++) {
			String str = selection.substring(i, i + 1);
			openingNo = str.equals(openingDelimiter) ? openingNo + 1 : openingNo;
			closingNo = str.equals(closingDelimiter) ? closingNo + 1 : closingNo;
		}
		return openingDelimiter.equals(closingDelimiter) ? openingNo % 2 != 0 : openingNo > closingNo;
	}

	public String matchDelimiter(final String openingDelimiter, final String selection, final String originalText) {
		String delimiterEnclosedString = null;
		String closingDelimiter = DELIMITER_MAP.get(openingDelimiter)[1];
		int openingNo = 0;
		int closingNo = 0;
		for (int i = 0; i < selection.length(); i++) {
			String str = selection.substring(i, i + 1);
			openingNo = str.equals(openingDelimiter) ? openingNo + 1 : openingNo;
			closingNo = openingNo > 0 && str.equals(closingDelimiter) ? closingNo + 1 : closingNo;
			if (openingNo > 0 && openingNo - closingNo == 0 && i >= originalText.length()) {
				delimiterEnclosedString = selection.substring(0, i + 1);
				break;
			}
		}
		return delimiterEnclosedString;
	}

	private String getTextToEndOfLine(final ITextEditor editor, final ITextSelection textSelection) {
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		int increasedSelectionLength = textSelection.getLength() + 1;
		String textToEndOfLine = textSelection.getText();;
		while (doc.getLength() >= textSelection.getOffset() + increasedSelectionLength
				&& !"\n".equals(textToEndOfLine.substring(textToEndOfLine.length() - 1))) {
			try {
				textToEndOfLine = doc.get(textSelection.getOffset(), increasedSelectionLength++);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		return textToEndOfLine;
	}
}
