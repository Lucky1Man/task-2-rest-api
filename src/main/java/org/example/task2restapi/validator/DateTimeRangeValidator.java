package org.example.task2restapi.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;
import java.util.List;

public class DateTimeRangeValidator implements ConstraintValidator<PasswordFormat, ObjectWithDateTimeRanges> {
    @Override
    public boolean isValid(ObjectWithDateTimeRanges objectWithDateTimeRanges, ConstraintValidatorContext constraintValidatorContext) {
        List<DateTimeRange> validatedRanges = objectWithDateTimeRanges.getValidatedRanges();
        constraintValidatorContext.disableDefaultConstraintViolation();
        return validatedRanges.stream().anyMatch(range -> fromDateIsBeforeToDate(range, constraintValidatorContext));
    }

    private boolean fromDateIsBeforeToDate(DateTimeRange range, ConstraintValidatorContext ctx) {
        LocalDateTime from = range.getFrom();
        LocalDateTime to = range.getTo();
        if(from != null && to != null && from.isAfter(to)) {
            ctx.buildConstraintViolationWithTemplate(
                    "%s is before %s".formatted(range.getFromFieldName(), range.getToFieldName())
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
