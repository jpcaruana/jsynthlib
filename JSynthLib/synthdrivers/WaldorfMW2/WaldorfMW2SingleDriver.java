// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package synthdrivers.WaldorfMW2;

import core.*;
import java.io.InputStream;
import java.io.PrintStream;
import javax.swing.JOptionPane;

public class WaldorfMW2SingleDriver extends Driver
{

    public WaldorfMW2SingleDriver()
    {
        super("Single Program", "Joachim Backhaus");
        sysexID = "F03E0E**10";
        sysexRequestDump = new SysexHandler("F0 3E 0E @@ 00 *BB* *NN* *XSUM* F7");
        patchNameStart = 247;
        patchNameSize = 16;
        deviceIDoffset = 3;
        checksumStart = 5;
        checksumEnd = 262;
        checksumOffset = 263;
        bankNumbers = (new String[] {
            "A", "B"
        });
        patchNumbers = DriverUtil.generateNumbers(1, 128, "#");
        patchSize = 265;
    }

    public Patch createNewPatch()
    {
        byte abyte0[] = new byte[265];
        try
        {
            InputStream inputstream = getClass().getResourceAsStream("mw2_default.syx");
            inputstream.read(abyte0);
            inputstream.close();
        }
        catch(Exception exception)
        {
            System.err.println("Unable to find mw2_default.syx using hardcoded default.");
            abyte0[0] = -16;
            abyte0[1] = 62;
            abyte0[2] = 14;
            abyte0[3] = 0;
            abyte0[4] = 16;
            abyte0[5] = 32;
            abyte0[6] = 0;
            abyte0[263] = 0;
            abyte0[264] = -9;
        }
        return new Patch(abyte0, this);
    }

    public void requestPatchDump(int i, int j)
    {
        if(sysexRequestDump == null)
        {
            JOptionPane.showMessageDialog(PatchEdit.getInstance(), "The " + toString() + " driver does not support patch getting.\n\n" + "Please start the patch dump manually...", "Get Patch", 2);
        } else
        {
            core.SysexHandler.NameValue anamevalue[] = {
                new core.SysexHandler.NameValue("BB", i), new core.SysexHandler.NameValue("NN", j), new core.SysexHandler.NameValue("XSUM", (byte)(i + j) & 0x7f)
            };
            send(sysexRequestDump.toSysexMessage(getDeviceID(), anamevalue));
            try
            {
                Thread.sleep(50L);
            }
            catch(Exception exception) { }
        }
    }
}
