/*
 * Copyright 2005 Ton Holsink
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

package synthdrivers.YamahaUB99;

// import core.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

public class ClickLabel extends JLabel {
    private Cursor oldCursor;
    private Cursor cursorUp;
    private Cursor cursorDown;
    private boolean isactivated;
    private int ypos;

    private int updown;
    private ChangeEvent changeEvent = null;
    private EventListenerList listenerList = new EventListenerList();
    private Cursor curup;

    public ClickLabel() {
        super();
        createWidget();
    }

    public ClickLabel(String text) {
        super(text);
        createWidget();
    }

    public boolean isFocusable() {
        return true;
    }

    protected void createWidget() {

        updown = 0;
        oldCursor = getCursor();
        cursorUp = getCursorUp();
        cursorDown = getCursorDown();
        isactivated = false;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                processMousePressed(me);
            }

            public void mouseClicked(MouseEvent me) {
                processMouseClicked(me);
            }

            public void mouseEntered(MouseEvent me) {
                processMouseEntered(me);
            }

            public void mouseExited(MouseEvent me) {
                processMouseExited(me);
            }

            public void mouseReleased(MouseEvent me) {
                processMouseReleased(me);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent me) {
                processMouseMoved(me);
            }
        });

        addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent fe) {
                processFocusLost(fe);
            }
        });
    }

    protected void processMousePressed(MouseEvent me) {
//             requestFocus();
        requestFocusInWindow();
//         setText("Pressed");
        if (isactivated) {
            ypos = me.getY();
        }
    }

    protected void processMouseClicked(MouseEvent me) {
        if (ypos > (getHeight() / 2)) {
            setCursor(cursorDown);
        } else {
            setCursor(cursorUp);
        }
        if (isactivated) {
            if (ypos > (getHeight() / 2)) {
                updown = -1;
            } else {
                updown = 1;
            }
            fireChangeEvent();
        }
        isactivated = true;
    }

    protected void processMouseEntered(MouseEvent me) {
//         setText("Entered");
    }

    protected void processMouseExited(MouseEvent me) {
//         setText("Exited");
//         setCursor(oldCursor);
//         isactivated = false;
    }

    protected void processMouseReleased(MouseEvent me) {
//         setText("Released");
    }

    protected void processMouseMoved(MouseEvent me) {
        if (isactivated) {
            if (me.getY() > (getHeight() / 2)) {
                setCursor(cursorDown);
            } else {
                setCursor(cursorUp);
            }
        }
    }

    protected void processFocusLost(FocusEvent fe) {
        setCursor(oldCursor);
        isactivated = false;
        updown = 0;
    }

    public int getUpDown() {
        return updown;
    }

    public void addChangeListener(ChangeListener cl) {
        listenerList.add(ChangeListener.class, cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        listenerList.remove(ChangeListener.class, cl);
    }

    protected void fireChangeEvent() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }

    protected Cursor getCursorUp() {
        int curWidth = 32;
        int curHeight = 32;

        int pix[] = new int[curWidth * curHeight];
        int curCol;
        int radius;

        for(int j = 2; j <= 4; j++) {
            if (j == 2) {
                curCol = Color.black.getRGB();
            } else {
                curCol = Color.red.getRGB();
            }
            radius = 15;
            for(int i = j; i <= 7; i++) {
                pix[(i+7) * 32 + radius] = curCol;
                pix[((i+7) + 1) * 32 - radius] = curCol;
                radius--;
            }
        }
        Image img = createImage(new MemoryImageSource(curWidth, curHeight, pix, 0, curWidth));
        return Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(15, 15), "cursor up");
    }

    protected Cursor getCursorDown() {
        int curWidth = 32;
        int curHeight = 32;

        int pix[] = new int[curWidth * curHeight];
        int curCol;
        int radius;

        for(int j = 2; j <= 4; j++) {
            if (j == 2) {
                curCol = Color.black.getRGB();
            } else {
                curCol = Color.red.getRGB();
            }
            radius = 15;
            for(int i = (32 - j); i >= (32 - 7); i--) {
                pix[(i-7) * 32 + radius] = curCol;
                pix[((i-7) + 1) * 32 - radius] = curCol;
                radius--;
            }
        }
        Image img = createImage(new MemoryImageSource(curWidth, curHeight, pix, 0, curWidth));
        return Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(15, 16), "cursor down");
    }

}
