package org.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.core.enums.Gender;

@Data
public class User {

    @JsonProperty("age")
    private int age;

    @JsonProperty("name")
    private String name;

    @JsonProperty("sex")
    private Gender sex;

    @JsonProperty("zipCode")
    private String zipCode;
}