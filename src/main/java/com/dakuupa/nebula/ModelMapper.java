package com.dakuupa.nebula;

import com.dakuupa.nebula.utils.NebulaLogger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author etwilliams
 */
public class ModelMapper {

    public static final String LOG_TAG = ModelMapper.class.getSimpleName();
    private static HashMap<String, String> mappings = new HashMap<>();

    public static String getModelName(Object activity) {

        String modelName = null;
        modelName = mappings.get(activity.getClass().getCanonicalName());

        if (modelName == null) {

            Type[] types = ModelMapper.getParameterizedTypes(activity);
            if (types != null && types.length > 0) {
                log("Model=" + ModelMapper.getParameterizedTypes(activity)[0].getTypeName());
                ModelMapper.mappings.put(activity.getClass().getCanonicalName(), ModelMapper.getParameterizedTypes(activity)[0].getTypeName());
                return ModelMapper.getParameterizedTypes(activity)[0].getTypeName();
            }

            return null;
        } else {
            log("Model from cache=" + modelName);
            return modelName;
        }

    }

    public static Object getModel(String modelName) {
        try {
            log("Creating model " + modelName);

            if (modelName.contains("$")) {
                log("Model is inner class");
                log("Parent class is " + StringUtils.substringBefore(modelName, "$"));

                Class<?> parentClass = Class.forName(StringUtils.substringBefore(modelName, "$"));
                Object parent = parentClass.newInstance();

                Class<?> innerClass = Class.forName(modelName);
                Constructor<?> ctor = innerClass.getDeclaredConstructor(parentClass);

                return ctor.newInstance(parent);

            } else {
                return Class.forName(modelName).newInstance();
            }

        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ModelMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static Type[] getParameterizedTypes(Object object) {
        Type superclassType = object.getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            return null;
        }
        return ((ParameterizedType) superclassType).getActualTypeArguments();
    }

    public static boolean map(Object obj, HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();

        try {

            List<String> paramsToRemove = new ArrayList<>();
            for (Entry<String, String[]> entry : parameterMap.entrySet()) {
                String name = entry.getKey();
                //remove 0-length params. The BeanUtils will choke on this
                if (entry.getValue().length == 0) {
                    paramsToRemove.add(name);
                }
            }

            //remove 0-length params. The BeanUtils will choke on this
            for (String param : paramsToRemove) {
                parameterMap.remove(param);
            }

            BeanUtils.populate(obj, parameterMap);

            if (request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {

                Map<String, String> files = new HashMap<>();

                File folder = new File(System.getProperty("java.io.tmpdir") + "/" + generateUUID() + "/");
                System.out.println("Create tmp folder " + folder);
                if (!folder.exists()) {
                    folder.mkdir();
                }

                for (Part part : request.getParts()) {
                    if (part.getSubmittedFileName() != null) {

                        String tempName = folder.getAbsolutePath() + "/" + part.getSubmittedFileName();
                        System.out.println("Create file tmp path " + tempName);
                        writeFile(tempName, part.getInputStream());
                        log("Wrote " + part.getName() + " to " + tempName);
                        files.put(part.getName(), tempName);
                    }
                }

                List<Field> fields = ReflectionHelper.getAllFields(obj.getClass());
                for (Field field : fields) {

                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    Object fieldInstance = field.get(obj);
                    boolean isCollection = false;

                    if (type == List.class) {
                        fieldInstance = new ArrayList();
                        isCollection = true;
                    }

                    if (isCollection) {

                        Type ttype = field.getGenericType();
                        if (ttype instanceof ParameterizedType) {
                            ParameterizedType pType = (ParameterizedType) ttype;
                            if (pType.getActualTypeArguments()[0].getTypeName().equals("java.io.File")) {
                                if (fieldInstance instanceof ArrayList) {

                                    //TODO maintain order
                                    for (String key : files.keySet()) {
                                        if (key.contains(field.getName() + "[")) {
                                            String num = key.replace(field.getName(), "");
                                            ((ArrayList) fieldInstance).add(new File(files.get(field.getName() + num)));
                                            field.set(obj, fieldInstance);
                                        }
                                    }
                                }
                            }
                        }

                    } else if (type.getName().equals("java.io.File")) {
                        String path = files.get(field.getName());
                        if (path != null) {
                            field.set(obj, new File(path));
                        }
                    }

                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private static void writeFile(String filename, InputStream inputStream) {

        OutputStream outputStream = null;

        try {

            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(new File(filename));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private static void handleParameter(Object obj, Field field, Class<?> type, String value) {
        try {
            //AppLogger.info(LOG_TAG, "Parameter " + field.getName() + "=" + value);
            String objVal = value;
            if (type.getName().equals("java.lang.String")) {
                String val = (String) objVal;
                field.set(obj, val);
            } else if (type.getName().equals("java.util.Date")) {
                //Date val = (Date) objVal;
                //field.set(obj, val);
            } else if (type.getName().equals("int") || type.getName().equals("java.lang.Integer")) {

                try {
                    int val = Integer.parseInt(objVal);
                    field.set(obj, val);
                } catch (Exception e) {
                    handleBadModelField(field.getName(), type.getName(), objVal);
                }

            } else if (type.getName().equals("long") || type.getName().equals("java.lang.Long")) {
                try {
                    long val = Long.parseLong(objVal);
                    field.set(obj, val);
                } catch (Exception e) {
                    handleBadModelField(field.getName(), type.getName(), objVal);
                }
            } else if (type.getName().equals("double") || type.getName().equals("java.lang.Double")) {
                try {
                    double val = Double.parseDouble(objVal);
                    field.set(obj, val);
                } catch (Exception e) {
                    handleBadModelField(field.getName(), type.getName(), objVal);
                }
            } else if (type.getName().equals("boolean") || type.getName().equals("java.lang.Boolean")) {
                try {
                    boolean val = Boolean.parseBoolean(objVal);
                    field.set(obj, val);
                } catch (Exception e) {
                    handleBadModelField(field.getName(), type.getName(), objVal);
                }
            } else if (type.getName().equals("float") || type.getName().equals("java.lang.Float")) {
                try {
                    float val = Float.parseFloat(objVal);
                    field.set(obj, val);
                } catch (Exception e) {
                    handleBadModelField(field.getName(), type.getName(), objVal);
                }
            } else {
                NebulaLogger.warn(LOG_TAG, "Parameter " + field.getName() + " is not primitive type.");
            }
        } catch (Exception ex) {
            NebulaLogger.exception(LOG_TAG, ex);
        }
    }

    private static void handleBadModelField(String fieldName, String fieldType, String value) {
        NebulaLogger.warn(LOG_TAG, "Parameter " + fieldName + " cannot parse " + value + " to " + fieldType);
    }

    private static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString().replace("-", "");
        return randomUUIDString;
    }

    private static void log(String msg) {
        NebulaLogger.info(LOG_TAG, msg);
    }
}
