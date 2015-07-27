package ua_parser;

/**
 * Created by sdagostino on 2015-07-27.
 */
public class Manufacture {
    public final String name;

    public Manufacture(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Manufacture)) return false;

        final Manufacture o = (Manufacture) other;
        return ((this.name != null) && this.name.equals(o.name)) || this.name == o.name;
    }

    @Override
    public int hashCode() {
        final int h = name == null ? 0 : name.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return String.format("{\"manufacure\": %s", name == null? Constants.EMPTY_STRING : '"' + name + '"');
    }
}
