package it.impo.defaultProject.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static Component parse(String message){
        if (message == null) return Component.empty();
        message = translateHexColorCodes(message);
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public static String toLegacy(Component component){
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static String parseToLegacy(String message){
        return toLegacy(parse(message));
    }

    private static String translateHexColorCodes(String message){
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while(matcher.find()){
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "§x"
                    + "§" + group.charAt(0) + "§" + group.charAt(1)
                    + "§" + group.charAt(2) + "§" + group.charAt(3)
                    + "§" + group.charAt(4) + "§" + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString().replace("&", "§");
    }
}
