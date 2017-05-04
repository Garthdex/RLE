import java.util.ArrayList;
import java.util.List;

public class Archiver {

    private static byte[] toArrayFromList(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    public static byte[] compressed(byte[] buffer) {
        return compressedRLE(buffer);
    }

    public static byte[] deCompressed(byte[] buffer) {
        return deCompressedRLE(buffer);
    }

    private static byte[] compressedRLE(byte[] buffer) {
        List<Byte> compressed = new ArrayList<>();
        List<Byte> unequalSequence = new ArrayList<>();
        int countEqualValues = 1;
        int countUnequalValues = 1;
        for (int i = 0; i < buffer.length - 1; i++) {
            if (i < buffer.length && buffer[i] == buffer[i + 1]) {
                countEqualValues++;
                if (countUnequalValues > 1) {
                    compressed.add((byte) ((countUnequalValues - 1) * -1)); // запись количества неодинаковых чисел
                    compressed.addAll(unequalSequence); // коллекция неодинаковых чисел(при этом не учитывается символ buffer[i + 1])
                }
                countUnequalValues = 0;
                if (countEqualValues == 130) {
                    compressed.add((byte) (countEqualValues - 3)); // предел 127 символов
                    compressed.add(buffer[i]); // записали сам символ
                    countEqualValues = 1; // нет одинаковой последовательности
                    countUnequalValues = 1; // есть неодинаковый символ
                }
                unequalSequence.clear(); // обнуляем коллекцию неодинаковых символов
            } else if (i < buffer.length && buffer[i] != buffer[i + 1]) {
                countUnequalValues++;
                if (countUnequalValues != 1)
                    unequalSequence.add(buffer[i]); // добавление байта неодинаковой последовательности
                if (countUnequalValues == 129) {
                    compressed.add((byte) ((countUnequalValues - 1) * -1)); // предел -128
                    compressed.addAll(unequalSequence); // записывает 128 байтов
                    unequalSequence.clear(); // очищаем коллекцию
                    countUnequalValues = 1; // пока что есть неодинаковый символ(нам не ясно, что будет дальше)
                }
                if (countEqualValues != 1) { // если все же были одинаковые
                    compressed.add((byte) (countEqualValues - 2)); // количество одинаковых символов(на 2 меньше)
                    compressed.add(buffer[i]); // записали этот символ
                }
                countEqualValues = 1; // нет одинаковой последовательности
            }
        }
        if (countEqualValues != 1) {
            compressed.add((byte) (countEqualValues - 2));
            compressed.add(buffer[buffer.length - 1]); // вытащить последний символ из массива
        }
        if (countUnequalValues > 0) {
            compressed.add((byte) (countUnequalValues * -1)); //-8 запись кол-ва неодинаковых символов
            compressed.addAll(unequalSequence); //7 записываем коллекцию неодинаковых символов
            compressed.add(buffer[buffer.length - 1]); //1 записываем последний символ(его нет в коллекции)
        }

        return toArrayFromList(compressed);
    }

    private static byte[] deCompressedRLE(byte[] buffer) {
        List<Byte> decompressed = new ArrayList<>();
        int iter = 0; // указатель на место
        while(iter < buffer.length) {
            Byte aByte = buffer[iter]; // хранит кол-во символов, счетчик
            iter++;
            if (iter == buffer.length) {
                decompressed.add(aByte);
                continue;
            }
            if (aByte >= 0) { // одинаковая последовательность, положительное число
                int finish = aByte + 2; // количество повторений след. символа(0 это 2(парный символ))
                for (int i = 0; i < finish; i++) {
                    decompressed.add(buffer[iter]); // запись символа
                }
                iter++;
            }
            if (aByte < 0) { // неодинаковая последовательность, отрицательные числа
                for (int i = iter; i < iter + (aByte * -1); i++) {
                    decompressed.add(buffer[i]); // запись символа
                }
                iter += (aByte * -1); // количество символов которое считал до этого цикл
            }
        }

        return toArrayFromList(decompressed);
    }
}
