package org.jsynthlib.editorbuilder.widgets;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class WidgetBeanInfo extends SimpleBeanInfo {
    private static final PropertyDescriptor props[];
    static {
        PropertyDescriptor p[] = null;
        try {
            p = new PropertyDescriptor[] {
                    new PropertyDescriptor("id", Widget.class),
                    //new PropertyDescriptor("type", Widget.class),
            };
        } catch (IntrospectionException e) {
            p = new PropertyDescriptor[0];
            System.out.println("Debug Me!");
        }
        props = p;
    }
    public PropertyDescriptor[] getPropertyDescriptors() {
        return props;
    }
}
