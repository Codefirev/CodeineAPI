package rip.kirooh.command;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMeta {
    String[] label();

    String[] options() default {};

    String permission() default "";

    String description() default "";

    boolean autoAddSubCommands() default true;

    boolean async() default false;
}
