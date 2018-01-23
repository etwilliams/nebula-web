package com.dakuupa.nebula;

import static com.dakuupa.nebula.Activity.CREATE_ACTION;
import static com.dakuupa.nebula.Activity.CREATE_RELOAD_ACTION;
import static com.dakuupa.nebula.Activity.DELETE_ACTION;
import static com.dakuupa.nebula.Activity.ERROR;
import static com.dakuupa.nebula.Activity.LIST_ACTION;
import static com.dakuupa.nebula.Activity.SUCCESS;
import static com.dakuupa.nebula.Activity.UPDATE_ACTION;
import static com.dakuupa.nebula.Activity.UPDATE_RELOAD_ACTION;
import com.dakuupa.nebula.utils.NebulaLogger;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author etwilliams
 */
public class CrudActivity<T extends Model> extends Activity<T> {

    @Override
    public String doAction() {
        return processForm();
    }

    @Override
    protected boolean handleMethods() {
        boolean methodAllowed = false;
        String requestMethod = http.getRequest().getMethod();
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
            NebulaLogger.info(Activity.class.getSimpleName(), "No specified allowed method for " + this.getClass().getSimpleName());
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

            if (model.getAction().equals(LIST_ACTION)) {
                try {
                    list();
                    return model.getForwardToListResult();
                } catch (Exception e) {
                    addErrorMessage("Error listing " + model.getModelDisplayName() + " items");
                    NebulaLogger.exception("processForm", e);
                    return ERROR;
                }
            } else if (model.getAction().equals(CREATE_RELOAD_ACTION)) {
                createReload();
                model.setAction(CREATE_ACTION);
                return SUCCESS;
            } else if (model.getAction().equals(UPDATE_RELOAD_ACTION)) {
                updateReload();
                model.setAction(UPDATE_ACTION);
                return SUCCESS;
            } else if (model.getAction().equals(CREATE_ACTION)) {
                try {
                    if (validateForm()) {
                        int id = create();
                        model.setId(id);
                        addSuccessMessage("Added " + model.getModelDisplayName());
                        read();
                        model.setAction(UPDATE_ACTION);
                        if (model.isReturnToListOnAdd()) {
                            list();
                            model.setAction(LIST_ACTION);
                            return model.getForwardToListResult();
                        } else {
                            return SUCCESS;
                        }
                    } else {
                        return ERROR;
                    }
                } catch (Exception e) {
                    addErrorMessage("Error adding " + model.getModelDisplayName());
                    NebulaLogger.exception("processForm", e);
                    return ERROR;
                }
            } else if (model.getAction().equals(UPDATE_ACTION)) {
                try {
                    if (validateForm()) {
                        update();
                        read();
                        model.setAction(UPDATE_ACTION);
                        addSuccessMessage("Updated " + model.getModelDisplayName());

                        if (model.isReturnToListOnUpdate()) {
                            list();
                            model.setAction(LIST_ACTION);
                            return model.getForwardToListResult();
                        } else {
                            return SUCCESS;
                        }

                    } else {
                        return ERROR;
                    }
                } catch (Exception e) {
                    addErrorMessage("Error updating " + model.getModelDisplayName());
                    NebulaLogger.exception("processForm", e);
                    return ERROR;
                }
            } else if (model.getAction().equals(DELETE_ACTION) && model.getId() != -1) {
                try {
                    if (delete()) {
                        addSuccessMessage("Removed " + model.getModelDisplayName());
                        model.setAction(LIST_ACTION);
                        list();
                        return model.getForwardToListResult();
                    } else {
                        read();
                        return ERROR;
                    }
                } catch (Exception e) {
                    addErrorMessage("Error removing " + model.getModelDisplayName());
                    NebulaLogger.exception("processForm", e);
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } else {
            if (model.getId() != -1) {
                //reading existing
                read();
                model.setAction(UPDATE_ACTION);
            } else {
                //adding new
                model.setAction(CREATE_ACTION);
            }
            return SUCCESS;
        }
    }

    protected int create() {
        return -1;
    }

    protected void read() {

    }

    protected void update() {

    }

    protected boolean delete() {
        return false;
    }

    protected void list() {
        //override as needed
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

    public final void addHelpMessage(String msg) {
        model.getHelpMessages().add(msg);
    }

    public final void addSuccessMessage(String msg) {
        model.getSuccessMessages().add(msg);
    }

    public final void addInfoMessage(String msg) {
        model.getInfoMessages().add(msg);
    }

    public final void addWarningMessage(String msg) {
        model.getWarningMessages().add(msg);
    }

    public final void addErrorMessage(String msg) {
        model.getErrorMessages().add(msg);
    }

}
