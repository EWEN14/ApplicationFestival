package com.example.applicationfestival;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InfosGroupe {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private DataInfoGroupe data;

    public  InfosGroupe() {}

    public InfosGroupe(String code, String message, DataInfoGroupe data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataInfoGroupe getData() {
        return data;
    }

    public void setData(DataInfoGroupe data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "InfosGroupe{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
