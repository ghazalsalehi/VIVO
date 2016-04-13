/*
Copyright (c) 2015, QUT University
All rights reserved.
*/

package edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;

//LIBRDF-48
public class PropertyTemplateResponseValues extends BaseResponseValues {

    private final String redirectUrl;
    private Individual ind;
    private String propertyName;
    /** 
     * The string redirectUrl will get the context added.    
       If you want a redirect for a URL that has the context already added, 
       as is the case if a UrlBuilder was used. use the class DirectRedirectResponseValues. 
       
       This will attempt to handle an off site redirect by checking for
        "://" in the URL. 
     */
    
    public PropertyTemplateResponseValues(String redirectUrl) {
        this.redirectUrl = getRedirectUrl(redirectUrl);
    }
    
    public PropertyTemplateResponseValues(String redirectUrl, Individual ind, String propertyName) {
    	this.redirectUrl = getRedirectUrl(redirectUrl);
        this.ind = ind;
        this.propertyName = propertyName;
    }

    public PropertyTemplateResponseValues(String redirectUrl, int statusCode) {
        super(statusCode);
        this.redirectUrl = getRedirectUrl(redirectUrl);
    }
    
    public PropertyTemplateResponseValues(Route redirectUrl) {
        this.redirectUrl = UrlBuilder.getUrl(redirectUrl);
    }
    
    @Override
    public String getRedirectUrl() {
        return this.redirectUrl;
    }
    
    protected String getRedirectUrl(String redirectUrl) {
        return redirectUrl.contains("://") ? redirectUrl : UrlBuilder.getUrl(redirectUrl);
    }
    
    public Individual getIndividual(){
    	return this.ind;
    }
    
    public String getPropertyName(){
    	return this.propertyName;
    }

}

