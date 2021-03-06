package pixels.entry;

import org.json.JSONArray;
import yanwittmann.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Entries {

    private List<Day> days = new ArrayList<>();

    public Entries(String input) throws ParseException {
        for (Object entry : new JSONArray(input)) days.add(new Day(entry.toString()));
        days = days.stream().sorted().collect(Collectors.toList());
    }

    public void setDayToButton(JButton button, JTextArea notes, JLabel tags, JPanel pixelsView, JTextArea resultsColor, int year, int month, int day) {
        Day foundDay = getDay(year + "/" + makeDoubleDigit(month) + "/" + makeDoubleDigit(day));
        button.setText(day + "");
        if (foundDay != null) {
            foundDay.setButtonLoadDay(button, notes, tags, pixelsView, resultsColor);
        } else {
            button.setBackground(MOOD_MISSING);
            button.setVisible(true);
        }
    }

    private List<Day> results;
    private int currentSearchResult = 0;

    public void search(String[] terms, boolean allTerms, boolean notes, boolean tags, boolean date, boolean ignoreCase) {
        Log.info("Searching for {}", Arrays.toString(terms));
        results = days.stream().filter(day -> day.search(terms, allTerms, notes, tags, date, ignoreCase)).collect(Collectors.toList());
        currentSearchResult = -1;
    }

    public Day nextSearchResult() {
        if(results.size() == 0) return null;
        currentSearchResult = (currentSearchResult + 1) % results.size();
        return results.get(currentSearchResult);
    }

    public Day previousSearchResult() {
        if(results.size() == 0) return null;
        currentSearchResult--;
        if (currentSearchResult < 0) currentSearchResult = results.size() - 1;
        return results.get(currentSearchResult);
    }

    public List<Day> getResults() {
        return results;
    }

    public void clearResults() {
        results.clear();
    }

    private String makeDoubleDigit(int n) {
        return n < 10 ? "0" + n : String.valueOf(n);
    }

    public Day getDay(String searchDate) {
        return days.stream().filter(day -> day.isDay(searchDate)).findFirst().orElse(null);
    }

    public static Color mapMoodToColor(int mood) {
        return switch (mood) {
            case 1 -> MOOD_1;
            case 2 -> MOOD_2;
            case 4 -> MOOD_4;
            case 5 -> MOOD_5;
            default -> MOOD_3;
        };
    }

    private final static Color MOOD_1 = new Color(255, 30, 0);
    private final static Color MOOD_2 = new Color(255, 135, 0);
    private final static Color MOOD_3 = new Color(245, 192, 0);
    private final static Color MOOD_4 = new Color(155, 243, 0);
    private final static Color MOOD_5 = new Color(42, 219, 0);
    public final static Color MOOD_MISSING = new Color(206, 206, 206);

}
