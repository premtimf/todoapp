package com.amigox.todo.api;

import com.google.gson.JsonElement;
import lombok.Data;

@Data

public class StandardResponse {

        private StatusResponse status;
        private String message;
        private JsonElement data;

    public StandardResponse(StatusResponse status) {
        this.status = status;
    }

    public StandardResponse(StatusResponse status, String message) {
        this.status = status;
        this.message = message;
    }

    public StandardResponse(StatusResponse status, String message, JsonElement data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


}
