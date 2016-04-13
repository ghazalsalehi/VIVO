/*
Copyright (c) 2015, QUT University
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// LIBRDF-48
public class PropertyEditTemplateResponseValues extends BaseResponseValues {

    private final String templateName;
    private final Map<String, Object> map;
    
    public PropertyEditTemplateResponseValues(String templateName) {
        this.templateName = templateName;
        this.map = new HashMap<String, Object>();
    }

    public PropertyEditTemplateResponseValues(String templateName, int statusCode) {
        super(statusCode);
        this.templateName = templateName;
        this.map = new HashMap<String, Object>();
    }
    
    public PropertyEditTemplateResponseValues(String templateName, Map<String, Object> map) {
        this.templateName = templateName;
        this.map = map;
    }

    public PropertyEditTemplateResponseValues(String templateName, Map<String, Object> map, int statusCode) {
        super(statusCode);
        this.templateName = templateName;
        this.map = map;
    }
    
    public PropertyEditTemplateResponseValues put(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    @Override
    public Map<String, Object> getMap() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }   

}

