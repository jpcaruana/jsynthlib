/**
 *  Copyright 1999-2002 Matthew Robinson and Pavel Vorobiev.
 *  All Rights Reserved.
 *
 *  ===================================================
 *  This program contains code from the book "Swing"
 *  2nd Edition by Matthew Robinson and Pavel Vorobiev
 *  http://www.spindoczine.com/sbe
 *  ===================================================
 *
 *  The above paragraph must be included in full, unmodified
 *  and completely intact in the beginning of any source code
 *  file that references, copies or uses (in any way, shape
 *  or form) code contained in this file.
 */

package synthdrivers.TCElectronicGMajor;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class TabListCellRenderer
    extends    JLabel
    implements ListCellRenderer {

    protected static Border m_noFocusBorder;
    protected FontMetrics m_fm = null;
    protected Insets m_insets = new Insets(0, 0, 0, 0);

    protected int m_defaultTab = 50;
    protected int[] m_tabs = null;

    public TabListCellRenderer() {
        super();
        m_noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        setOpaque(true);
        setBorder(m_noFocusBorder);
    }

    public void setDefaultTab(int defaultTab) {
        m_defaultTab = defaultTab;
    }

    public int getDefaultTab() {
        return m_defaultTab;
    }

    public void setTabs(int[] tabs) {
        m_tabs = tabs;
    }

    public int[] getTabs() {
        return m_tabs;
    }

    public int getTab(int index) {
        if (m_tabs == null)
            return m_defaultTab*index;

        int len = m_tabs.length;
        if (index>=0 && index<len)
            return m_tabs[index];

        return m_tabs[len-1] + m_defaultTab*(index-len+1);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color colorRetainer = g.getColor();
        m_fm = g.getFontMetrics();

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());

        g.setColor(getForeground());
        g.setFont(getFont());
        m_insets = getInsets();
        int x = m_insets.left;
        int y = m_insets.top + m_fm.getAscent();

        StringTokenizer st = new StringTokenizer(getText(), "\t");
        while (st.hasMoreTokens()) {
            String sNext = st.nextToken();
            g.drawString(sNext, x, y);
            x += m_fm.stringWidth(sNext);

            if (!st.hasMoreTokens())
                break;
            int index = 0;
            while (x >= getTab(index))
                index++;
            x = getTab(index);
        }

        g.setColor(colorRetainer);
    }

    public Component getListCellRendererComponent(JList list,
        Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());

        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());

        setFont(list.getFont());
        setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : m_noFocusBorder);

        return this;
    }

}
