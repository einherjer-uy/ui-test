package org.einherjer.twitter.tickets.model;

import java.util.HashMap;

public class SimpleJson extends HashMap<String, Object> {

    public SimpleJson append(String key, Object value) {
        this.put(key, value);
        return this;
    }

}
