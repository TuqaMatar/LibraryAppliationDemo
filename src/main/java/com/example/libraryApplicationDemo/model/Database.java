package com.example.libraryApplicationDemo.model;
import com.fasterxml.jackson.databind.JsonNode;

public class Database {
    private String name;
    private JsonNode schema;

    public Database(String name, JsonNode schema) {
        this.name = name;
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonNode getSchema() {
        return schema;
    }

    public void setSchema(JsonNode schema) {
        this.schema = schema;
    }
}
