
package util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import model.annotation.MaxLength;
import model.annotation.Positive;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        ArrayList<String> errors = new ArrayList<>();

        if (obj == null) {
            errors.add("Object cannot be null");
            return errors.toArray(String[]::new);
        }

        Field[] fields = obj.getClass().getDeclaredFields();

if (obj instanceof model.Account) {
    fields = model.Account.class.getDeclaredFields();
}

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);

                if (field.isAnnotationPresent(Positive.class)) {
                    Positive annotation =
                            field.getAnnotation(Positive.class);

                    if (value instanceof Number
                            && ((Number) value).doubleValue() <= 0) {
                        errors.add(field.getName() + ": " + annotation.message());
                    }
                }

                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength annotation =
                            field.getAnnotation(MaxLength.class);

                    if (value instanceof String
                            && ((String) value).length()
                            > annotation.value()) {
                        errors.add(field.getName() + ": maximum length is "
                + annotation.value());
                    }
                }

            } catch (IllegalAccessException e) {
                errors.add("Unable to validate field: "
                        + field.getName());
            }
        }

        return errors.toArray(String[]::new);
    }
}
