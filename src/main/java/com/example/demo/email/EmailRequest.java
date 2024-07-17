package com.example.demo.email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailRequest {
    private String email;

    public EmailRequest() {
    }

    @JsonCreator
    public EmailRequest(@JsonProperty("email") String email) {
        this.email = email;
    }

}
