package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryRepository<ID, T extends Identifiable<ID>>
        implements Repository<ID, T> {

    private final List<T> values = new ArrayList<>();

    @Override
    public void save(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        // повторное сохранение с тем же ID заменяет старую сущность
        for (int i = 0; i < values.size(); i++) {
            if (Objects.equals(values.get(i).getId(), value.getId())) {
                values.set(i, value);
                return;
            }
        }
        values.add(value);
    }

    @Override
    public T findById(ID id) {
        for (T value : values) {
            if (Objects.equals(value.getId(), id)) {
                return value;
            }
        }
        throw new EntityNotFoundException("Entity with id " + id + " not found");
    }

    @Override
    public boolean existsById(ID id) {
        for (T value : values) {
            if (Objects.equals(value.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return values.size();
    }
}
