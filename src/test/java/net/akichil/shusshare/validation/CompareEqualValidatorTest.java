package net.akichil.shusshare.validation;

import lombok.Data;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareEqualValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testNoViolations() {

        CompareEqualsValidationBean bean = new CompareEqualsValidationBean();
        bean.setValue1("value");
        bean.setValue2("value");

        Set<ConstraintViolation<CompareEqualsValidationBean>> violations = validator.validate(bean);

        assertEquals(0, violations.size());
    }

    @Test
    public void testViolations() {

        CompareEqualsValidationBean bean = new CompareEqualsValidationBean();
        bean.setValue1("value");
        bean.setValue2("xxxxxxxxx");

        Set<ConstraintViolation<CompareEqualsValidationBean>> violations = validator.validate(bean);

        assertEquals(1, violations.size());
        ConstraintViolation<CompareEqualsValidationBean> violation = violations.iterator().next();
        assertEquals("一致しません", violation.getMessage());
    }

    @Data
    @CompareEquals(value1 = "value1", value2 = "value2")
    class CompareEqualsValidationBean {

        private String value1;

        private String value2;

    }

}
