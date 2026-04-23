package com.example.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KodeGenerator {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private KodeGenerator() {}

    public static String generateBarangKode(List<String> existingKodes) {
        int max = 0;
        for (String k : existingKodes) {
            if (k != null && k.startsWith("BR-")) {
                try {
                    int n = Integer.parseInt(k.substring(3));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("BR-%03d", max + 1);
    }

    public static String generateTrxId(List<String> existingIds, LocalDateTime now) {
        String today = DATE_FMT.format(now);
        String prefix = "TRX-" + today + "-";
        int max = 0;
        for (String id : existingIds) {
            if (id != null && id.startsWith(prefix)) {
                try {
                    int n = Integer.parseInt(id.substring(prefix.length()));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return prefix + String.format("%03d", max + 1);
    }
}
