package com.d2d.elastic.validator;


import com.d2d.elastic.metadata.PublicationYear;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Year;

public class PublicationYearValidator implements ConstraintValidator<PublicationYear, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return !Year.of(value).isAfter(Year.now());
    }
}