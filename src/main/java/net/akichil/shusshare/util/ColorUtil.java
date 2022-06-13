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
        int randomValue = random.nextInt(0xffffff);
        String hex = String.format("%06x", randomValue);
        return "#" + hex;
    }

}
