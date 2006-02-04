package synthdrivers.Generic;

import core.Patch;
import core.PatchEditorFrame;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: jemenake
 * Date: May 10, 2005
 * Time: 1:35:53 AM
 */
public class SingleTextAreaFrame extends PatchEditorFrame {

    Font f = new Font("Courier", Font.PLAIN, 12);
    int cols = 80;
    private TextArea textArea;

    public SingleTextAreaFrame(String title, int cols) {
        this(title);
        this.cols = cols;
    }

    SingleTextAreaFrame(String title) {
        super(title,new Patch(new byte[] {(byte) 0xF0, (byte) 0xF7}));

        textArea = new TextArea();
        textArea.setFont(f);
        textArea.setEditable(false);

        // Get the width of a single character (we'd better have a fixed-width font) and use it
        // to calculate the needed pixel width of the textArea. Then, resize...
        int charwidth = textArea.getFontMetrics(f).stringWidth(" ");
        int pixelwidth = cols * charwidth;
        textArea.setPreferredSize(new Dimension(pixelwidth + 50,200));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(textArea,java.awt.BorderLayout.CENTER);
//        scrollPane.setLayout(new BorderLayout());
//        scrollPane.add(textArea,java.awt.BorderLayout.CENTER);
    }

    public void append(String str) {
        textArea.append(str);
    }
}
