package pixels.ui;

import pixels.Constants;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;

public class GuiMonthPicker extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> yearComboBox;
    private JComboBox<String> monthComboBox;

    public GuiMonthPicker() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        Calendar calendar = Calendar.getInstance();
        DefaultComboBoxModel<String> years = new DefaultComboBoxModel<>(getArrayReverse(2000, calendar.get(Calendar.YEAR)));
        yearComboBox.setModel(years);
        DefaultComboBoxModel<String> months = new DefaultComboBoxModel<>(getArray(1, 12));
        monthComboBox.setModel(months);

        setTitle(Constants.FRAME_TITLE_GENERAL + " - " + Constants.FRAME_TITLE_GO_TO_MONTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String[] getArray(int start, int end) {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = start; i <= end; i++) arr.add(i + "");
        return arr.toArray(new String[0]);
    }

    private String[] getArrayReverse(int start, int end) {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = end; i >= start; i--) arr.add(i + "");
        return arr.toArray(new String[0]);
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

    public int getMonth() {
        return Integer.parseInt((String) monthComboBox.getSelectedItem());
    }

    public int getYear() {
        return Integer.parseInt((String) yearComboBox.getSelectedItem());
    }
}
