package tools.ui.util;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ViewUtil {

    
    
    private static final String PARAM_ITEM_VALUE_SEPARATOR = "=";
    private static final String PARAM_SEPARATOR = "/";

    public static final String CATEGORY_PARAM_KEY = "category";
    public static final String BUILD_PARAM_KEY = "build";
    
    /**
     * Serializes the map into a String. Use {@link #stringToMap(String)} to convert back from the string
     * to a map. Supports Basic data type by using toString on them.
     *
     * @param mapToSerialize
     *            with key being the param name and value being the value of the param
     *
     * @return String representation of the map
     */
    public static String mapToString(Map<String, Object> mapToSerialize) {
        StringBuffer serializedString = new StringBuffer();
        for (Entry<String, Object> entry : mapToSerialize.entrySet()) {
            serializedString.append(PARAM_SEPARATOR);
            serializedString.append(entry.getKey());
            serializedString.append(PARAM_ITEM_VALUE_SEPARATOR);
            serializedString.append(entry.getValue().toString());
        }
        return serializedString.toString();
    }

    /**
     * Deserializes the String serialized by {@link #mapToString(Map)} into a map.
     * Supports Basic data type by using toString on them.
     *
     * @param serializedMap
     *            serialized Map
     *
     * @return Map the map of param:value
     */
    public static Map<String, String> stringToMap(String serializedMap) {
        HashMap<String, String> paramMap = new HashMap<>();
        for (String param : serializedMap.split(PARAM_SEPARATOR)) {
            if (param.contains(PARAM_ITEM_VALUE_SEPARATOR)) {
                String[] paramArray = param.split(PARAM_ITEM_VALUE_SEPARATOR);
                paramMap.put(paramArray[0], paramArray[1]);
            }
        }
        return paramMap;
    }

}