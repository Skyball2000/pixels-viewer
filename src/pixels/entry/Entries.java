package pixels.entry;

import org.json.JSONArray;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Entries {

    private List<Day> days = new ArrayList<>();

    public Entries(String input) throws ParseException {
        for (Object entry : new JSONArray(input)) days.add(new Day(entry.toString()));
        days = days.stream().sorted().collect(Collectors.toList());
    }

    public void setDayToButton(JButton button, JTextArea notes, JLabel tags, JPanel pixelsView, int year, int month, int day) {
        Day foundDay = getDay(year + "/" + makeDoubleDigit(month) + "/" + makeDoubleDigit(day));
        if (foundDay != null) {
            button.setText(day + "");
            button.setBackground(mapMoodToColor(foundDay.getMood()));
            button.setVisible(true);
            Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener);
            button.addActionListener(e -> {
                pixelsView.setBorder(new TitledBorder(foundDay.getDay()));
                pixelsView.setBackground(mapMoodToColor(foundDay.getMood()));
                tags.setText(foundDay.getTags());
                notes.setText(foundDay.getNotes());
            });
        }
    }

    private String makeDoubleDigit(int n) {
        return n < 10 ? "0" + n : String.valueOf(n);
    }

    public Day getDay(String searchDate) {
        return days.stream().filter(day -> day.isDay(searchDate)).findFirst().orElse(null);
    }

    private Color mapMoodToColor(int mood) {
        return switch (mood) {
            case 1 -> MOOD_1;
            case 2 -> MOOD_2;
            case 4 -> MOOD_4;
            case 5 -> MOOD_5;
            default -> MOOD_3;
        };
    }

    private final static Color MOOD_1 = new Color(255, 30, 0);
    private final static Color MOOD_2 = new Color(236, 156, 0);
    private final static Color MOOD_3 = new Color(245, 192, 0);
    private final static Color MOOD_4 = new Color(155, 243, 0);
    private final static Color MOOD_5 = new Color(42, 219, 0);

}
