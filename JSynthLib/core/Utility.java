//======================================================================================================================
// Summary: Utility.java
// Author: phil@muqus.com - 07/2001
// Notes: Various utility functions
//======================================================================================================================

package core;
import java.io.ByteArrayOutputStream;

//======================================================================================================================
// Class: Utility
//======================================================================================================================

public class Utility extends Object {

//----------------------------------------------------------------------------------------------------------------------
// Utility->byteArrayDelete()
// Returns: byte[] resulting from deleting deleteLength bytes from src at srcOffset
//----------------------------------------------------------------------------------------------------------------------

  public static byte[] byteArrayDelete (byte[] src, int srcOffset, int deleteLength) {
    ByteArrayOutputStream os = new ByteArrayOutputStream(src.length - deleteLength);

    os.write(src, 0, srcOffset);
    os.write(src, srcOffset + deleteLength, src.length - (srcOffset + deleteLength));
    return os.toByteArray();
  }

//----------------------------------------------------------------------------------------------------------------------
// Utility->byteArrayInsert()
// Returns: byte[] resulting from inserting insertLength bytes into src at srcOffset
//----------------------------------------------------------------------------------------------------------------------

  public static byte[] byteArrayInsert (byte[] src, int srcOffset, byte[] insert, int insertOffset, int insertLength) {
    ByteArrayOutputStream os = new ByteArrayOutputStream(src.length + insertLength);

    os.write(src, 0, srcOffset);
    os.write(insert, insertOffset, insertLength);
    os.write(src, srcOffset, src.length - srcOffset);
    return os.toByteArray();
  }

//----------------------------------------------------------------------------------------------------------------------
// Utility->byteArrayReplace()
// Returns: byte[] array resulting from inserting insertLength bytes into src at srcOffset, replacing
//   replaceLength bytes
//----------------------------------------------------------------------------------------------------------------------

  public static byte[] byteArrayReplace (byte[] src, int srcOffset, int replaceLength, byte[] insert, int insertOffset, int insertLength) {
    ByteArrayOutputStream os = new ByteArrayOutputStream(src.length + insertLength - replaceLength);

    os.write(src, 0, srcOffset);
    os.write(insert, insertOffset, insertLength);
    os.write(src, srcOffset + replaceLength, src.length - (srcOffset + replaceLength));
    return os.toByteArray();
  }
} // End Class: Utility