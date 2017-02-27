package com.dakuupa.nebula;

import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author etwilliams
 */
public abstract class Activity<M> {

    //results
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    //actions
    public static final String LIST_ACTION = "list";
    public static final String CREATE_ACTION = "create";
    public static final String UPDATE_ACTION = "update";
    public static final String DELETE_ACTION = "delete";
    public static final String RELOAD_ACTION = "reload";
    public static final String CREATE_RELOAD_ACTION = "createreload";
    public static final String UPDATE_RELOAD_ACTION = "updatereload";
    public static final String READ_ACTION = "read";

    protected M model;
    protected HttpWrapper http;

    public Activity() {
    }

    private void loadModel() {

        String modelName = ModelMapper.getModelName(this);
        if (modelName != null) {
            model = (M) ModelMapper.getModel(modelName);
            ModelMapper.map(model, http.getRequest());
        }

    }

    public void preAction() {
    }

    public abstract String doAction();

    public void setHttpStatus(String result) {
        if (StringUtils.equals(result, SUCCESS)) {
            http.getResponse().setStatus(HttpServletResponse.SC_OK);
        } else if (StringUtils.equals(result, ERROR)) {
            http.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void postAction(String result) {

    }

    public void complete(String result) {
        http.writeFinal();
    }

    protected void setHttp(HttpWrapper http) {
        this.http = http;
    }

    public void process() {
        loadModel();
        preAction();
        String result = doAction();
        setHttpStatus(result);
        postAction(result);
        complete(result);
    }

    public M getModel() {
        return model;
    }

}
