package edu.istu.freeart.util;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JsonMaker<T> {

    public JSONObject convertToJson(T entity, String... fieldNames)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = entity.getClass();
        JSONObject json = new JSONObject();

        for (String fieldName : fieldNames) {
            final String getterName = "get" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
            final Method method = clazz.getMethod(getterName);
            final Object value = method.invoke(entity);
            json.put(fieldName, value);
        }

        return json;
    }

}
