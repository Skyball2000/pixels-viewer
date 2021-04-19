package pixels.entry;

import org.json.JSONArray;
import org.json.JSONObject;
import pixels.ui.GuiMainView;
import yanwittmann.utils.Log;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class Day implements Comparable<Day> {

    private final Date date;
    private final String stringDate;
    private final boolean isHighlighted;
    private final String notes;
    private final int mood;
    private final HashMap<String, ArrayList<String>> tags = new HashMap<>();

    public Day(String input) throws ParseException {
        JSONObject mainObject = new JSONObject(input);
        date = INPUT_DATE_FORMAT.parse(mainObject.getString(KEY_DATE));
        stringDate = OUTPUT_DATE_FORMAT.format(date);
        JSONObject entries = mainObject.getJSONArray(KEY_ENTRIES).getJSONObject(0);
        isHighlighted = entries.getBoolean(KEY_IS_HIGHLIGHTED);
        notes = entries.getString(KEY_NOTES);
        mood = entries.getInt(KEY_MOOD_VALUE);

        JSONArray tags = entries.getJSONArray(KEY_TAGS);
        for (int i = 0; i < tags.length(); i++) {
            JSONObject tag = tags.getJSONObject(i);
            this.tags.put(tag.getString(KEY_TAG_TYPE), new ArrayList<>(tag.getJSONArray(KEY_TAG_ENTRIES).toList().stream().map(Object::toString).collect(Collectors.toList())));
        }
    }

    public int getMood() {
        return mood;
    }

    public String getNotes() {
        return notes;
    }

    public String getTags() {
        return this.tags.entrySet().stream().map(tag -> tag.getKey() + ": " + String.join(", ", tag.getValue().toArray(new String[0]))).collect(Collectors.joining("\n"));
    }

    public boolean isDay(String day) {
        return stringDate.equals(day);
    }

    public String getDate() {
        return stringDate;
    }

    public boolean search(String[] search, boolean allTerms, boolean notes, boolean tags, boolean date, boolean ignoreCase) {
        boolean found;
        for (String term : search) {
            found = false;
            if (notes)
                if (ignoreCase) {
                    if (this.notes.toLowerCase(Locale.ROOT).contains(term)) found = true;
                } else {
                    if (this.notes.contains(term)) found = true;
                }
            if (tags && tagsContain(term, ignoreCase)) found = true;
            if (date && this.stringDate.contains(term)) found = true;
            if (allTerms && !found) return false;
            else if (!allTerms && found) return true;
        }
        return allTerms;
    }

    private boolean tagsContain(String term, boolean ignoreCase) {
        if (ignoreCase)
            return tags.entrySet().stream().anyMatch(tags -> tags.getValue().stream().map(tag -> tag.toLowerCase(Locale.ROOT)).collect(Collectors.toList()).contains(term));
        return tags.entrySet().stream().anyMatch(tags -> tags.getValue().contains(term));
    }

    private JButton displayButton;
    private JTextArea textAreaNotes;
    private JTextArea textAreaResultsColor;
    private JLabel labelTags;
    private JPanel panelPixelsView;

    public void setButtonLoadDay(JButton button, JTextArea notes, JLabel tags, JPanel pixelsView, JTextArea resultsColor) {
        this.displayButton = button;
        this.textAreaNotes = notes;
        this.labelTags = tags;
        this.panelPixelsView = pixelsView;
        this.textAreaResultsColor = resultsColor;

        this.displayButton.setBackground(Entries.mapMoodToColor(mood));
        this.displayButton.setVisible(true);
        Arrays.stream(this.displayButton.getActionListeners()).forEach(this.displayButton::removeActionListener);
        this.displayButton.addActionListener(e -> loadDayToResults());
    }

    public void loadDayToResults() {
        if (displayButton.getBackground() == Entries.MOOD_MISSING) return;
        panelPixelsView.setBorder(new TitledBorder(stringDate));
        textAreaResultsColor.setBackground(Entries.mapMoodToColor(mood));
        labelTags.setText(getTags());
        textAreaNotes.setText(this.notes);
        GuiMainView.highlightAllResults();
        highlightButton(GuiMainView.HIGHLIGHT_SELECTED);
        GuiMainView.highlightAllCurrentTerms();
    }

    private static JButton lastSelectedButton;

    public void highlightButton(Border border) {
        if (lastSelectedButton != null && lastSelectedButton.getBorder() != GuiMainView.HIGHLIGHT)
            lastSelectedButton.setBorder(GuiMainView.NO_HIGHLIGHT);
        displayButton.setBorder(border);
        lastSelectedButton = displayButton;
    }

    public int getMonth() {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
    }

    public int getYear() {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
    }

    public int getDay() {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth();
    }

    public void print() {
        System.out.println(stringDate);
        System.out.println(isHighlighted);
        System.out.println(mood);
        System.out.println(notes);
        this.tags.entrySet().stream().map(tag -> tag.getKey() + " " + Arrays.toString(tag.getValue().toArray(new String[0]))).forEach(System.out::println);
    }

    private final static String KEY_DATE = "date";
    private final static String KEY_ENTRIES = "entries";
    private final static String KEY_IS_HIGHLIGHTED = "isHighlighted";
    private final static String KEY_NOTES = "notes";
    private final static String KEY_MOOD_VALUE = "value";
    private final static String KEY_TAGS = "tags";
    private final static String KEY_TAG_ENTRIES = "entries";
    private final static String KEY_TAG_TYPE = "type";

    private final static SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public int compareTo(Day o) {
        if (date.after(o.date)) return 1;
        else if (date.before(o.date)) return -1;
        else return 0;
    }
}