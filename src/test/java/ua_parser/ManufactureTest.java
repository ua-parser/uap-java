package ua_parser;

/**
 * Created by sdagostino on 2015-07-27.
 */
public class ManufactureTest extends DataTest<Manufacture> {
    @Override
    protected Manufacture getRandomInstance(long seed, StringGenerator g) {
        random.setSeed(seed);
        String name = g.getString(256);
        return new Manufacture(name);
    }

    @Override
    protected Manufacture getBlankInstance() {
        return new Manufacture(null);
    }
}
