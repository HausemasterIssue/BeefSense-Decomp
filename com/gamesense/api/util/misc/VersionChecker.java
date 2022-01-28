



package com.gamesense.api.util.misc;

import java.util.*;
import java.io.*;
import javax.swing.event.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

public class VersionChecker
{
    public VersionChecker() {
        this.checkVersion("v0.0.1");
    }
    
    private void checkVersion(final String version) {
        boolean isLatest = true;
        String newVersion = "null";
        if (version.startsWith("d")) {
            return;
        }
        try {
            final URL url = new URL("https://raw.githubusercontent.com/IUDevman/gamesense-assets/main/files/versioncontrol.txt");
            final Scanner scanner = new Scanner(url.openStream());
            final String grabbedVersion = scanner.next();
            if (!version.equalsIgnoreCase(grabbedVersion)) {
                isLatest = false;
                newVersion = grabbedVersion;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            isLatest = true;
        }
        if (!isLatest) {
            this.generatePopUp(newVersion);
        }
    }
    
    private void generatePopUp(final String newVersion) {
        final JLabel label = new JLabel();
        final Font font = label.getFont();
        final String style = "font-family:" + font.getFamily() + ";font-weight:" + (font.isBold() ? "bold" : "normal") + ";font-size:" + font.getSize() + "pt;";
        final JEditorPane editorPane = new JEditorPane("text/html", "<html><body style=\"" + style + "\">Version outdated! Download the latest (" + newVersion + ") <a href=\"https://github.com/IUDevman/gamesense-client/releases\">HERE</a>!</body></html>");
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(final HyperlinkEvent event) {
                if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        Desktop.getDesktop().browse(event.getURL().toURI());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (URISyntaxException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        editorPane.setEditable(false);
        editorPane.setBackground(label.getBackground());
        JOptionPane.showMessageDialog(null, editorPane, "KiefSense v0.0.1", 2);
    }
}
