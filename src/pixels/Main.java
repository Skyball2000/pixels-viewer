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

    private GuiMainView pixelsMainFrameGui;
    private JFrame pixelsMainFrame;
    private Entries entries;
    private static Main self;

    public Main() {
        self = this;
        try {
            loadEntries();
            createGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGui() {
        pixelsMainFrameGui = new GuiMainView(entries);
        pixelsMainFrame = new JFrame(Constants.FRAME_TITLE_GENERAL);

        pixelsMainFrame.setContentPane(pixelsMainFrameGui.getMainPane());
        pixelsMainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pixelsMainFrame.setIconImage(new ImageIcon("res/img/icon.png").getImage());
        pixelsMainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pixelsMainFrame.dispose();
                System.exit(0);
            }
        });
        pixelsMainFrame.pack();
        pixelsMainFrame.setLocationRelativeTo(null);
        pixelsMainFrame.setVisible(true);
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
