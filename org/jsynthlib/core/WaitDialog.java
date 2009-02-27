/*
 * Copyright 2004 Hiroo Hayashi
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * Dialog window to show a waiting state.
 * 
 * @author Hiroo Hayashi
 */
class WaitDialog extends JDialog {
    private JProgressBar progressBar;
    private JLabel label;
    private Timer timer;
    private final static int ONE_SECOND = 1000;

    WaitDialog(JFrame parent) {
        super(parent, "Working...", false);

        // create a labe widget
        label = new JLabel();
        // create a progress bar
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        //progressBar.setStringPainted(true); //get space for the string
        //progressBar.setString(""); //but don't paint it

        // layout them
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.add(label);
        contentPane.add(progressBar);
        contentPane.setOpaque(true);
        setContentPane(contentPane);

        //Create a timer.
        timer = new Timer(ONE_SECOND, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(true);
            }
        });
        timer.setRepeats(false); // fires only once
    }

    /**
     * Show the wait dialog window after 1 second.
     * 
     * @param s
     *            Message string in the dialog window.
     */
    void showDialog(String s) {
        label.setText(s);
        pack();
        Utility.centerDialog(this);
        timer.start();
    }

    /**
     * Hide the wait dialog.
     */
    void hideDialog() {
        timer.stop(); // may be stopped already
        setVisible(false);
    }
}
