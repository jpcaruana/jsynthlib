/**
 * Define Action classes
 * @version $Id$
 */

package core;

//import javax.swing.event.*;

public final class Actions {
    // I hope 64bit is enough for a while.
    static final long EN_ABOUT			= 0x0000000000000001L;
    static final long EN_COPY			= 0x0000000000000002L;
    static final long EN_CROSSBREED		= 0x0000000000000004L;
    static final long EN_CUT			= 0x0000000000000008L;
    static final long EN_DELETE			= 0x0000000000000010L;
    static final long EN_DELETE_DUPLICATES	= 0x0000000000000020L;
    static final long EN_DOCS			= 0x0000000000000040L;
    static final long EN_EDIT			= 0x0000000000000080L;
    static final long EN_EXIT			= 0x0000000000000100L;
    static final long EN_EXPORT			= 0x0000000000000200L;
    static final long EN_EXTRACT		= 0x0000000000000400L;
    static final long EN_GET			= 0x0000000000000800L;
    static final long EN_HOME_PAGE		= 0x0000000000001000L;
    static final long EN_IMPORT			= 0x0000000000002000L;
    static final long EN_IMPORT_ALL		= 0x0000000000004000L;
    static final long EN_LICENSE		= 0x0000000000008000L;
    static final long EN_MONITOR		= 0x0000000000010000L;
    static final long EN_NEW			= 0x0000000000020000L;
    static final long EN_NEW_PATCH		= 0x0000000000040000L;
    static final long EN_NEW_SCENE		= 0x0000000000080000L;
    static final long EN_NEXT_FADER		= 0x0000000000100000L;
    static final long EN_OPEN			= 0x0000000000200000L;
    static final long EN_PASTE			= 0x0000000000400000L;
    static final long EN_PLAY			= 0x0000000000800000L;
    static final long EN_PREFS			= 0x0000000001000000L;
    static final long EN_REASSIGN		= 0x0000000002000000L;
    static final long EN_SAVE			= 0x0000000004000000L;
    static final long EN_SAVE_AS		= 0x0000000008000000L;
    static final long EN_SEARCH			= 0x0000000010000000L;
    static final long EN_SEND			= 0x0000000020000000L;
    static final long EN_SEND_TO		= 0x0000000040000000L;
    static final long EN_SORT			= 0x0000000080000000L;
    static final long EN_STORE			= 0x0000000100000000L;
    static final long EN_TRANSFER_SCENE		= 0x0000000200000000L;
    static final long EN_UPLOAD			= 0x0000000400000000L;

    /** All actions excluding ones which are always eanbled.  */
    static final long EN_ALL	= (//EN_ABOUT
				   EN_COPY
				   | EN_CROSSBREED
				   | EN_CUT
				   | EN_DELETE
				   | EN_DELETE_DUPLICATES
				   //| EN_DOCS
				   | EN_EDIT
				   //| EN_EXIT
				   | EN_EXPORT
				   | EN_EXTRACT
				   | EN_GET
				   | EN_HOME_PAGE
				   | EN_IMPORT
				   | EN_IMPORT_ALL
				   //| EN_LICENSE
				   //| EN_MONITOR
				   //| EN_NEW
				   | EN_NEW_PATCH
				   //| EN_NEW_SCENE
				   //| EN_NEXT_FADER
				   //| EN_OPEN
				   | EN_PASTE
				   | EN_PLAY
				   //| EN_PREFS
				   | EN_REASSIGN
				   | EN_SAVE
				   | EN_SAVE_AS
				   | EN_SEARCH
				   | EN_SEND
				   | EN_SEND_TO
				   | EN_SORT
				   | EN_STORE
				   | EN_TRANSFER_SCENE
				   | EN_UPLOAD);
}
