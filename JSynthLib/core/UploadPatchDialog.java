

package core;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import java.beans.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
public class UploadPatchDialog extends JDialog {
  final JTextField t1;
  final JTextField t2;
  final JTextArea  t3;
  final JTextField t4;
  final JTextField t5;
  final JPasswordField t6;


  public UploadPatchDialog(JFrame Parent) {
      super(Parent,"Upload Patch to Repository",false);


    String patchType;
    PatchBasket library=(PatchBasket)JSLDesktop.getSelectedFrame();
    Patch q	= library.GetSelectedPatch(); 
    
    JPanel container= new JPanel();
    container.setLayout (new BorderLayout());
    JPanel p4 = new JPanel();   
    p4.setLayout (new ColumnLayout());
    JLabel l1 = new JLabel("Patch Type: ");
    t1 = new JTextField(20);
    JLabel l2 = new JLabel("Patch Name: ");
    t2 = new JTextField(20);
    t2.setText(q.getDriver().getPatchName(q));
    t1.setEditable(false);

    patchType= q.getDriver().getManufacturerName();
    patchType+=" ";
    patchType+=q.getDriver().getModelName();
    t1.setText(patchType);
	
    JLabel l4 = new JLabel("Repository:");
     t4=new JTextField(20);
    t4.setText(PatchEdit.appConfig.getRepositoryURL());
    JLabel l5 = new JLabel("Contributor: ");
     t5=new JTextField(20);
    t5.setText(PatchEdit.appConfig.getRepositoryUser());
    JLabel l6 = new JLabel("Contributor Password: ");
     t6=new JPasswordField(20);
    t6.setText(PatchEdit.appConfig.getRepositoryPass());

    JLabel l3 = new JLabel("Description: ");
     t3 = new JTextArea(4,50);
     t3.setText(q.getComment());
    
    
    JPanel buttonPanel = new JPanel();
    JButton upload = new JButton("Upload");
    upload.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    PatchBasket library=(PatchBasket)JSLDesktop.getSelectedFrame();
		    Patch q	= library.GetSelectedPatch();
		    uploadPatch(q);
		}catch (Exception ex){JOptionPane.showMessageDialog(null, "Patch Must be Focused","Error", JOptionPane.ERROR_MESSAGE);}
	    }});
    buttonPanel.add(upload);
    JButton play = new JButton("Play");
    play.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		play();
	    }});
    buttonPanel.add(play);
    
    JButton ok = new JButton("Cancel");
    ok.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		OKPressed();
	    }});
    buttonPanel.add( ok );
    getRootPane().setDefaultButton(ok);
    p4.add(l4);p4.add(t4);
    p4.add(l5);p4.add(t5);
    p4.add(l6);p4.add(t6);
    p4.add(l1);p4.add(t1);
    p4.add(l2);p4.add(t2);
    p4.add(l3);p4.add(t3);
  
    
    container.add(p4,BorderLayout.NORTH);
    container.add(buttonPanel,BorderLayout.SOUTH);
    getContentPane().add(container);
    // setSize(400,300);
    pack();
    centerDialog();
    
    
}
   public void show()
   {
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

void OKPressed() {
 	this.setVisible(false);
  }
void uploadPatch(Patch p)
{
    String patchType=t1.getText();
    String patchName=t2.getText();
    String desc=t3.getText();
    String repository=t4.getText();
    String userName=t5.getText();
    String passwd=t6.getText();
    byte [] sysex = p.sysex;
    if (patchName.length()<4)
	ErrorMsg.reportError("Error","Patch Name must be at least 4 characters.");
    else if (desc.length()<20)
	ErrorMsg.reportError("Error","Description must be at least 20 characters.");
    else if (userName.length()<1)
	ErrorMsg.reportError("Error","Please supply a Contributer's User Name");
    else if (passwd.length()<1)
	ErrorMsg.reportError("Error","Please supply the Contributer's Password for the Repository");
    else if (repository.length()<5)
	ErrorMsg.reportError("Error","Please supply the Repository URL Address");
    else
	{
	    String mime = new String();
	    mime = mimeBoundary();
	    mime += makeMime("name",patchName);
	    mime += mimeBoundary();
	    mime += makeMime("synthName",patchType);
	    mime += mimeBoundary();
	    try{
	    String filestr=new String (sysex,"ISO-8859-1");	    
	    mime += makeMime("file",filestr);
	    } catch (Exception e) {System.out.println("UploadPatchDialog encoding failed.");}
	    mime += mimeBoundary();
	    mime += makeMime("username",userName);
	    mime += mimeBoundary();
	    mime += makeMime("pass",passwd);
	    mime += mimeBoundary();
	    mime += makeMime("comment",desc);
	    mime += mimeBoundary();
	    mime += makeMime("send","Upload");
	    mime += mimeBoundary();
	    mime += "\r\n\r\n";
	    // System.out.println(mime);
	    if (postData(repository,mime)==1)
		setVisible(false);
	}
	

}

    String makeMime(String name1, String body1)
    {
	String s = new String();
	s = "Content-Disposition: form-data; name=\"" + name1 + "\"\r\n\r\n";
	s +=body1;
	s+="\r\n";
	return s;
	
    }

    int postData(String repository,String mime)
    {
	try{
	    URL url = new URL(repository+"/cgi-bin/upload.cgi");
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setDoInput(true);
	    conn.setDoOutput(true);
	    conn.setUseCaches(false);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Connection", "Close");
	    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=-----------------------------18042893838469308861681692777");
	    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
	    os.writeBytes(mime);
	    os.flush();
	    os.close();
	    DataInputStream in = new DataInputStream(conn.getInputStream());
	    String str;
	    while (( str = in.readLine()) != null)
		{
		    //     System.out.println("Server: "+str);
		    
		    if (str.indexOf("<h2")>-1)
		    {
			int st = str.indexOf(">");
			int en = str.indexOf("</h");
			if (st<0 || en<0)
			    ErrorMsg.reportError("Error","Can not Understand Server Response: "+str);
			else 
			    {
				if (str.indexOf("Success")>-1)
				    {
					ErrorMsg.reportError("Info","Patch Uploaded Successfully");
					return 1;
				    }
				else
				    ErrorMsg.reportError("Error",str.substring(st+1,en));
			    }						 
		    }
		      
			
		}
   in.close();    
	    
	} catch (Exception e){ ErrorMsg.reportStatus(e);}
	
		    return 0;
    }

    String mimeBoundary()
    {
	return "-----------------------------18042893838469308861681692777\r\n";
    }

  void play()
 {
    try{
	    PatchBasket library=(PatchBasket)JSLDesktop.getSelectedFrame();
	     Patch p = library.GetSelectedPatch();
      		       
		       if (p==null) return;
		       p.getDriver().sendPatch(p);
		       p.getDriver().playPatch(p);
   }catch (Exception ex){JOptionPane.showMessageDialog(null, "Patch Must be Focused","Error", JOptionPane.ERROR_MESSAGE);}
	
 }

}

