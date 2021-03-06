/* $Id$ */

package org.jsynthlib.core;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CrossBreedDialog extends JDialog {
    private final JLabel l1;
    private CrossBreeder crossBreeder;

    public CrossBreedDialog(JFrame parent) {
        super(parent, "Patch Cross-Breeder", false);
        crossBreeder = new CrossBreeder();
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JPanel p4 = new JPanel();
        p4.setLayout(new ColumnLayout());
        l1 = new JLabel("Patch Type: ");

        JPanel buttonPanel = new JPanel();
        JPanel buttonPanel2 = new JPanel();

        JButton gen = new JButton("Generate");
        gen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generatePressed();
            }
        });
        buttonPanel2.add(gen);

        JButton play = new JButton("Play");
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                play(crossBreeder.getCurrentPatch());
            }
        });
        buttonPanel2.add(play);

        JButton keep = new JButton("Keep");
        keep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    PatchBasket library = (PatchBasket) PatchEdit.getDesktop()
                            .getSelectedFrame();
                    IPatch q = crossBreeder.getCurrentPatch();
                    library.pastePatch(q);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Destination Library Must be Focused", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel2.add(keep);

        JButton ok = new JButton("Close");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OKPressed();
            }
        });
        buttonPanel.add(ok);
        getRootPane().setDefaultButton(ok);

        p4.add(l1);
        container.add(p4, BorderLayout.NORTH);
        container.add(buttonPanel2, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(container);
        // setSize(400,300);
        pack();
        Utility.centerDialog(this);
    }

    void OKPressed() {
        this.setVisible(false);
    }

    void generatePressed() {
        crossBreeder.generateNewPatch((PatchBasket) PatchEdit.getDesktop().getSelectedFrame());
        IPatch p = crossBreeder.getCurrentPatch();
        l1.setText("Patch Type: " + p.getDevice().getManufacturerName() + " "
                + p.getDevice().getModelName() + " " + p.getType());
        play(p);
    }

    private void play(IPatch p) {
        if (p.isSinglePatch()) {
            ((ISinglePatch) p).send();
            ((ISinglePatch) p).play();
        }
    }
}