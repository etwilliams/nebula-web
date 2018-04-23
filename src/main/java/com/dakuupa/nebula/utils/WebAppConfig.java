package com.dakuupa.nebula.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class WebAppConfig {

    public static final String LOG_TAG = "Nebula Config";
    public static final String APP_CONTEXT_PATH = "appcontext";
    public static final String ACTIVITY_PATH = "activitypath";
    public static final String ACTIVITY_PREFIX = "activityprefix";
    public static final String ACTIVITY_SUFFIX = "activitysuffix";
    public static final String HANDLER_PATH = "handlerpath";

    private String context;
    private String activityPath;
    private String activityPrefix;
    private String activitySuffix;
    private List<Class> handlers;

    public WebAppConfig(String mapping, String activityPath, String activityPrefix, String activitySuffix) {
        this.context = mapping;
        this.activityPath = activityPath;
        this.activityPrefix = activityPrefix;
        this.activitySuffix = activitySuffix;
        this.handlers = new ArrayList<>();
    }

    public WebAppConfig(String contextPath, String activityPath, String activityPrefix, String activitySuffix, List<String> handlersList) {
        this.context = contextPath;
        this.activityPath = activityPath;
        this.activityPrefix = activityPrefix;
        this.activitySuffix = activitySuffix;
        this.handlers = new ArrayList<>();

        for (String handler : handlersList) {
            NebulaLogger.info(LOG_TAG, "Loading handler " + handler);
            try {
                Class clz = Class.forName(handler);
                handlers.add(clz);
            } catch (ClassNotFoundException ex) {
                NebulaLogger.exception(LOG_TAG, ex);
            }
        }
    }

    public String getContext() {
        return context;
    }

    public String getActivityPath() {
        return activityPath;
    }

    public String getActivityPrefix() {
        return activityPrefix;
    }

    public String getActivitySuffix() {
        return activitySuffix;
    }

    public List<Class> getHandlers() {
        return handlers;
    }

}
