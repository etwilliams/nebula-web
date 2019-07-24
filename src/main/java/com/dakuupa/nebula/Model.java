package com.dakuupa.nebula;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author etwilliams
 */
public class Model {

    protected transient Long id = null;//-1;
    
    protected transient String identifier;
    
    //action to call (for processForm method)

    protected transient String action;

    protected transient User user = new User();

    protected transient String modelDisplayName;

    protected transient String forwardToListResult;

    protected transient boolean requirePermission = false;

    protected transient boolean returnToListOnAdd = false;

    protected transient boolean returnToListOnUpdate = false;
    //item list

    protected transient List list = new ArrayList();
    //Messages

    private transient List<String> helpMessages = new ArrayList<>();

    private transient List<String> successMessages = new ArrayList<>();

    private transient List<String> infoMessages = new ArrayList<>();

    private transient List<String> warningMessages = new ArrayList<>();

    private transient List<String> errorMessages = new ArrayList<>();

    public Model() {
    }

    public Model(String modelDisplayName, String forwardToListResult, boolean requirePermission, boolean returnToListOnAdd) {
        this.modelDisplayName = modelDisplayName;
        this.forwardToListResult = forwardToListResult;
        this.requirePermission = requirePermission;
        this.returnToListOnAdd = returnToListOnAdd;
    }

    public Model(String modelDisplayName, String forwardToListResult, boolean requirePermission, boolean returnToListOnAdd, boolean returnToListOnUpdate) {
        this.modelDisplayName = modelDisplayName;
        this.forwardToListResult = forwardToListResult;
        this.requirePermission = requirePermission;
        this.returnToListOnAdd = returnToListOnAdd;
        this.returnToListOnUpdate = returnToListOnUpdate;
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

    public String getForwardToListResult() {
        return forwardToListResult;
    }

    public void setForwardToListResult(String forwardToListResult) {
        this.forwardToListResult = forwardToListResult;
    }

    public boolean isReturnToListOnAdd() {
        return returnToListOnAdd;
    }

    public void setReturnToListOnAdd(boolean returnToListOnAdd) {
        this.returnToListOnAdd = returnToListOnAdd;
    }

    public List getList() {
        return list;
    }

    public boolean isRequirePermission() {
        return requirePermission;
    }

    public void setList(List list) {
        this.list = list;
    }

    public User getUser() {
        return user;
    }

    public void populateUser(User user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getHelpMessages() {
        return helpMessages;
    }

    public List<String> getInfoMessages() {
        return infoMessages;
    }

    public List<String> getSuccessMessages() {
        return successMessages;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }

    public boolean isReturnToListOnUpdate() {
        return returnToListOnUpdate;
    }

    public void setReturnToListOnUpdate(boolean returnToListOnUpdate) {
        this.returnToListOnUpdate = returnToListOnUpdate;
    }
}
