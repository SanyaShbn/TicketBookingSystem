package com.example.ticketbookingsystem.validator;

import com.example.ticketbookingsystem.entity.Arena;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrUpdateArenaValidator implements Validator<Arena>{
    private static final CreateOrUpdateArenaValidator INSTANCE = new CreateOrUpdateArenaValidator();
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

    public static CreateOrUpdateArenaValidator getInstance(){
        return INSTANCE;
    }
}
