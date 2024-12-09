package com.example.ticketbookingsystem.validator;

import com.example.ticketbookingsystem.dto.UserDto;

public class RegisterUserValidator implements Validator<UserDto> {
    private static final RegisterUserValidator INSTANCE = new RegisterUserValidator();

    @Override
    public ValidationResult isValid(UserDto userDto) {
        var validationResult = new ValidationResult();
        String password = userDto.getPassword();
        String confirmPassword = userDto.getConfirmPassword();
        if (password == null || password.length() < 8) {
            validationResult.add(Error.of("short.password",
                    "Пароль должен содержать не менее 8 символов!"));
        } else if (!password.equals(confirmPassword)) {
            validationResult.add(Error.of("confirm.password.fail",
                    "Пароли не совпадают!"));
        } else {
            validatePasswordComplexity(password, validationResult);
        }
        return validationResult;
    }

    private void validatePasswordComplexity(String password, ValidationResult validationResult) {
        if (!password.matches(".*[A-Z].*")) {
            validationResult.add(Error.of("missing.uppercase",
                    "Пароль должен содержать хотя бы одну заглавную букву!"));
        } else if (!password.matches(".*[a-z].*")) {
            validationResult.add(Error.of("missing.lowercase",
                    "Пароль должен содержать хотя бы одну строчную букву!"));
        } else if (!password.matches(".*\\d.*")) {
            validationResult.add(Error.of("missing.digit",
                    "Пароль должен содержать хотя бы одну цифру!"));
        } else if (!password.matches(".*[!@#$%^&*()-_+=<>?].*")) {
            validationResult.add(Error.of("missing.special",
                    "Пароль должен содержать хотя бы один специальный символ!"));
        }
    }

    public static RegisterUserValidator getInstance(){
        return INSTANCE;
    }

    private RegisterUserValidator(){}

}