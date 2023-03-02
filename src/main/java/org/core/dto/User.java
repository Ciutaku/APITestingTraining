package org.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.core.enums.Gender;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

    @JsonProperty
    private int age;

    @JsonProperty
    private String name;

    @JsonProperty
    private Gender sex;

    @JsonProperty
    private String zipCode;
}