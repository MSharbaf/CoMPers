package org.eclipse.emfcloud.ecore.modelserver;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.eclipse.glsp.graph.impl.*;


public class GPointImplInstanceTypeAdapter implements JsonDeserializer<GPointImpl>, JsonSerializer<GPointImpl> {


    @Override
    public GPointImpl deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    	GPointImpl impl = new GPointImpl();
        JsonObject object = json.getAsJsonObject();
        impl.setX(object.get("x").getAsDouble());
        impl.setY(object.get("y").getAsDouble());       
        return impl;
    }

    @Override
    public JsonElement serialize(GPointImpl src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }
}
