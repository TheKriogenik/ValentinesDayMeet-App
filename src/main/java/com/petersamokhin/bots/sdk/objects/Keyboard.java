package com.petersamokhin.bots.sdk.objects;

import org.json.JSONObject;

import java.util.*;

public class Keyboard {

    boolean oneTime = false;
//    List<MessageButton> buttons = new ArrayList<>();

    Map<Integer, List<Button>> buttons = new TreeMap();


    public Keyboard() {

    }

    public Keyboard setOneTime(boolean one_time) {
        this.oneTime = one_time;
        return this;
    }

    public Keyboard addButtons(int line, Button bnt) {
        List<Button> bnts;
        if (buttons.containsKey(line)) {
            bnts = buttons.get(line);
        } else {
            bnts = new ArrayList<>();
            buttons.put(line, bnts);
        }
        bnts.add(bnt);
        return this;
    }

    public JSONObject getJson() {
        JSONObject me = new JSONObject();
        me.put("one_time", oneTime);
        if (buttons.size() > 0) {
            List<List<JSONObject>> bnts = new ArrayList<>();
            buttons.forEach((key, val) -> {
                List l = Arrays.asList(val.stream().map(Button::getJSON).toArray());
                bnts.add(l);
            });
            me.put("buttons", bnts);
        }
        return me;
    }

    public Map<Integer, List<Button>> getButtons() {
        return buttons;
    }
}
