package ua_parser;


import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by sdagostino on 2015-07-27.
 */
public class ManufactureParserTest {

    @Test
    public void testAppleManufacture() {
        String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; fr; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5,gzip(gfe),gzip(gfe)";

        final Manufacture testManufacture = getManufactureFromUA(userAgent);
        final Manufacture expected = new Manufacture(Constants.APPLE);

        assertThat(testManufacture, is(expected));
    }

    @Test
    public void testMicrosoftManufacture() {
        String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

        final Manufacture testManufacture = getManufactureFromUA(userAgent);
        final Manufacture expected = new Manufacture(Constants.MICROSOFT);

        assertThat(testManufacture, is(expected));
    }

    @Test
    public void testGoogleManufacture() {
        String userAgent = "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

        final Manufacture testManufacture = getManufactureFromUA(userAgent);
        final Manufacture expected = new Manufacture(Constants.GOOGLE);

        assertThat(testManufacture, is(expected));
    }

    @Test
    public void testLinuxManufacture() {
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36";

        final Manufacture testManufacture = getManufactureFromUA(userAgent);
        final Manufacture expected = new Manufacture(Constants.LINUX);

        assertThat(testManufacture, is(expected));
    }

    private Manufacture getManufactureFromUA(String ua) {
        final ManufactureParser manufactureParser = new ManufactureParser();
        return manufactureParser.parse(ua);
    }
}
