package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
public class EnvelopeWidget extends SysexWidget
{
    int base;
    JLabel []valueLabels;
    JTextField [] values;
    EnvelopeNode []nodes;
    int numValues;
    public EnvelopeWidget (String l,Patch p,EnvelopeNode[] n)
    {label=l;patch=p;nodes=n;setup ();}
    public void setSliderNum (int num)
    {
        sliderNum=num;
        setToolTipText ("Bank "+(((num-1)/16)+"  Sliders "+(((num-1)%16)+1))+" to "+(((num-1)%16)+numFaders));
        for (int j=0; j<numValues;j++)  values[j].setToolTipText ("Bank "+(((num-1)/16)+"  Slider "+(((num-1+j)%16)+1)));
        for (int j=0; j<numValues;j++)  valueLabels[j].setToolTipText ("Bank "+(((num-1)/16)+"  Slider "+(((num-1+j)%16)+1)));
        
    }
    public void setup ()
    {
        // super.setup();
        
        setLayout (new BorderLayout ());
        valueLabels=new JLabel [8];
        values=new JTextField[8];
        int j=0;
        for (int i=0; i<nodes.length;i++)
        {
            if (nodes[i].minX!=nodes[i].maxX)
            { valueLabels[j]=new JLabel (nodes[i].nameX);
              values[j]=new JTextField (new Integer (nodes[i].ofsX.get ()).toString (),4);
              values[j].setEditable (false);
              j++;
            }
            if (nodes[i].minY!=nodes[i].maxY)
            { valueLabels[j]=new JLabel (nodes[i].nameY);
              values[j]=new JTextField (new Integer (nodes[i].ofsY.get ()).toString (),4);
              values[j].setEditable (false);
              j++;
            }
        }
        numValues=j;
        numFaders=numValues;
        JPanel valuePane=new JPanel ();
        valuePane.setLayout (new GridBagLayout ());
        GridBagConstraints gbc=new GridBagConstraints ();
        gbc.fill=gbc.BOTH;
        gbc.anchor=gbc.EAST;
        for (j=0;j<numValues;j++)
        {
            gbc.gridx=0;gbc.gridy=j;gbc.gridwidth=1;gbc.gridheight=1;
            valuePane.add (valueLabels[j],gbc);
            gbc.gridx=1;gbc.gridy=j;gbc.gridwidth=1;gbc.gridheight=1;
            valuePane.add (values[j],gbc);
        }
        add (valuePane,BorderLayout.EAST);
        jlabel = new JLabel (label);
        add (jlabel,BorderLayout.NORTH);
        add (new EnvelopeCanvas (nodes,patch,values),BorderLayout.CENTER);
    }
    
    public void setValue (int fader, int value)
    {
        fader-=sliderNum;
        int j=0;
        for (int i=0; i<nodes.length;i++)
        {
            if (nodes[i].minX!=nodes[i].maxX)
            {
                if (fader==j)
                {
                    value=(int)(nodes[i].minX+((float)(value)/127*(nodes[i].maxX-nodes[i].minX)));
                    nodes[i].ofsX.set (value);
                    values[j].setText (new Integer (value).toString ());
                    try
                    {
                        nodes[i].senderX.channel=(byte)((Device)PatchEdit.deviceList.get (patch.deviceNum)).getChannel();
                        PatchEdit.MidiOut.writeLongMessage (
                        ((Device)PatchEdit.deviceList.get (patch.deviceNum)).getPort(),nodes[i].senderX.generate (nodes[i].ofsX.get ()));
                    }
                    catch (Exception e1)
                    {
                        ErrorMsg.reportStatus (e1);
                    }
                }
                j++;
            }
            if (nodes[i].minY!=nodes[i].maxY)
            {
                if (fader==j)
                {
                    value=(int)(nodes[i].minY+((float)(value)/127*(nodes[i].maxY-nodes[i].minY)));
                    nodes[i].ofsY.set (value);
                    values[j].setText (new Integer (value).toString ());
                    try
                    {
                        nodes[i].senderY.channel=(byte)((Device)PatchEdit.deviceList.get (patch.deviceNum)).getChannel();
                        PatchEdit.MidiOut.writeLongMessage (
                        ((Device)PatchEdit.deviceList.get (patch.deviceNum)).getPort(),nodes[i].senderY.generate (nodes[i].ofsY.get ()));
                    }
                    catch (Exception e1)
                    {
                        ErrorMsg.reportStatus (e1);
                    }
                }
                j++;
            }
            
        }
        
    }
    
    
    class EnvelopeCanvas extends JPanel
    {
        EnvelopeNode nodes[];
        int nodeX[];
        int nodeY[];
        JTextField []values;
        Patch p;
        public EnvelopeCanvas (EnvelopeNode [] e,Patch pa,JTextField[] f)
        {super(); values=f;nodes=e;p=pa;    nodeX=new int[nodes.length];
         setMinimumSize (new Dimension (300,50));
         MyListener myListener = new MyListener ();
         addMouseListener (myListener);
         addMouseMotionListener (myListener);
         
         nodeY=new int[nodes.length];
        }
        protected void paintComponent (Graphics g)
        {
            if (g!=null) super.paintComponent (g);
            Insets insets = getInsets ();
            int currentWidth = getWidth () - insets.left - insets.right-10;
            int currentHeight = getHeight () - insets.top - insets.bottom-5;
            int currX=insets.left+5;int lastX=0;int lastY=0;
            int maxX=0;int maxY=0;float scaleX; float scaleY;
            for (int i=0;i<nodes.length;i++)
            {
                maxX+=nodes[i].maxX;
                if ((nodes[i].maxY+nodes[i].baseY>maxY) && (nodes[i].maxY!=5000)) maxY=nodes[i].maxY+nodes[i].baseY;
            };
            
            scaleX=(float)currentWidth/(float)maxX;
            scaleY=(float)(currentHeight-5)/(float)maxY;
            
            for (int i=0;i<nodes.length;i++)
            {
                int x; int y;
                x=getX (i);
                y=getY (i);
                
                if (g!=null)g.fillRect ((int)((currX+x)*scaleX)-3,currentHeight-((int)(y*scaleY))-3,6,6);
                nodeX[i]= (int)((currX+x)*scaleX);
                nodeY[i]=currentHeight-((int)(y*scaleY));
                if ((i>0) &&(g!=null)) g.drawLine ((int)((currX+x)*scaleX),currentHeight-((int)(y*scaleY)),
                (int)((lastX)*scaleX),currentHeight-((int)(lastY*scaleY)));
                lastX=x+currX;lastY=y;
                currX+=x;
            }
        }
        
        
        public int getX (int i)
        { if (nodes[i].minX==nodes[i].maxX) return nodes[i].minX;
          if (nodes[i].invertX) return (nodes[i].maxX-nodes[i].ofsX.get ());
          return (nodes[i].ofsX.get ());
        }
        public int getY (int i)
        { if (nodes[i].minY==EnvelopeNode.SAME) return getY (i-1);
          if (nodes[i].minY==nodes[i].maxY) return nodes[i].minY+nodes[i].baseY;
          return (nodes[i].ofsY.get ()+nodes[i].baseY);
        }
        
        
        class MyListener extends MouseInputAdapter
        {
            int oldx;int oldy;
            int dragNode;
            
