package schnapsen.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;

public class Utils {
    public static void saveObjectAsJSON(File file, Object object) throws IOException {
        Files.createDirectories(Paths.get(file.getParentFile().getPath()));
        Files.writeString(file.toPath(), new Gson().toJson(object));
    }

    public static <T> T loadObjectFromJSON(File file, Class<T> clazz) throws IOException {
        return new Gson().fromJson(Files.readString(file.toPath()), clazz);
    }

    public static <T> List<T> loadListFromJSON(File file, Class<T> clazz) throws IOException {
        return new Gson().fromJson(Files.readString(file.toPath()), new TypeToken<List<T>>() {
        }.getType());
    }

    public static String getTimestamp() {
        return "[" + new Timestamp(System.currentTimeMillis()).toString().substring(11, 19) + "]";
    }
}
