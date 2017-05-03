import java.io.*;

public class FilesManager {

    private static final int EOF = -1;

    public static byte[] readFile(File f) throws FileNotFoundException {
        byte[] buffer = new byte[(int) f.length()];
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(f))) {
            reader.read(buffer);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("файл не найден");
        } catch (IOException e) {
            throw new FileNotFoundException("ошибка ввода/вывода");
        }
        return buffer;
    }

    public static void writeFile(String pathFile, byte[] data) {
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(pathFile))) {
            writer.write(data);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка ввода\\вывода");
        }
    }
}