package synthdrivers.YamahaMotif;
import core.PatchEdit;
import core.ErrorMsg;
public abstract class YamahaMotifSysexUtility {

  public static final int DEVICE_ID_OFFSET = 2;
  public static final int SIZE_OFFSET = 4;
  public static final int SIZE_LENGTH = 2;
  public static final int ADDRESS_OFFSET = 6;
  public static final int ADDRESS_LENGTH = 3;
  public static final int DATA_OFFSET = 9;
  public static final int CHECKSUM_OFFSET = -2;
  public static final int SYSEX_OVERHEAD = 11;

  public static void checksum( byte[] sysex ) {
    for ( int size = 0, offset = 0;
	  offset <= sysex.length - SYSEX_OVERHEAD;
	  offset += size + SYSEX_OVERHEAD ) {
      size = getSize(sysex, offset);
      checksum( sysex, offset + SIZE_OFFSET, 
		offset + size + SYSEX_OVERHEAD + CHECKSUM_OFFSET );
    }
  }
  public static void checksum( byte[] sysex, int offset ) {
    int size = getSize(sysex, offset);
    checksum(   sysex, offset + SIZE_OFFSET, 
		offset + size + SYSEX_OVERHEAD + CHECKSUM_OFFSET );
  }
  public static void checksum( byte[] sysex, int start, int end) {
    if (start < 0 || start > end || end >= sysex.length)
      return;
   int sum = 0;
    for (int i = start; i < end; i++)
      sum += sysex[i];
    sysex[end] = (byte) (((( sum & 127) ^ 127) + 1) & 127 );
  }
  public static int findBaseAddressOffset ( byte[] sysex, int address ) {
    byte high, mid, low;
    high = (byte)( (address >> 16) & 127 );
    mid = (byte)( (address >> 8) & 255 );//Mid can be negative as a wildcard
    low = (byte)( address & 127 );
    for ( int size = 0, offset = 0;
	  offset <= sysex.length - SYSEX_OVERHEAD;
	  offset += size + SYSEX_OVERHEAD ) {
      size = getSize(sysex, offset);
      if ( sysex[offset + ADDRESS_OFFSET] == high && 
	   (mid == -1 || sysex[offset + ADDRESS_OFFSET + 1] == (mid & 127)) &&
	   sysex[offset + ADDRESS_OFFSET + 2] <= low &&
	   sysex[offset + ADDRESS_OFFSET + 2] + size >= low )
	return offset;
    }
    return -1;
  }

  public static byte getParameter( byte[] sysex, int address ) {
    return getParameter(sysex, address, findBaseAddressOffset(sysex, address));
  }
  public static byte getParameter( byte[] sysex, int address, int offset ) {
    if (offset < 0 || offset + ADDRESS_OFFSET + 2 >= sysex.length)
      return -1;
    int index = offset + DATA_OFFSET + 
      (address & 127) - sysex[offset + ADDRESS_OFFSET + 2];
    if (index < 0 || index >= sysex.length)
      return -1;
    return sysex[index];
  }
  public static int getShortParameter( byte[] sysex, int address ) {
    return getShortParameter( sysex, address, 
			      findBaseAddressOffset(sysex, address));
  }
  public static int getShortParameter( byte[] sysex, int address, int offset) {
    int retval = getParameter(sysex, address, offset) << 7;
    retval |= (getParameter(sysex, address + 1, offset) & 127);
    return retval;
  }
  public static boolean setParameter( byte[] sysex, int address, int value) {
    return setParameter(sysex, address, value, 
			findBaseAddressOffset(sysex, address));
  }
  public static boolean setParameter( byte[] sysex, int address, int value, 
				      int offset ) {
    if (offset < 0 || offset + ADDRESS_OFFSET + 2 >= sysex.length)
      return false;
    int index = offset + DATA_OFFSET + 
      (address & 127) - sysex[offset + ADDRESS_OFFSET + 2];
    if (index < 0 || index >= sysex.length)
      return false;
    sysex[index] = (byte)(value & 127);
    checksum(sysex, offset);
    return true;
  }
  public static boolean setShortParameter(byte[] sysex,int address,int value) {
    return setShortParameter(sysex, address, value, 
			     findBaseAddressOffset(sysex, address));
  }
  public static boolean setShortParameter(byte[] sysex, int address, int value,
					  int offset) {
    return setParameter(sysex, address, (value >> 7) & 127, offset)
      && setParameter(sysex, address + 1, value & 127, offset);
  }

  public static void splitAndSendBulk( byte[] sysex, int port, int deviceid ) {
    byte[] msg;
     for ( int size = 0, offset = 0;
	  offset <= sysex.length - SYSEX_OVERHEAD;
	  offset += size + SYSEX_OVERHEAD ) {
       size = getSize(sysex, offset);
       msg = new byte[size + SYSEX_OVERHEAD];
       System.arraycopy(sysex, offset, msg, 0, msg.length);
       msg[DEVICE_ID_OFFSET] = (byte)(deviceid & 127);
       try {
	  PatchEdit.MidiOut.writeLongMessage (port,msg);
       } catch (Exception e) { ErrorMsg.reportStatus (e); }
     }
  }

  public static int getSize(byte[] sysex, int offset) {
    int size = sysex[ offset + SIZE_OFFSET ] << 7;
    size |= (sysex[ offset + SIZE_OFFSET + 1] & 127);
    return size;
  }
}