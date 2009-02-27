package core;

import javax.swing.*;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.*;
import java.awt.geom.AffineTransform;

// TODO: Unselect any cells in the table before printing... and then select them again afterward.

/**
 * Created by IntelliJ IDEA.
 * User: Joe Emenaker
 * Date: Oct 22, 2005
 * Time: 7:30:33 PM
 */
public class BankPrinter implements Printable {

    JTable table;
    double widthScale = 0.75; // How much of the printable page width to use
    boolean horiz_center = true; // Center it horizonatally on the page?
    boolean includePageNumbers = true; // Include page numbers on the bottom of the page?

    public BankPrinter(JTable tableView) {
        this.table = tableView;
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(this);
        pj.printDialog();
        try {
            pj.print();
        } catch (Exception PrintException) {
        }
    }

    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.black);
        int fontHeight = g2.getFontMetrics().getHeight();
        int fontDesent = g2.getFontMetrics().getDescent();

        //leave room for page number
        double pageHeight = pageFormat.getImageableHeight() - (includePageNumbers ? fontHeight : 0);
        double pageWidth = pageFormat.getImageableWidth();
        double pageLeft = pageFormat.getImageableX();
        double pageTop = pageFormat.getImageableY();
        double tableWidth = (double) table.getColumnModel().getTotalColumnWidth();

        // Find out if we need to shrink the table to fit the width of the page
        double scale = 1;
        if (tableWidth >= pageWidth) {
            scale = pageWidth / tableWidth * widthScale;
        }

        double tableHeaderHeightOnPage = table.getTableHeader().getHeight() * scale;

        // How wide will the table be on the page after scaling?
        double tableWidthOnPage = tableWidth * scale;

        // How high will a single row be on the page after scaling?
        double oneRowHeight = (table.getRowHeight() /* + table.getRowMargin()*/) * scale;
        // How many complete rows will fit on the page?
        int numRowsOnAPage = (int) ((pageHeight - tableHeaderHeightOnPage) / oneRowHeight);
        // How many pages is the table going to use?
        int totalNumPages = (int) Math.ceil(((double) table.getRowCount()) / numRowsOnAPage);
        // If we're being asked to print a page past that, report that there are no more pages to print.
        if (pageIndex >= totalNumPages) {
            return NO_SUCH_PAGE;
        }
        // How many rows are on *this* page
        int numRowsOnThisPage = table.getRowCount() - numRowsOnAPage * (totalNumPages - 1);

        // How much vertical space is used on the page, after scaling?
        double tableBodyHeightOnPage = oneRowHeight * numRowsOnThisPage;
        double tableTotalHeightOnPage = tableBodyHeightOnPage + tableHeaderHeightOnPage;

        g2.translate(pageLeft, pageTop); // Move to the top-left of the printable area
        g2.translate(1,1); // Make room for the top and left table border

        // If we want a page number, move to the bottom of the page and print it now
        if (includePageNumbers) {
            String pageNumString = "Page: " + (pageIndex + 1);
            int widthOfString = g2.getFontMetrics().stringWidth(pageNumString);
            g2.drawString(pageNumString, ((int) pageWidth - widthOfString) / 2,
                    (int) (pageHeight + fontHeight - fontDesent));//bottom center
        }
        g2.translate((pageWidth - tableWidthOnPage) / 2,0); // Center the table on the page
        AffineTransform unscaledTransform = g2.getTransform();

        //If this piece of the table is smaller than the size available,
        //clip to the appropriate bounds.
/*
        if (pageIndex + 1 == totalNumPages) {
            int lastRowPrinted = numRowsOnAPage * pageIndex;
            int numRowsLeft = table.getRowCount() - lastRowPrinted;
            g2.setClip(0, (int) (tableBodyHeightOnPage * pageIndex),
                    (int) Math.ceil(tableWidthOnPage),
                    (int) Math.ceil(oneRowHeight * numRowsLeft));
        } else {
            //else clip to the entire area available.
            g2.setClip(0, (int) (tableBodyHeightOnPage * pageIndex),
                    (int) Math.ceil(tableWidthOnPage),
                    (int) Math.ceil(tableBodyHeightOnPage));
        }

*/
        g2.setTransform(unscaledTransform);
        g2.scale(scale, scale);
        AffineTransform scaledTransform = g2.getTransform();
        table.getTableHeader().paint(g2);//paint header at top
        g2.translate(0,tableHeaderHeightOnPage / scale );
        table.paint(g2);
        g2.setTransform(unscaledTransform);
        g2.drawRect(0,0,(int) tableWidthOnPage - 1, (int) tableTotalHeightOnPage);
/*
    For debugging. This draws lines on the page where the table should go...
        // Draw lines over the row separators....
        for(int i=0; i<=numRowsOnThisPage; i++) {
            g2.drawLine(0,(int) ( oneRowHeight * (double) i), (int) tableWidthOnPage, (int) ( oneRowHeight * (double) i));
        }
*/
        g2.setTransform(unscaledTransform);
        // If this isn't the first page, move the (0,0) point up off the top of the page
        // so that the rows we want will be where the page is.
        g2.translate(0f, pageIndex * tableBodyHeightOnPage);
        g2.translate(0f, -tableHeaderHeightOnPage);
        g2.setClip(0, 0, (int) Math.ceil(tableWidthOnPage),
                (int) Math.ceil(tableHeaderHeightOnPage));

        return Printable.PAGE_EXISTS;
    }
}

