package com.dakuupa.nebula;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author etwilliams
 */
public class RestModel {

    protected transient Long id = null;

    protected transient String identifier;

    protected transient String action;

    protected transient RestUser user;

    protected transient String modelDisplayName;

    protected transient List list = new ArrayList();

    public RestModel() {
    }
    
    public boolean hasIdentifier(){
        return (getId() != null && getId() != -1) || StringUtils.isNotEmpty(getIdentifier());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getModelDisplayName() {
        return modelDisplayName;
    }

    public void setModelDisplayName(String modelDisplayName) {
        this.modelDisplayName = modelDisplayName;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public RestUser getUser() {
        return user;
    }

    public void populateUser(RestUser user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
