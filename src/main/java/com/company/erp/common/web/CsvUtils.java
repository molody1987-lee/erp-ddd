package com.company.erp.common.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 轻量 CSV 工具（用于导入/导出，避免引入额外依赖）。
 */
public final class CsvUtils {

    private CsvUtils() {
    }

    /**
     * 读取 CSV（含表头），返回去除表头后的数据行。
     */
    public static List<String[]> read(InputStream in) throws IOException {
        String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        if (content.startsWith("\uFEFF")) {
            content = content.substring(1);
        }
        List<String[]> rows = new ArrayList<>();
        boolean headerSkipped = false;
        for (String line : content.split("\\r?\\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (!headerSkipped) {
                headerSkipped = true;
                continue;
            }
            rows.add(split(trimmed));
        }
        return rows;
    }

    /**
     * 渲染为 CSV 字节流（每行数组拼接逗号，末尾换行）。
     */
    public static byte[] render(List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : rows) {
            sb.append(String.join(",", row)).append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String[] split(String line) {
        String[] parts = line.split(",", -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
}