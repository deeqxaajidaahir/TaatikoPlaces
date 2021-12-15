package com.taatiko.places;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class JsonObjectModalResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private JsonArray record;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public JsonArray getRecord() {
        return record;
    }

    public void setRecord(JsonArray record) {
        this.record = record;
    }
}
