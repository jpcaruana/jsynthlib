package org.jsynthlib.jsynthlib.synthdrivers.yamaha.motif;



import org.jsynthlib.jsynthlib.xml.XMLDriverImplementation
import org.jsynthlib.jsynthlib.xml.XMLPatch
import org.jsynthlib.jsynthlib.xml.XMLDevice
import org.jsynthlib.jsynthlib.xml.XMLParameter


class normal_voice extends XMLDriverImplementation{

	public static final int EDIT_BUFFER = 0x0f
	public static final int NORMAL_VOICE = 0x08

	public void sendPatch(XMLPatch p) {
		sendPatch(p, -1)
    }
    public void storePatch(XMLPatch p, int bank, int patch) {
        sendPatch(p, patch)
    }
    public void sendParameter(XMLPatch patch, XMLParameter param) {
		d = patch.getDevice()
		pmsg = param.getMessage(patch)
		hi = pmsg[5]
		mid = pmsg[6]
		low = pmsg[7] + param.getOffset() - 8
    		if (param.getType() != param.STRING) { 
			msg = "F0 43 ${hex(d.getChannel()+16)} 6B
				  ${hex(hi)} ${hex(mid)} ${hex(low)}
				  ${param.encode(patch)} F7"
    			d.send(midiMessage(msg))
    		}// Could loop and send each character individually,
    		 // but I don't think it's necessary
    }
    public void requestPatchDump(XMLDevice d, int bank, int patch) {
        d.send(midiMessage(
            "F0 43 ${hex(d.getChannel() + 32)}
             6B 0E 08 ${hex(patch)} F7"))
    }
    
    protected void sendPatch(XMLPatch p, int i) {
    		XMLParameter[] params = new XMLParameter[] { 
    			p.getParameter("Header", "Bulk Header", "Address Mid"),
    			p.getParameter("Footer", "Bulk Footer", "Address Mid"),
    			p.getParameter("Header", "Bulk Header", "Address Low"),
    			p.getParameter("Footer", "Bulk Footer", "Address Low")
    		}
    		if (i == -1) {
    			params[0].set(p, EDIT_BUFFER)
    			params[1].set(p, EDIT_BUFFER)
    			params[2].set(p, 0)
    			params[3].set(p, 0)
    		} else {
    			params[0].set(p, NORMAL_VOICE)
    			params[1].set(p, NORMAL_VOICE)
    			params[2].set(p, i)
    			params[3].set(p, i)    		
    		}
    		p.flush()
    		p.calculateChecksum()
    		d = p.getDevice()
    		for (m in p.getMessages()) {
    			d.send(m)
    		}
    }
}