package pixels;

import pixels.entry.Entries;
import pixels.ui.GuiMainView;
import yanwittmann.types.File;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.ParseException;

public class Main {

    private GuiMainView pixelsMainFrame;
    private Entries entries;

    public Main() {
        try {
            loadEntries();
            createGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGui() {
        pixelsMainFrame = new GuiMainView(entries);
        JFrame frame = new JFrame(Constants.FRAME_TITLE);

        frame.setContentPane(pixelsMainFrame.getMainPane());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setIconImage(new ImageIcon("res/img/icon.png").getImage());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadEntries() throws IOException, ParseException {
        entries = new Entries(String.join("", new File("res/pixels/pixels.json").readToArray()));
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}
