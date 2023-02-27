package org.core.dto;

import lombok.Data;

@Data
public class ResponseEntity<T> {

    private int statusCode;
    private T body;
}
