package com.liteThinking.products.dto;

public record TokenResponse(
    String token,
    String tipo,
    String email
) {}
