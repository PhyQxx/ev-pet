package com.evpet.utils;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ContentFilterUtil {

    // Basic sensitive word patterns (simplified for demo)
    private static final Set<String> BLOCKED_WORDS = new HashSet<>(Arrays.asList(
            "赌博", "毒品", "暴力", "色情", "政治"
    ));

    private static final Pattern PATTERN_BLOCKED = Pattern.compile(
            ".*(" + String.join("|", BLOCKED_WORDS) + ").*"
    );

    /**
     * Filter user input content
     * @return true if content is safe, false if blocked
     */
    public boolean isContentSafe(String content) {
        if (content == null || content.trim().isEmpty()) {
            return true;
        }
        return !PATTERN_BLOCKED.matcher(content).matches();
    }

    /**
     * Sanitize content for display
     */
    public String sanitize(String content) {
        if (content == null) {
            return "";
        }
        // Remove potential injection characters
        return content.replaceAll("[<>\"']", "");
    }
}
