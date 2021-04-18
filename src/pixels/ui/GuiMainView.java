package pixels.ui;

import pixels.entry.Entries;
import yanwittmann.utils.Log;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

public class GuiMainView {
    private JPanel dateSelectionPaneIJ;
    private JPanel mainPane;
    private JPanel dateSelectionPane;
    private JTextArea notesTextArea;
    private JPanel resultsPane;
    private JLabel tagsLabel;
    private JPanel navigatePanel;
    private JButton buttonPreviousMonth;
    private JButton buttonNextMonth;
    private JButton buttonGoToMonth;
    private JButton buttonSearch;

    private final Entries entries;

    public GuiMainView(Entries entries) {
        this.entries = entries;
        buttonPreviousMonth.addActionListener(e -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            setMonth(currentYear, currentMonth);
        });
        buttonNextMonth.addActionListener(e -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            setMonth(currentYear, currentMonth);
        });
        buttonGoToMonth.addActionListener(e -> {
            GuiMonthPicker dialog = new GuiMonthPicker();
            if (!dialog.isCancelled())
                setMonth(dialog.getYear(), dialog.getMonth());
        });
        afterCreation();
        buttonSearch.addActionListener(e -> {
            GuiSearchPixels dialog = new GuiSearchPixels();
            if (!dialog.isCancelled()) {
                boolean allTerms = dialog.allTermsMustBeContained();
                boolean notes = dialog.searchInNotes();
                boolean tags = dialog.searchInTags();
                boolean date = dialog.searchInDate();
                String search = dialog.getSearch();

                entries.search(search, allTerms, notes, tags, date);
            }
        });
    }

    private final JButton[] pixels = new JButton[42];
    private final String[] weekdays = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private void afterCreation() {
        GridLayout layout = new GridLayout(7, 6);
        layout.setHgap(4);
        layout.setVgap(4);
        dateSelectionPane.setLayout(layout);
        for (int i = 0; i < 7; i++) {
            JLabel label = new JLabel();
            label.setText(weekdays[i]);
            label.setPreferredSize(new Dimension(40, 20));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            dateSelectionPane.add(label);
        }
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = new JButton();
            pixels[i].setText((i + 1) + "");
            pixels[i].setVisible(false);
            pixels[i].setEnabled(true);
            pixels[i].setBackground(BUTTON_COLOR);
            pixels[i].setPreferredSize(new Dimension(40, 30));
            dateSelectionPane.add(pixels[i]);
        }
        dateSelectionPane.revalidate();
        dateSelectionPane.repaint();

        notesTextArea.setWrapStyleWord(true);

        buttonPreviousMonth.setBackground(BUTTON_COLOR);
        buttonNextMonth.setBackground(BUTTON_COLOR);
        buttonGoToMonth.setBackground(BUTTON_COLOR);
        buttonSearch.setBackground(BUTTON_COLOR);

        Calendar calendar = Calendar.getInstance();
        setMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }

    private int currentYear, currentMonth;

    public void setMonth(int year, int month) {
        currentYear = year;
        currentMonth = month;
        dateSelectionPaneIJ.setBorder(new TitledBorder("Select Pixel " + year + "/" + month));
        LocalDate l = LocalDate.of(year, month, 1);
        int startingDay = l.with(TemporalAdjusters.firstDayOfMonth()).getDayOfWeek().getValue() - 1;
        int endingDay = l.with(TemporalAdjusters.lastDayOfMonth()).getDayOfWeek().getValue() - 1;
        int endingDayDate = l.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        Log.info("Setting to year {} and month {} with starting day {} and ending day {} which is month day {}", year, month, startingDay, endingDay, endingDayDate);

        hidePixels();

        int counter = 0;
        for (int i = startingDay; i < endingDayDate + startingDay; i++) {
            counter++;
            entries.setDayToButton(pixels[i], notesTextArea, tagsLabel, resultsPane, year, month, counter);
        }
    }

    private void hidePixels() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i].setVisible(false);
        }
    }

    public JPanel getMainPane() {
        return mainPane;
    }

    private final static Color BUTTON_COLOR = Color.WHITE;
}
