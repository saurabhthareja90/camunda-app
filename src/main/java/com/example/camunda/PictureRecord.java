package com.example.camunda;

import java.io.Serializable;

public record PictureRecord(
        String animal,
        String url,
        String timestamp) implements Serializable {
}
