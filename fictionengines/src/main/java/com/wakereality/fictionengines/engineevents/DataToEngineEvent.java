package com.wakereality.fictionengines.engineevents;

import org.json.JSONObject;

/**
 * Created by Stephen A. Gutknecht on 8/22/17.
 */

public class DataToEngineEvent {
    final public JSONObject dataJSON;
    public DataToEngineEvent(JSONObject validatedJSON) {
        dataJSON = validatedJSON;
    }
}
