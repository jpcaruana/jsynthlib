package org.jsynthlib.editorbuilder.widgets;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class RootPanelWidgetBeanInfo extends SimpleBeanInfo {
    
    public PropertyDescriptor[] getPropertyDescriptors() {
        // no properties in addition to PanelWidget's
        return new PropertyDescriptor[0];
    }
    public BeanInfo[] getAdditionalBeanInfo() {
        try {
            // Skip the methods from AnchoredWidget
            BeanInfo w[] = {
                    Introspector.getBeanInfo(Widget.class),
                    Introspector.getBeanInfo(PanelWidget.class, AnchoredWidget.class),
                    };
            return w;
        } catch (IntrospectionException ex) {
            // Shouldn't happen!
            System.out.println("Break here!");
            return null;
        }
    }
}
