package pixels;

import pixels.entry.Entries;
import pixels.ui.GuiMainView;
import yanwittmann.types.File;
import yanwittmann.utils.FileUtils;
import yanwittmann.utils.Popup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {

    private GuiMainView pixelsMainFrameGui;
    private JFrame pixelsMainFrame;
    private Entries entries;
    private static Main self;

    public Main() {
        self = this;
        loadEntries();
        try {
            createGui();
        } catch (Exception e) {
            e.printStackTrace();
            Popup.error(Constants.FRAME_TITLE_GENERAL, "Something went wrong while preparing the pixels.\n" +
                    "Please make sure that your pixels are in the unchanged default JSON export format.\n" +
                    "The exact error message: " + e.getMessage());
            System.exit(2);
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

    private void loadEntries() {
        try {
            FileDialog picker = new FileDialog((Frame)null, Constants.FRAME_TITLE_GENERAL + " - Pick your pixels.json file!");
            picker.setDirectory(new File("").getAbsolutePath());
            picker.setIconImage(new ImageIcon("res/img/icon.png").getImage());
            picker.setVisible(true);
            java.io.File[] files = picker.getFiles();
            if (files.length > 0) {
                File file = new File(files[0].getAbsolutePath());
                if (file.exists())
                    entries = new Entries(String.join("", file.readToArray()));
            } else System.exit(3);
        } catch (Exception e) {
            e.printStackTrace();
            Popup.error(Constants.FRAME_TITLE_GENERAL, "Unable to read pixels from file.\n" +
                    "Please make sure that your pixels are in the unchanged default JSON export format.\n" +
                    "The exact error message: " + e.getMessage());
            System.exit(1);
        }
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
