package pixels.ui;

import pixels.entry.Day;
import pixels.entry.Entries;
import yanwittmann.utils.Log;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

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
    private JButton previousResultButton;
    private JButton nextResultButton;
    private JPanel leftSidePanel;
    private JTextArea resultsColorTextArea;
    private JPanel panelNavigateButtons;
    private JLabel labelStatus;

    private final Entries entries;

    private static GuiMainView self;

    public GuiMainView(Entries entries) {
        self = this;
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
                boolean ignoreCase = dialog.ignoreCase();
                boolean notes = dialog.searchInNotes();
                boolean tags = dialog.searchInTags();
                boolean date = dialog.searchInDate();
                String search = dialog.getSearch();

                if (ignoreCase) search = search.toLowerCase(Locale.ROOT);
                entries.search(search, allTerms, notes, tags, date, ignoreCase);
                setStatus(MessageFormat.format("{0} results for search {1}", entries.getResults().size(), Arrays.toString(search.split(" "))));
                nextSearchResult();
            }
        });
        previousResultButton.addActionListener(e -> {
            previousSearchResult();
        });
        nextResultButton.addActionListener(e -> {
            nextSearchResult();
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

        prepareButton(buttonPreviousMonth);
        prepareButton(buttonNextMonth);
        prepareButton(buttonGoToMonth);
        prepareButton(buttonSearch);
        prepareButton(previousResultButton);
        prepareButton(nextResultButton);

        Calendar calendar = Calendar.getInstance();
        setMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);

        resultsColorTextArea.setBackground(Entries.MOOD_MISSING);
    }

    private void prepareButton(JButton button) {
        button.setBackground(BUTTON_COLOR);
        button.setBorder(BUTTON_BORDER);
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
            entries.setDayToButton(pixels[i], notesTextArea, tagsLabel, resultsPane, resultsColorTextArea, year, month, counter);
        }
        highlightAllResults();
    }

    private void hidePixels() {
        for (JButton pixel : pixels) pixel.setVisible(false);
    }

    private void nextSearchResult() {
        if (entries.getResults() != null) {
            Day searchResult = entries.nextSearchResult();
            if (searchResult == null) return;
            setMonth(searchResult.getYear(), searchResult.getMonth());
            searchResult.loadDayToResults();
        }
    }

    private void previousSearchResult() {
        if (entries.getResults() != null) {
            Day searchResult = entries.previousSearchResult();
            if (searchResult == null) return;
            setMonth(searchResult.getYear(), searchResult.getMonth());
            searchResult.loadDayToResults();
        }
    }

    public static void highlightAllResults() {
        self.removeResultsHighlight();
        if (self.entries.getResults() != null)
            for (Day result : self.entries.getResults())
                if (self.currentMonth == result.getMonth() && self.currentYear == result.getYear())
                    result.highlightButton(GuiMainView.HIGHLIGHT);
    }

    private void removeResultsHighlight() {
        for (JButton pixel : pixels) pixel.setBorder(NO_HIGHLIGHT);
    }

    public void setStatus(String text) {
        labelStatus.setText(text);
    }

    public JPanel getMainPane() {
        return mainPane;
    }

    private final static Color BUTTON_COLOR = Color.WHITE;
    public final static Border BUTTON_BORDER = new LineBorder(new Color(0, 0, 0, 0), 4);
    public final static Border HIGHLIGHT = new LineBorder(Color.BLACK, 3);
    public final static Border HIGHLIGHT_SELECTED = new LineBorder(Color.BLUE, 3);
    public final static Border NO_HIGHLIGHT = null;
}
