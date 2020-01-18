package com.petersamokhin.bots.sdk.objects;

import org.json.JSONObject;

public class Button {

    String payLoad, label;
    Color color = Color.Default;

    public Button() {

    }

    public Button setColor(Color color) {
        this.color = color;
        return this;
    }

    public Button setLabel(String label) {
        this.label = label;
        return this;
    }

    public Button setPayLoad(String payLoad) {
        this.payLoad = payLoad;
        return this;
    }


    public JSONObject getJSON() {
        JSONObject button = new JSONObject();
        JSONObject action = new JSONObject();
        action.put("type", "text");

        if (payLoad != null) {
            JSONObject payload = new JSONObject();
            payload.put("button", payLoad);
            action.put("payload", payload.toString());
        }
        action.put("label", label);

        button.put("action", action);
        button.put("color", color.toString());
        return button;
    }
}
