package com.jobhelper.careerflowapi.global.docs;

import com.jobhelper.careerflowapi.global.docs.errors.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CommonUnAuthorizedResponseDocs
@CommonForbiddenResponseDocs
@CommonBadRequestResponseDocs
@CommonInternalServerErrorResponseDocs
@CommonNotFoundResponseDocs
public @interface CommonReadErrorDocs {
}
