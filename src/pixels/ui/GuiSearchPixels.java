package pixels.ui;

import pixels.Constants;

import javax.swing.*;
import java.awt.event.*;

public class GuiSearchPixels extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox allTermsMustBeCheckBox;
    private JCheckBox searchInNotesCheckBox;
    private JTextField textFieldSearchText;
    private JCheckBox searchInDateCheckBox;
    private JCheckBox searchInTagsCheckBox;

    public GuiSearchPixels() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setTitle(Constants.FRAME_TITLE_GENERAL + " - " + Constants.FRAME_TITLE_SEARCH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        cancelled = true;
        dispose();
    }

    private boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public String getSearch() {
        return textFieldSearchText.getText();
    }

    public boolean searchInNotes() {
        return searchInNotesCheckBox.isSelected();
    }

    public boolean searchInTags() {
        return searchInTagsCheckBox.isSelected();
    }

    public boolean searchInDate() {
        return searchInDateCheckBox.isSelected();
    }

    public boolean allTermsMustBeContained() {
        return allTermsMustBeCheckBox.isSelected();
    }
}