            public void mousePressed (MouseEvent e)
            {
                int x = e.getX ();
                int y = e.getY ();
                for (int i=0;i<nodes.length;i++)
                    if (((Math.abs (x-nodeX[i]))<5) && ((Math.abs (y-nodeY[i])<5)))
                    {
                        dragNode=i;
                    }
                
                repaint ();
            }
            public void mouseDragged (MouseEvent e)
            {
                int x = e.getX ();
                int y = e.getY ();
                if ((x==oldx) &&(y==oldy))return;
                if (x-oldx>0)
                {
                    while ((x-nodeX[dragNode]>5) && (getX (dragNode)<nodes[dragNode].maxX))
                    {
                        if (nodes[dragNode].invertX) nodes[dragNode].ofsX.set (nodes[dragNode].ofsX.get ()-1); else
                            nodes[dragNode].ofsX.set (nodes[dragNode].ofsX.get ()+1);
                        paintComponent (null);
                        
                    }
                } else
                {
                    while ((x-nodeX[dragNode]<-5) && (getX (dragNode)>nodes[dragNode].minX))
                    {
                        if (nodes[dragNode].invertX) nodes[dragNode].ofsX.set (nodes[dragNode].ofsX.get ()+1); else
                            nodes[dragNode].ofsX.set (nodes[dragNode].ofsX.get ()-1);
                        paintComponent (null);
                    }
                }
                if (y-oldy<0)
                {
                    while((y-nodeY[dragNode]<-5) && (getY (dragNode)<nodes[dragNode].maxY+nodes[dragNode].baseY))
                    {
                        nodes[dragNode].ofsY.set (nodes[dragNode].ofsY.get ()+1);
                        paintComponent (null);
                    }
                } else if (y-oldy>0)
                {
                    while ((y-nodeY[dragNode]>5) && (getY (dragNode)>nodes[dragNode].minY+nodes[dragNode].baseY))
                    {
                        nodes[dragNode].ofsY.set (nodes[dragNode].ofsY.get ()-1);
                        paintComponent (null);
                        
                    }
                }
                oldx=x;oldy=y;
                
                int j=0;
                for (int i=0; i<nodes.length;i++)
                {
                    if (nodes[i].minX!=nodes[i].maxX)
                    { if (i==dragNode)values[j].setText (new Integer (nodes[i].ofsX.get ()).toString ());
                      j++;
                    }
                    if (nodes[i].minY!=nodes[i].maxY)
                    { if (i==dragNode)values[j].setText (new Integer (nodes[i].ofsY.get ()).toString ());
                      j++;
                    }
                }
                
                repaint ();
            }
            public void mouseReleased (MouseEvent e)
            {updateSize (e);}
            void updateSize (MouseEvent e)
            {
                if (nodes[dragNode].senderX!=null)
                    try
                    {
                        nodes[dragNode].senderX.channel=(byte)((Device)PatchEdit.deviceList.get (patch.deviceNum)).getChannel();
                        PatchEdit.MidiOut.writeLongMessage (
                        ((Device)PatchEdit.deviceList.get (patch.deviceNum)).getPort(),nodes[dragNode].senderX.generate (nodes[dragNode].ofsX.get ()));
                    }catch (Exception e1)
                    {ErrorMsg.reportStatus (e1);}
                    if (nodes[dragNode].senderY!=null)
                        try
                        {
                            nodes[dragNode].senderY.channel=(byte)((Device)PatchEdit.deviceList.get (patch.deviceNum)).getChannel();
                            PatchEdit.MidiOut.writeLongMessage (
                            ((Device)PatchEdit.deviceList.get (patch.deviceNum)).getPort(), nodes[dragNode].senderY.generate (nodes[dragNode].ofsY.get ()));
                        }catch (Exception e2)
                        {ErrorMsg.reportStatus (e2);}
                        
            }
            
            
        }
    }}
    
