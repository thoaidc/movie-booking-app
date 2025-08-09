package vn.ptit.model.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Generic class for handling json with file and string
 * @author thoaidc
 */
@SuppressWarnings("unused")
public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    // Configure ObjectMapper to serialize null fields and format JSON with pretty printing
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true);

    /**
     * Read an object from json file
     * @param filePath Path to json file
     * @param className Class of the object to be converted from json data
     * @return Object corresponding to json data or null on error
     * @param <T> Generics type
     */
    public static <T> T readJsonFromFile(String filePath, Class<T> className) {
        try {
            return objectMapper.readValue(new File(filePath), className);
        } catch (JsonProcessingException e) {
            log.error("[INVALID_JSON_FORMAT] - When read Object from file: {}, {}", filePath, e.getMessage());
        } catch (IOException e) {
            log.error("[READ_JSON_ERROR] - Cannot read Object data from file: {}. {}", filePath, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * Read an array from json file
     * @param filePath Path to json file
     * @param className Class of the object to be converted from json data
     * @return List of objects which corresponds to json data or an empty list if an error occurs
     * @param <T> Generics type
     */
    public static <T> List<T> readJsonArrayFromFile(String filePath, Class<T> className) {
        try {
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            List<T> results = new ArrayList<>();

            // Check if the JSON is an array or a single object
            if (jsonNode.isArray()) {
                // Parse json data as list
                TypeReference<List<T>> typeRef = new TypeReference<>() {};
                results = objectMapper.convertValue(jsonNode,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, className));
            } else if (jsonNode.isObject()) {
                // Parse json data as an object then add to list results
                T object = objectMapper.treeToValue(jsonNode, className);

                if (Objects.nonNull(object))
                    results.add(object);
            }

            return results;
        } catch (JsonProcessingException e) {
            log.error("[INVALID_JSON_FORMAT] - When read Array from file: {}, {}", filePath, e.getMessage());
        } catch (IOException e) {
            log.error("[READ_JSON_ERROR] - Cannot read Array from file: {}. {}", filePath, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new ArrayList<>();
    }

    /**
     * Delete old data in file and overwrite a new json object
     * @param filePath Path to json file
     * @param jsonObject Data to be written to file
     */
    public static void writeJsonToFile(String filePath, Object jsonObject) {
        try {
            objectMapper.writeValue(new File(filePath), jsonObject);
        } catch (IOException e) {
            log.error("[WRITE_JSON_ERROR] - Cannot write JSON object to file: {}", filePath, e);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Parse JSON content into a specific class type.
     *
     * @param jsonString  JSON string to parse
     * @param className Class to parse into
     * @param <T>         Type of the desired object
     * @return Parsed object of type T or null on error
     */
    public static <T> T parseJson(String jsonString, Class<T> className) {
        if (StringUtils.hasText(jsonString)) {
            try {
                return objectMapper.readValue(jsonString, className);
            } catch (JsonProcessingException e) {
                log.error("[INVALID_JSON_FORMAT] - When parse from string: {}, {}", jsonString, e.getMessage());
            }
        }

        return null;
    }

    /**
     * Convert object to json String
     * @param object data to convert
     * @return A json string or empty on error
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("[WRITE_JSON_ERROR] - Cannot convert object to JSON string: {}", e.getMessage());
        }

        return "";
    }
}
