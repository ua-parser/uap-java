package ua_parser;

/**
 * Created by sdagostino on 2015-07-27.
 */
public class PlatformTest extends DataTest<Platform> {
    @Override
    protected Platform getRandomInstance(long seed, StringGenerator g) {
        random.setSeed(seed);
        String name = g.getString(256);
        return new Platform(name);
    }

    @Override
    protected Platform getBlankInstance() {
        return new Platform(null);
    }
}
