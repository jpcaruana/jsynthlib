// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package synthdrivers.WaldorfMW2;


public class MW2Constants
{

    public MW2Constants()
    {
    }

    public static final byte SYSEX_START_BYTE = -16;
    public static final byte SYSEX_END_BYTE = -9;
    public static final int SYSEX_HEADER_OFFSET = 8;
    public static final int PATCH_SIZE = 265;
    public static final int PATCH_NAME_SIZE = 16;
    public static final int PATCH_NAME_START = 247;
    public static final int DEVICE_ID_OFFSET = 3;
    public static final String SYSEX_ID = "F03E0E**";
    public static final String DEFAULT_SYSEX_FILENAME = "mw2_default.syx";
}
