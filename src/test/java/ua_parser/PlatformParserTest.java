package ua_parser;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by sdagostino on 2015-07-27.
 */
public class PlatformParserTest  {

    @Test
    public void testDesktopPlatform() {
        String desktopUA = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; fr; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5,gzip(gfe),gzip(gfe)";

        final Platform desktopPlatform = getPlatformFromUA(desktopUA);
        final Platform expected = new Platform(Constants.DESKTOP);

        assertThat(desktopPlatform, is(expected));

    }

    @Test
    public void testMobilePlatform() {
        String mobileUA = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

        final Platform mobilePlatform = getPlatformFromUA(mobileUA);
        final Platform expected = new Platform(Constants.MOBILE);

        assertThat(mobilePlatform, is(expected));
    }

    @Test
    public void testTabletPlatform() {
        String tabletUA = "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25";
        final Platform tablePlatform = getPlatformFromUA(tabletUA);
        final Platform expected = new Platform(Constants.TABLET);

        assertThat(tablePlatform, is(expected));
    }

    private Platform getPlatformFromUA(String ua) {
        final PlatformParser platformParser = new PlatformParser(
            DeviceList.getPhoneDeviceList(),
            DeviceList.getTabletDeviceList());

        return  platformParser.parse(ua);
    }
}
