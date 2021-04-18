package pixels.entry;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public String getDay() {
        return stringDate;
    }

    public boolean search(String[] search, boolean allTerms, boolean notes, boolean tags, boolean date) {
        boolean found = false;
        for (String term : search) {
            if (notes && this.notes.contains(term)) found = true;
            if (tags && tagsContain(term)) found = true;
            if (date && this.stringDate.contains(term)) found = true;
            if (allTerms && !found) return false;
            else if (!allTerms && found) return true;
        }
        return false;
    }

    private boolean tagsContain(String term) {
        return tags.entrySet().stream().anyMatch(tags -> tags.getValue().contains(term));
    }

    public void setButtonLoadDay(JButton button, JTextArea notes, JLabel tags, JPanel pixelsView) {
        button.setBackground(Entries.mapMoodToColor(mood));
        button.setVisible(true);
        Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener);
        button.addActionListener(e -> {
            pixelsView.setBorder(new TitledBorder(stringDate));
            tags.setText(getTags());
            notes.setText(this.notes);
        });
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