package com.library.api.util;

public enum UserRole {
    ALUNO("aluno"),
    PROFESSOR("professor");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
