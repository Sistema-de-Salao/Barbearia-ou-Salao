package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO<T extends Serializable> {
    private final String filePath;

    public BaseDAO(String fileName) {
        this.filePath = "data/" + fileName;
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void saveAll(List<T> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
