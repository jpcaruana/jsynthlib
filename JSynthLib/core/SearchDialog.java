/*
* $Id$
*/
package core;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class SearchDialog extends JDialog {
 JRadioButton button2;
 JRadioButton button3;
   public SearchDialog(JFrame Parent) {

        super(Parent,"Search Library",false);

	JPanel container= new JPanel();
        container.setLayout (new BorderLayout());
        JPanel searchFor = new JPanel();
        JLabel label = new JLabel("Search For");
        final JTextField textField = new JTextField(30);
        searchFor.setLayout(new FlowLayout());
        searchFor.add(label);
        searchFor.add(textField);
	try {
        searchFor.setLayout(new FlowLayout());
        searchFor.add(label);
        searchFor.add(textField);
        container.add(searchFor, BorderLayout.NORTH);
        final ButtonGroup group=new ButtonGroup();
	JRadioButton button1 = new JRadioButton("Patch Name");
        button2 = new JRadioButton("Field 1");
        button3 = new JRadioButton("Field 2");
        JRadioButton button4 = new JRadioButton("Comment");
        JRadioButton button5 = new JRadioButton("All Fields");
        button1.setActionCommand("P");
        button2.setActionCommand("1");
        button3.setActionCommand("2");
        button4.setActionCommand("C");
        button5.setActionCommand("A");
         group.add(button1);
         group.add(button2);
         group.add(button3);
         group.add(button4);
         group.add(button5);

         if (JSLDesktop.getSelectedFrame() instanceof SceneFrame)
         {
             button2.setEnabled(false);
             button3.setEnabled(false);
         }


         button1.setSelected(true);
	 JPanel radioPanel=new JPanel();
         radioPanel.setLayout(new FlowLayout());
	 radioPanel.add(button1);
         radioPanel.add(button2);
         radioPanel.add(button3);
         radioPanel.add(button4);
         radioPanel.add(button5);
         container.add(radioPanel,BorderLayout.CENTER);
	 JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout ( new FlowLayout() );
        JButton findFirst = new JButton(" Find First ");
        JButton findNext = new JButton(" Find Next ");

        findFirst.addActionListener(new ActionListener() {
	           	       public void actionPerformed(ActionEvent e){
				   String command = group.getSelection().getActionCommand();
                                   String text = textField.getText();
                                   findString(text, command,true);
                        	}});

        findNext.addActionListener(new ActionListener() {
	           	       public void actionPerformed(ActionEvent e){
                                   String text = textField.getText();
				   String command = group.getSelection().getActionCommand();
                                   findString(text,command,false);

				}});

        buttonPanel.add( findFirst );
        buttonPanel.add( findNext );
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
                                   setVisible(false);
			       }});
        buttonPanel.add( cancel );

        getRootPane().setDefaultButton(findFirst);

	container.add(buttonPanel, BorderLayout.SOUTH);
	getContentPane().add(container);
        pack();
	centerDialog();
	   }catch(Exception e) {ErrorMsg.reportStatus(e);}
  }

public void show()
{
    if (JSLDesktop.getSelectedFrame() instanceof SceneFrame)
         {
             button2.setEnabled(false);
             button3.setEnabled(false);
         }
    else
         {
             button2.setEnabled(true);
             button3.setEnabled(true);
         }
    super.show();
}

    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
	Dimension size = this.getSize();
	screenSize.height = screenSize.height/2;
	screenSize.width = screenSize.width/2;
	size.height = size.height/2;
	size.width = size.width/2;
	int y = screenSize.height - size.height;
	int x = screenSize.width - size.width;
	this.setLocation(x,y);
    }

void findString(String text, String command, boolean restart)
{
   if ((JSLDesktop.getSelectedFrame() == null)
       || (!(JSLDesktop.getSelectedFrame() instanceof AbstractLibraryFrame)))
      {ErrorMsg.reportError("Error","Library to search in must be focused.");
       return;
      }

   AbstractLibraryFrame lf= ((AbstractLibraryFrame)JSLDesktop.getSelectedFrame());
   if (lf.getAbstractPatchListModel().getRowCount()==0) return;
   int searchFrom;
   if (restart || lf.getTable().getSelectedRow()==-1) searchFrom=0;
     else searchFrom=lf.getTable().getSelectedRow()+1;

   Patch p;
   int field=0;
   if (command.equals("P")) field=0;
   if (command.equals("1")) field=1;
   if (command.equals("2")) field=2;
   if (command.equals("C")) field=3;
   if (command.equals("A")) field=4;
   text=text.toLowerCase();
   String s;
   int i;
   boolean match=false;
   for (i=searchFrom;i<lf.getAbstractPatchListModel().getRowCount();i++)
     {
       p=(Patch)lf.getAbstractPatchListModel().getPatchAt(i);

       match=false;
        if (field==0 || field==4)
         {
          s=p.getDriver().getPatchName(p).toLowerCase();
          match=(s.indexOf(text)!=-1);
          if (match) break;
         }
        if ((field==1 || field==4) && (lf instanceof LibraryFrame))
         {
          s = p.getDate().toLowerCase();
          match=(s.indexOf(text)!=-1);
          if (match) break;
         }
        if ((field==2 || field==4) && (lf instanceof LibraryFrame))
         {
          s = p.getAuthor().toLowerCase();
          match=(s.indexOf(text)!=-1);
          if (match) break;
         }

         if (field==3 || field==4)
         {
          s=lf.getAbstractPatchListModel().getCommentAt(i).toString().toLowerCase();
          //s=p.comment.toString().toLowerCase();
          match=(s.indexOf(text)!=-1);
          if (match) break;
         }

     }

   if (!match) {ErrorMsg.reportError("Search Complete","Not Found.");return;}
   lf.getTable().changeSelection(i,0,false,false);



 }

}


