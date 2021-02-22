package ua_parser;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LegacyParserTest extends ParserTestBase {

    @Override
    public String resourcePath() {
        return "/ua_parser/legacy/";
    }

    @Override
    public void initParser() {
        parser = Parser.newLegacyParser();
    }

    @Test
    public void testParseAll() {
        String agentString1 = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; fr; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5,gzip(gfe),gzip(gfe)";
        String agentString2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

        Client expected1 = new Client(new UserAgent("Firefox", "3", "5", "5"),
                                      new OS("Mac OS X", "10", "4", null, null),
                                      new Device("Other", null, null));
        Client expected2 = new Client(new UserAgent("Mobile Safari", "5", "1", null),
                                      new OS("iOS", "5", "1", "1", null),
                                      new Device("iPhone", "Apple", "iPhone"));

        assertThat(parser.parse(agentString1), is(expected1));
        assertThat(parser.parse(agentString2), is(expected2));
    }
}
