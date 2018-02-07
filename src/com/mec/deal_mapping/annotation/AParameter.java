package com.mec.deal_mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记法方法的参数，因为通过反射无法得到class方法变量名,因为class文件的方法参数名为简写形式，如arg0。
 * 
 * @author xl
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AParameter {
	String value() default "";
}
