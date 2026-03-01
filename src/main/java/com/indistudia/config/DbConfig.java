package com.indistudia.config;

public record DbConfig(
        String dbUrl,
        String dbUsername,
        String dbPassword
) {
}
