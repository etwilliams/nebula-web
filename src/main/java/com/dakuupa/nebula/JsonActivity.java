package com.dakuupa.nebula;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author etwilliams
 */
@ContentType(type = "application/json")
public abstract class JsonActivity<M extends Model> extends Activity<M> {

    @Override
    public void complete(String result) {

        String callbackName = null;
        for (Map.Entry<String, String[]> entry : http.getRequest().getParameterMap().entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue()[0];
            if (StringUtils.equals(name, "callback")) {
                if (StringUtils.isNotEmpty(value)) {
                    callbackName = value;
                }
                break;
            }
        }

        String modelString = "";

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.addMixInAnnotations(File.class, MixInForIgnoreType.class);
        try {
            modelString = mapper.writeValueAsString(model);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JsonActivity.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (callbackName != null) {
            http.write(callbackName + "(" + modelString + ");");
        } else {
            http.write(modelString);
        }

        http.writeFinal();

    }

    @JsonIgnoreType
    public class MixInForIgnoreType {
    }

}
