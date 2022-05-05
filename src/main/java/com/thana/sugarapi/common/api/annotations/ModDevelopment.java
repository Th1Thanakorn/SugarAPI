package com.thana.sugarapi.common.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This API will mark your code points as "DevelopmentOnly"
 * Which means this API only work in development environment {@link net.minecraftforge.fml.loading.FMLEnvironment}
 * You need to manually create attached API for it {@link DevelopmentChecker} by calling DevelopmentChecker.check()
 * @author Thana, HTMLChannel
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ModDevelopment {
}
