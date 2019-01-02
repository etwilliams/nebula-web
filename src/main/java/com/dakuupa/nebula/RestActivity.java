package com.dakuupa.nebula;

import static com.dakuupa.nebula.Activity.CREATE_ACTION;
import static com.dakuupa.nebula.Activity.DELETE_ACTION;
import static com.dakuupa.nebula.Activity.ERROR;
import static com.dakuupa.nebula.Activity.SUCCESS;
import static com.dakuupa.nebula.Activity.UPDATE_ACTION;
import com.dakuupa.nebula.utils.NebulaLogger;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author etwilliams
 * @param <T>
 */
public class RestActivity<T extends RestModel> extends Activity<T> {

    public static final String ERROR_NOT_FOUND = "not_found_error";
    public static final String ERROR_FORBIDDEN = "forbidden_error";
    
    @Override
    public String doAction() {
        return processForm();
    }

    @Override
    protected boolean handleMethods() {

        String requestMethod = http.getRequest().getMethod();

        String pathInfo = http.getRequest().getPathInfo();
        String identifier = pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
        if (StringUtils.isNumeric(identifier)) {
            model.setId(Integer.parseInt(identifier));
        } else if (StringUtils.isNotEmpty(identifier) && !StringUtils.equals(identifier, http.getActivityName())) {
            model.setIdentifier(identifier);
        }

        if (requestMethod.equalsIgnoreCase("GET")) {
            model.setAction(READ_ACTION);
        } else if (requestMethod.equalsIgnoreCase("POST")) {
            model.setAction(CREATE_ACTION);
        } else if (requestMethod.equalsIgnoreCase("PUT")) {
            model.setAction(UPDATE_ACTION);
        } else if (requestMethod.equalsIgnoreCase("DELETE")) {
            model.setAction(DELETE_ACTION);
        }

        boolean methodAllowed = false;
        String requestAction = model.getAction() == null ? "" : model.getAction();

        NebulaLogger.info(Activity.class.getSimpleName(), "Request method: " + http.getRequest().getMethod());
        NebulaLogger.info(Activity.class.getSimpleName(), "Request action: " + requestAction);
        List<HTTPMethod> methods = ReflectionHelper.getHTTPMethods(this.getClass());
        if (methods != null && !methods.isEmpty()) {
            methodAllowed = false;
            for (HTTPMethod method : methods) {
                if (requestMethod.equals(method.value()) && requestAction.equals(method.action())) {
                    methodAllowed = true;
                    break;
                }
            }

        } else {
            return true;
        }
        if (!methodAllowed) {
            methodNotAllowed(requestMethod, http);
        }
        return methodAllowed;
    }

    @Override
    protected void methodNotAllowed(String method, HttpWrapper http) {
        try {
            String action = model.getAction() == null ? "--no action--" : model.getAction();
            http.getResponse().setContentType("text/html");
            http.getResponse().setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            http.getResponse().getWriter().println("<html><body style='text-align:center; font-family: Sans-Serif'><h1>Error 405</h1>Method " + method + " not allowed for action " + action);
            http.getResponse().getWriter().println("</body></html>");
            http.setOutputFinished(true);
        } catch (Exception e) {

        }
    }

    /*
     * Process CRUD forms (default method)
     */
    protected String processForm() {

        if (model.getAction() != null && !model.getAction().equals("")) {

            if (model.getAction().equals(CREATE_ACTION)) {
                try {
                    if (validateForm()) {
                        int id = create();
                        model.setId(id);
                        if (read()) {
                            return SUCCESS;
                        } else {
                            return ERROR;
                        }
                    } else {
                        return ERROR;
                    }
                } catch (Exception e) {
                    NebulaLogger.exception("processForm", e);
                    return ERROR;
                }
            } else if (model.getAction().equals(READ_ACTION)) {
                
                if (read()){
                    return SUCCESS;
                }
                else{
                    return ERROR_NOT_FOUND;
                }

            } else if (model.getAction().equals(UPDATE_ACTION)) {
                try {
                    if (validateForm() && update()) {
                        read();
                        return SUCCESS;
                    } else {
                        return ERROR;
                    }
                } catch (Exception e) {
                    NebulaLogger.exception("processForm", e);
                    return ERROR;
                }
            } else if (model.getAction().equals(DELETE_ACTION) && model.hasIdentifier()) {
                try {
                    if (delete()) {
                        return SUCCESS;
                    } else {
                        return ERROR_NOT_FOUND;
                    }
                } catch (Exception e) {
                    NebulaLogger.exception("processForm", e);
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } else {
            return ERROR;
        }
    }

    protected int create() {
        return -1;
    }

    protected boolean read() {
        return true;
    }

    protected boolean update() {
        return true;
    }

    protected boolean delete() {
        return true;
    }

    //used when page is reloaded when adding a new item
    protected void createReload() {
        //override as needed
    }

    //used when page is reloaded when editing a new item
    protected void updateReload() {
        //override as needed
    }

    //override to add validation
    protected boolean validateForm() {
        return true;
    }

}
