package com.example.ticketbookingsystem.validator;

import lombok.Value;

/**
 * Class to describe an occurred error
 */
@Value(staticConstructor = "of")
public class Error {
    String code;
    String defaultMessage;
}
