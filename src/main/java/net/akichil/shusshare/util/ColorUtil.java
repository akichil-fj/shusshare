package net.akichil.shusshare.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ColorUtil {

    public static String code = "#ffffff";

    /**
     * 文字列からランダムにカラーコードを生成する
     * @param string コードを生成する文字列
     * @return 16進カラーコード
     */
    public static String generateColorCode(String string) {
        byte[] bytes = string.getBytes();
        long value = 0;
        for (byte b: bytes)
            value += b;

        Random random = new Random(value);
        int r = random.nextInt(0x9f) + 0x60;
        int g = random.nextInt(0x9f) + 0x60;
        int b = random.nextInt(0x9f) + 0x60;
        int sum = r * 0x10000 + g * 0x100 + b;
        String hex = String.format("%06x", sum);
        return "#" + hex;
    }

}
