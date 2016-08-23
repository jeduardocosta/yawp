package io.yawp.commons.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.owlike.genson.*;
import com.owlike.genson.stream.ObjectWriter;
import io.yawp.commons.utils.json.BaseGensonBundle;
import io.yawp.commons.utils.json.RawJsonWriter;
import io.yawp.repository.Repository;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private JsonUtils() {
    }

    private static Genson genson;

    static {
        genson = new GensonBuilder().withBundle(new BaseGensonBundle()).create();
    }

    public static <T> T from(Repository r, String json, Class<T> clazz) {
        return genson.deserialize(json, clazz);
    }

    public static Object from(Repository r, String json, Type type) {
        return genson.deserialize(json, GenericType.of(type));
    }

    public static <T> List<T> fromList(Repository r, String json, Class<T> clazz) {
        return (List<T>) fromListRaw(r, json, clazz);
    }

    @SuppressWarnings("unchecked")
    public static List<?> fromListRaw(Repository r, String json, Type valueType) {
        ParameterizedTypeImpl type = new ParameterizedTypeImpl(List.class, new Type[]{valueType}, null);
        return (List<?>) from(r, json, type);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> fromMap(Repository r, String json, Class<K> keyClazz, Class<V> valueClazz) {
        return (Map<K, V>) fromMapRaw(r, json, keyClazz, valueClazz);
    }

    public static Map<?, ?> fromMapRaw(Repository r, String json, Type keyType, Type valueType) {
        ParameterizedTypeImpl type = new ParameterizedTypeImpl(Map.class, new Type[]{keyType, valueType}, null);
        return (Map<?, ?>) from(r, json, type);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, List<V>> fromMapList(Repository r, String json, Class<K> keyClazz, Class<V> valueClazz) {
        Type listType = new ParameterizedTypeImpl(List.class, new Type[]{valueClazz}, null);
        Type type = new ParameterizedTypeImpl(Map.class, new Type[]{keyClazz, listType}, null);
        return (Map<K, List<V>>) from(r, json, type);
    }

    public static String to(Object o) {
        StringWriter sw = new StringWriter();
        ObjectWriter writer = createWriter(sw);

        if (o == null) {
            try {
                writer.writeNull();
                writer.flush();
            } catch (Exception e) {
                throw new JsonBindingException("Could not serialize null value.", e);
            }
        } else {
            genson.serialize(o, o.getClass(), writer, new Context(genson));
        }

        return sw.toString();
    }

    public static String readJson(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    public static boolean isJsonArray(String json) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        return json.charAt(0) == '[';
    }

    // TODO: remove gson support here
    public static List<String> getProperties(String json) {
        List<String> properties = new ArrayList<String>();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        for (Map.Entry<String, JsonElement> property : jsonObject.entrySet()) {
            properties.add(property.getKey());
        }
        return properties;
    }

    private static ObjectWriter createWriter(StringWriter sw) {
        return new RawJsonWriter(sw, genson.isSkipNull(), genson.isHtmlSafe(), false);
    }

}
