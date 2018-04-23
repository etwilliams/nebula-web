package com.dakuupa.nebula;

import com.dakuupa.nebula.utils.NebulaLogger;
import java.util.List;
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

    public boolean preAction() {
        return true;
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
        boolean allowedMethods = handleMethods();
        if (allowedMethods) {
            boolean preOk = preAction();
            if (preOk) {
                String result = doAction();
                setHttpStatus(result);
                postAction(result);
                complete(result);
            }
        }
    }

    public M getModel() {
        return model;
    }

    protected boolean handleMethods() {
        boolean methodAllowed = false;
        String requestMethod = http.getRequest().getMethod();
        NebulaLogger.info(Activity.class.getSimpleName(), "Request method: " + http.getRequest().getMethod());
        List<HTTPMethod> methods = ReflectionHelper.getHTTPMethods(this.getClass());
        if (methods != null && !methods.isEmpty()) {
            methodAllowed = false;
            for (HTTPMethod method : methods) {
                System.out.println(method.value());
                if (requestMethod.equals(method.value())) {
                    methodAllowed = true;
                    break;
                }
            }

        } else {
            NebulaLogger.info(Activity.class.getSimpleName(), "No specified allowed method for " + this.getClass().getSimpleName());
            return true;
        }
        if (!methodAllowed) {
            methodNotAllowed(requestMethod, http);
        }
        return methodAllowed;
    }

    protected void methodNotAllowed(String method, HttpWrapper http) {
        try {
            http.getResponse().setContentType("text/html");
            http.getResponse().setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            http.getResponse().getWriter().println("<html><body style='text-align:center; font-family: Sans-Serif'><h1>Error 405</h1>Method " + method + " not allowed");
            http.getResponse().getWriter().println("</body></html>");
            http.setOutputFinished(true);
        } catch (Exception e) {

        }
    }

}
