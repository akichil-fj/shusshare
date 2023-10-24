package net.akichil.shusshare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;

public class CompareEqualsValidator implements ConstraintValidator<CompareEquals, Object> {

    private String value1;
    private String value2;
    private String message;

    @Override
    public void initialize(CompareEquals constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        //　アノテーションの引数情報を設定する。
        this.value1 = constraintAnnotation.value1();
        this.value2 = constraintAnnotation.value2();
        this.message = constraintAnnotation.message();
    }

    /**
     * 2つのオブジェクトが一致するか調べる
     *
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 値を取得
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Object value1 = beanWrapper.getPropertyValue(this.value1);
        Object value2 = beanWrapper.getPropertyValue(this.value2);

        //　比較処理
        //　同値でも型が違う場合はfalseを返却する。
        boolean matched = ObjectUtils.nullSafeEquals(value1, value2);

        if (matched) {
            return true;
        }

        //　メッセージを設定
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addPropertyNode(this.value2).addConstraintViolation();
        return false;
    }
}
