/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package customControls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Steewsc
 */
public class SComboBox extends JComboBox<Object> {

    public SComboBox(ComboBoxModel d) {
	super.setModel(d);
	setAutoComplete();
    }

    public SComboBox() {
	setAutoComplete();
    }

    private void setAutoComplete() {
	new AutoCompleteComboBox(this);
    }

    @Override
    public void setModel(ComboBoxModel e) {
	super.setModel(e);
	if (e.getSize() > 0) {
	    setAutoComplete();
	}
    }

    /**
     * Get text of the selected item
     *
     * @return String
     */
    public String getText() {
	return super.getSelectedItem().toString();
    }

    /**
     * Appends new item at the end of the ComboBox list
     *
     * @param String item
     */
    public void addItem(String item) {
	super.addItem(makeObj(item));
    }

    /**
     * Appends new items at the end of the ComboBox list
     *
     * @param String[] items
     */
    public void addItems(String[] items) {
	try {
	    for (String item : items) {
		addItem(item);
	    }
	} catch (Exception e) {
	}
    }

    /**
     * Set an array items as an ComboBox items
     *
     * @param String[] items
     */
    public void setItems(String[] items) {
	super.setModel(new DefaultComboBoxModel(items));
    }

    private static Object makeObj(final String item) {
	try {
	    if (item != null) {
		return new Object() {
		    @Override
		    public String toString() {
			return item;
		    }
		};
	    } else {
		return new Object() {
		    @Override
		    public String toString() {
			return " - ";
		    }
		};
	    }
	} catch (NullPointerException e) {
	    return new Object() {
		@Override
		public String toString() {
		    return " - ";
		}
	    };
	}
    }
    
    class AutoCompleteComboBox extends PlainDocument {

	JComboBox comboBox;
	ComboBoxModel model;
	JTextComponent editor;
	boolean selecting = false;
	boolean hidePopupOnFocusLoss;
	boolean hitBackspace = false;
	boolean hitBackspaceOnSelection;

	public AutoCompleteComboBox(final JComboBox comboBox) {
	    this.comboBox = comboBox;
	    model = comboBox.getModel();
	    editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
	    editor.setDocument(this);
	    comboBox.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (!selecting) {
			highlightCompletedText(0);
		    }
		}
	    });
	    editor.addKeyListener(new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
		    if (comboBox.isDisplayable()) {
			comboBox.setPopupVisible(true);
		    }
		    hitBackspace = false;
		    switch (e.getKeyCode()) {
			case KeyEvent.VK_BACK_SPACE:
			    hitBackspace = true;
			    hitBackspaceOnSelection = editor.getSelectionStart() != editor.getSelectionEnd();
			    break;
		    }
		}
	    });
	    hidePopupOnFocusLoss = System.getProperty("java.version").startsWith("1.5");
	    editor.addFocusListener(new FocusAdapter() {
		public void focusGained(FocusEvent e) {
		    highlightCompletedText(0);
		}

		public void focusLost(FocusEvent e) {
		    if (hidePopupOnFocusLoss) {
			comboBox.setPopupVisible(false);
		    }
		}
	    });
	    
	    Object selected = comboBox.getSelectedItem();
	    if (selected != null) {
		setText(selected.toString());
	    }
	    highlightCompletedText(0);
	}

	public void remove(int offs, int len) throws BadLocationException {
	    if (selecting) {
		return;
	    }
	    if (hitBackspace) {
		if (offs > 0) {
		    if (hitBackspaceOnSelection) {
			offs--;
		    }
		}
		highlightCompletedText(offs);
	    } else {
		super.remove(offs, len);
	    }
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
	    if (selecting) {
		return;
	    }
	    
	    super.insertString(offs, str, a);
	    Object item = lookupItem(getText(0, getLength()));

	    boolean listContainsSelectedItem = true;
	    if (item == null) {
		item = getText(0, getLength());
		listContainsSelectedItem = false;
	    }
	    setSelectedItem(item);
	    setText(item.toString());
	    if (listContainsSelectedItem) {
		highlightCompletedText(offs + str.length());
	    }
	}

	private void setText(String text) {
	    try {
		super.remove(0, getLength());
		super.insertString(0, text, null);
	    } catch (BadLocationException e) {
		throw new RuntimeException(e.toString());
	    }
	}

	private void highlightCompletedText(int start) {
	    try {
		editor.setCaretPosition(getLength());
	    } catch (Exception e) {
	    }
	    editor.moveCaretPosition(start);
	}

	private void setSelectedItem(Object item) {
	    selecting = true;
	    model.setSelectedItem(item);
	    selecting = false;
	}

	private Object lookupItem(String pattern) {
	    Object selectedItem = model.getSelectedItem();
	    if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
		return selectedItem;
	    } else {
		for (int i = 0, n = model.getSize(); i < n; i++) {
		    Object currentItem = model.getElementAt(i);
		    if (currentItem != null) {
			if (startsWithIgnoreCase(currentItem.toString(), pattern)) {
			    return currentItem;
			}
		    }
		}
	    }
	    
	    return null;
	}

	private boolean startsWithIgnoreCase(String str1, String str2) {
	    return str1.toUpperCase().startsWith(str2.toUpperCase());
	}
    }
}
