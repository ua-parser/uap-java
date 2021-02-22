package ua_parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * These tests really only redo the same tests as in ParserTest but with a
 * different Parser subclass Also the same tests will be run several times on
 * the same user agents to validate the caching works correctly.
 *
 * @author niels
 *
 */
public class CachingParserTest extends CurrentParserTest {

  @Override
  public String resourcePath() {
    return "/ua_parser/current/";
  }

  @Before
  public void initParser() {
    parser = CachingParser.newCachingParser();
  }

  @Override
  Parser parserFromStringConfig(String configYamlAsString) throws Exception {
    InputStream yamlInput = new ByteArrayInputStream(
        configYamlAsString.getBytes("UTF8"));
    return new CachingParser(yamlInput);
  }

  @Test
  public void testCachedParseUserAgent() {
    super.testParseUserAgent();
    super.testParseUserAgent();
    super.testParseUserAgent();
  }

  @Test
  public void testCachedParseOS() {
    super.testParseOS();
    super.testParseOS();
    super.testParseOS();
  }

  @Test
  public void testCachedParseAdditionalOS() {
    super.testParseAdditionalOS();
    super.testParseAdditionalOS();
    super.testParseAdditionalOS();
  }

  @Test
  public void testCachedParseDevice() {
    super.testParseDevice();
    super.testParseDevice();
    super.testParseDevice();
  }

  @Test
  public void testCachedParseFirefox() {
    super.testParseFirefox();
    super.testParseFirefox();
    super.testParseFirefox();
  }

  @Test
  public void testCachedParsePGTS() {
    super.testParsePGTS();
    super.testParsePGTS();
    super.testParsePGTS();
  }

  @Test
  public void testCachedParseAll() {
    testParseAll();
    testParseAll();
    testParseAll();
  }

  @Test
  public void testCachedReplacementQuoting() throws Exception {
    super.testReplacementQuoting();
    super.testReplacementQuoting();
    super.testReplacementQuoting();
  }

}
