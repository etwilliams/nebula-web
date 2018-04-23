package com.dakuupa.nebula;

import com.dakuupa.nebula.utils.NebulaLogger;
import com.dakuupa.nebula.utils.WebAppConfig;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author etwilliams
 */
public class ActivityManager {

    public static final String LOG_TAG = ActivityManager.class.getSimpleName();

    private WebAppConfig config;

    public ActivityManager(WebAppConfig config) {
        this.config = config;
    }

    public boolean performActivity(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        HttpWrapper http = null;
        String path = request.getRequestURI();
        String activityName = "";

        log("Request=" + path);

        //TODO: add activity mapping annotations etc
        try {
            if (path != null) {
                http = new HttpWrapper(path, request, response);

                String activityPath = StringUtils.substringAfter(path, config.getContext());

                String[] parts = activityPath.split("/");

                if (parts != null && parts.length == 2) {
                    activityName = parts[parts.length - 1];
                } else if (parts != null && parts.length > 1) {
                    activityName = parts[parts.length - 2];
                }
                http.setActivityName(activityName);

            }

            if (StringUtils.isAllLowerCase(activityName)) {
                activityName = StringUtils.capitalize(activityName);
            }

            if (!StringUtils.isEmpty(config.getActivityPrefix())) {
                activityName = activityName.replace(config.getActivityPrefix(), "");
            }

            if (!StringUtils.isEmpty(config.getActivitySuffix())) {
                activityName += config.getActivitySuffix();
            }

            if (http != null && activityName != null && !activityName.trim().isEmpty()) {

                Class clz = Class.forName(config.getActivityPath() + "." + activityName);

                Object instance = clz.newInstance();
                if (instance instanceof Activity) {

                    Activity activity = ((Activity) instance);

                    if (getContentType(clz) != null && (http.getResponse().getContentType() == null || http.getResponse().getContentType().isEmpty())) {
                        http.getResponse().setContentType(getContentType(clz));
                    } else {
                        http.getResponse().setContentType("text/html");
                    }
                    log("Content Type=" + http.getResponse().getContentType());

                    activity.setHttp(http);
                    activity.process();

                    return true;
                } else {
                    activityNotFound(activityName, http);
                    return false;
                }

            }

        } catch (ClassNotFoundException ex) {

            log("Activity " + activityName + " not found. Looking for resource request.");

            RequestDispatcher rd = request.getServletContext().getNamedDispatcher("default");
            if (rd != null) {
                HttpServletRequest wrapped = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getServletPath() {
                        return "";
                    }
                };
                rd.forward(wrapped, response);
            }

        } catch (InstantiationException | IllegalAccessException | SecurityException ex) {
            NebulaLogger.exception(LOG_TAG, ex);
        }
        return false;
    }

    private static void activityNotFound(String activity, HttpWrapper http) throws IOException {
        http.getResponse().setContentType("text/html");
        http.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        http.getResponse().getWriter().println("<html><body style='text-align:center; font-family: Sans-Serif'><h1>Error 500</h1>Activity not found: " + WebAppConfig.ACTIVITY_PATH + "." + activity);
        http.getResponse().getWriter().println("</body></html>");
        http.setOutputFinished(true);
    }

    private static String getContentType(Class clazz) {
        if (clazz.isAnnotationPresent(ContentType.class)) {
            return ((ContentType) (clazz.getAnnotation(ContentType.class))).type();
        }
        return null;
    }

    private static void log(String msg) {
        NebulaLogger.info(LOG_TAG, msg);
    }

}
