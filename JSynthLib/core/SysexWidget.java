package core;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
public class SysexWidget extends JPanel
{
    int valueMin;
    int valueMax;
    int valueCurr;
    //int sysexOffset;
    SysexSender sysexString=null;
    public ParamModel paramModel;
    int sliderNum;
    public int numFaders=1; //how many faders does the widget use?
    Patch patch;
    String label;
    public JLabel jlabel;
    public SysexWidget ()
    {
        super();
        patch=null;
    }
    public SysexWidget (String l,Patch p,int min, int max)
    {
        super();
        valueMin=min;
        valueMax=max;
        setValue (p);
        patch=p;
        label=l;
        setup ();
    }
    
    public SysexWidget (String l,Patch p,int min, int max,ParamModel ofs)
    {super(); valueMin=min; valueMax=max;paramModel=ofs;setValue (p);patch=p;label=l;setup ();}
    public SysexWidget (String l,Patch p,int min, int max,ParamModel ofs,SysexSender s)
    {super(); valueMin=min; valueMax=max;paramModel=ofs;sysexString=s;setValue (p);label=l;patch=p;setup ();}
    public SysexWidget (String l,Patch p,int min, int max,SysexSender s)
    {super(); valueMin=min; valueMax=max;sysexString=s;setValue (p);label=l;patch=p;setup ();}
    public Insets getInsets ()
    {return new Insets (0,0,0,0);}
    public void setValue (Patch p)
    {
        valueCurr=paramModel.get ();
    }
    public void setValue (int fader, int value)
    {}
    public int getValue ()
    {return valueCurr;}
    public void setValue (int v)
    {valueCurr=v;}
    public void sendSysex ()
    {
        paramModel.set (valueCurr);
        if (sysexString!=null) // do it only, if there is a sysex-sender available
        {
            try
            {
                //paramModel.set (valueCurr);
                PatchEdit.MidiOut.writeLongMessage (
                ((Device)PatchEdit.appConfig.getDevice (patch.deviceNum)).getPort (),sysexString.generate (valueCurr));
            }catch (Exception e)
            {ErrorMsg.reportStatus (e);}
        }
    }
    
    public void setSliderNum (int num)
    {
        sliderNum=num;
        if (num>0)setToolTipText ("Bank "+(((num-1)/16)+"  Slider "+(((num-1)%16)+1)));
        if (num<0)
        {num=0-num;setToolTipText ("Bank "+((num-1)/16)+"  Button "+(((num-1)%16)+1));}
    }
    
    public void setup ()
    {
        if (sysexString!=null) // If there is no Sysex-Sender avaliable, don't initialize it
        {
            sysexString.channel=(byte)((Device)PatchEdit.appConfig.getDevice (patch.deviceNum)).getChannel();
        }
    }
}
