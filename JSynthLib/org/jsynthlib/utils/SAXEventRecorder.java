package org.jsynthlib.utils;

import java.util.ArrayList;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class for recording/playing back SAX events. Call recordX at
 * the beginning of X in you DefaultHandler.  If an element has
 * an id attribute, call startRecording, and save the result.
 * Recording will automatically stop when that element is ended.
 * Call replay on the recording to replay the events when the id
 * is referenced.  Replayed events will not have the id attribute
 * to prevent recording looops.
 * 
 * @author ribrdb
 */
public class SAXEventRecorder {
    private ArrayList events = new ArrayList();
    private LinkedList recordings = new LinkedList();
    private int curdepth = 0;
    
    public Recording startRecording() {
        Recording r = new Recording(events.size());
        recordings.addFirst(r);
        return r;
    }
    
    public void recordStartElement(String uri, String localName, String qName,
            Attributes attributes) {
        curdepth = curdepth + 1;
        if (recordings.isEmpty())
            return;
        events.add(new StartElement(uri, localName, qName, attributes));
    }
    public void recordEndElement(String uri, String localName, String name) {
        curdepth = curdepth - 1;
        if (recordings.isEmpty())
            return;
        Recording r = (Recording)recordings.getFirst();
        if (r.depth > curdepth) {
            recordings.removeFirst();
            r.setEnd(events.size());
        }
        if (!recordings.isEmpty())
            events.add(new EndElement(uri, localName, name));
    }
    public void recordCharacters(String chars) {
        if (!recordings.isEmpty())
            events.add(new Characters(chars));
    }
    
    public class Recording {
        private int start;
        private int end = -1;
        private int depth;

        private Recording(int start) {
            this.start = start;
            this.depth = curdepth;
        }
        
        private void setEnd(int end) {
            this.end = end;
        }
        
        public void replay(DefaultHandler h) throws SAXException {
            if (end == -1)
                throw new SAXException("Cannot replay recorded events until recording is finished.");
            for (int i = start; i < end; i++) {
                RecordedEvent e = (RecordedEvent)events.get(i);
                e.replay(h);
            }
        }
    }
    private interface RecordedEvent {
        public void replay(DefaultHandler h) throws SAXException;
    }
    private class StartElement implements RecordedEvent {
        private String uri;
        private String localName;
        private String qName;
        private AttributesImpl attributes;
        
        public StartElement(String uri, String localName, String qName,
                Attributes attributes) {
            this.uri = uri;
            this.localName = localName;
            this.qName = qName;
            this.attributes = new AttributesImpl(attributes);
            int i = attributes.getIndex("id");
            if (i != -1)
                this.attributes.removeAttribute(i);
        }
        public void replay(DefaultHandler h) throws SAXException {
           h.startElement(uri, localName, qName, attributes);
        }
    }
    private class EndElement implements RecordedEvent {
        private String uri;
        private String localName;
        private String qName;

        public EndElement(String uri, String localName, String name) {
            this.uri = uri;
            this.localName = localName;
            qName = name;
        }
        public void replay(DefaultHandler h) throws SAXException {
           h.endElement(uri, localName, qName);
        }
    }
    private class Characters implements RecordedEvent {
        private char ch[];
        
        public Characters(String s) {
            ch = s.toCharArray();
        }
        
        public void replay(DefaultHandler h) throws SAXException {
            h.characters(ch, 0, ch.length);
        }

    }
}
