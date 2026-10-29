
package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import model.annotation.Id;

public class Repository<T> {

    private final HashMap<String, T> items = new HashMap<>();

    private String getId(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        Class<?> type = entity.getClass();

        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(entity);

                        if (value == null) {
                            throw new IllegalArgumentException(
                                    "@Id field cannot be null"
                            );
                        }

                        return value.toString();

                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(
                                "Cannot read @Id field", e
                        );
                    }
                }
            }

            type = type.getSuperclass();
        }

        throw new IllegalArgumentException(
                "No field marked with @Id found in "
                        + entity.getClass().getName()
        );
    }

    public T save(T entity) {
        String id = getId(entity);
        items.put(id, entity);
        return entity;
    }

    public T findById(String id) {
        return items.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(items.values());
    }

    public boolean delete(String id) {
        return items.remove(id) != null;
    }

    @SafeVarargs
    public final ArrayList<T> saveAll(T... entities) {
        ArrayList<T> saved = new ArrayList<>();

        for (T entity : entities) {
            saved.add(save(entity));
        }

        return saved;
    }

    public int count() {
        return items.size();
    }

    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(
                    "Array must not be empty"
            );
        }

        double sum = 0;

        for (N value : values) {
            sum += value.doubleValue();
        }

        return sum / values.length;
    }
}

