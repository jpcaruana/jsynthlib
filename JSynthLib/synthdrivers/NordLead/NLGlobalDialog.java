// written by Kenneth L. Martinez

package synthdrivers.NordLead;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NLGlobalDialog extends JDialog {
  String channels[];
  JComboBox channelList;

  public NLGlobalDialog(JFrame frame, String chan[], String title, int idx) {
    super(frame, title, true);
    channels = chan;
    JPanel container= new JPanel();
    container.setLayout (new BoxLayout(container, BoxLayout.Y_AXIS));

    channelList = new JComboBox(channels);
    channelList.setMaximumSize(new Dimension(150, 25));
    channelList.setSelectedIndex(idx);
    channelList.setAlignmentX(CENTER_ALIGNMENT);
    container.add(Box.createVerticalGlue());
    container.add(channelList);
    container.add(Box.createVerticalGlue());

    JButton ok = new JButton("OK");
    ok.addActionListener(new ActionListener() {
                           public void actionPerformed(ActionEvent e) {
                               OKPressed();
                           }});
    ok.setAlignmentX(CENTER_ALIGNMENT);
    container.add( ok );
    container.add(Box.createVerticalGlue());
    getRootPane().setDefaultButton(ok);

    getContentPane().add(container);
    setSize(300,150);

    centerDialog();
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

  public int getChannel() {
    return channelList.getSelectedIndex();
  }
}
