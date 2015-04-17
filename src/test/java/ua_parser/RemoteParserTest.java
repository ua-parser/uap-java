package ua_parser;

import org.junit.Test;

public class RemoteParserTest {

	private static final String REMOTE_URL = "https://raw.githubusercontent.com/ua-parser/uap-core/master/regexes.yaml";
	
	@Test
	public void testRemoteParserConstruct(){
		new Parser(REMOTE_URL);
	}
	
	@Test
	public void testRemoteUpdate(){
		Parser parser = new Parser(REMOTE_URL);
		parser.startRemoteUpdate(REMOTE_URL);
		parser.stopRemoteUpdate();
	}
	
}
