package Util;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JsonReader {

    public static String getJsonData(String key) throws IOException, ParseException {
        return (String) getJsonObject().get(key);
    }

    public static JSONObject getJsonObject() throws IOException, ParseException {
        String jsonFilePath = "Resources/TestData/testdata.json";

        File jsonFile = new File(jsonFilePath);
        String stringJsonFile = FileUtils.readFileToString(jsonFile);
        Object json = new JSONParser().parse(stringJsonFile);
        return (JSONObject) json;
    }

    public static Map<String,String> getJsonMap(String key) throws IOException, ParseException {
        JSONObject parent = getJsonObject();
        JSONObject child = (JSONObject) getJsonObject().get(key);

        Map<String,String> map = new HashMap<>();
        for(Object k : child.keySet())
        {
            String mapKey = (String) k;
            String mapValue = (String) child.get(k);
            map.put(mapKey,mapValue);
        }
        return map;
    }


}
