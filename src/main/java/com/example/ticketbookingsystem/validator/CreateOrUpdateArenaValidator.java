package com.example.ticketbookingsystem.validator;

import com.example.ticketbookingsystem.entity.Arena;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Validator class for validating the creation or update of {@link Arena} entities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrUpdateArenaValidator implements Validator<Arena>{
    private static final CreateOrUpdateArenaValidator INSTANCE = new CreateOrUpdateArenaValidator();

    /**
     * Validates the given {@link Arena} entity.
     * Checks if the capacity is within the valid range.
     * @param arena the arena entity to validate
     * @return the validation result containing any validation errors
     */
    @Override
    public ValidationResult isValid(Arena arena) {
        var validationResult = new ValidationResult();
        if(arena.getCapacity() < 1 || arena.getCapacity() > 22000){
            validationResult.add(Error.of("invalid.capacity",
                    "Проверьте корректность ввода данных значения вместимости " +
                            "(допускается вводить только целые числа в диапазоне от 1 до 22000)!"));
        }

        return validationResult;
    }

    /**
     * Gets the singleton instance of {@link CreateOrUpdateArenaValidator}.
     * @return the singleton instance
     */
    public static CreateOrUpdateArenaValidator getInstance(){
        return INSTANCE;
    }
}
