package ua_parser;

import java.util.Map;

/**
 * Platform parsed data class
 * @author Salvatore D'Agostino (@iToto)
 */
public class Platform {
    public final String name;

    public Platform(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Platform)) return false;

        final Platform o = (Platform) other;
        return ((this.name != null) && this.name.equals(o.name)) || this.name == o.name;
    }

    public static Platform fromMap(Map<String, String> map) {
        return new Platform(map.get("platform"));
    }

    @Override
    public int hashCode() {
        final int h = name == null ? 0 : name.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return String.format("{\"platform\": %s}",
            name == null ? Constants.EMPTY_STRING : '"' + name + '"'
            );
    }
}
