package core;

public class SysexSender
{ String sysex;
  public byte channel;
 public SysexSender() {} 
 public SysexSender(String s) {sysex=s;}
  public byte [] generate(int value) 
  {
    byte []b=new byte[sysex.length()/2];
    for (int i=0;i<sysex.length();i+=2)
    {
       if (sysex.charAt(i)=='*') b[i/2]=(byte)value; else
       if (sysex.charAt(i)=='@') b[i/2]=(byte) (channel-1); else
       if (sysex.charAt(i)=='#') b[i/2]=(byte) (channel-1+16);

        else
	{
	  Integer in = new Integer(0);
	  b[i/2]=(byte)in.parseInt(sysex.substring(i,i+2),16);
	}
     }
   return b;
  }
}
