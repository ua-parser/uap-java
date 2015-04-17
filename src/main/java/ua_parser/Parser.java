/**
 * Copyright 2012 Twitter, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua_parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

/**
 * Java implementation of <a href="https://github.com/tobie/ua-parser">UA
 * Parser</a>
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class Parser {

	/** The Constant REGEX_YAML_PATH. */
	private static final String REGEX_YAML_PATH = "/ua_parser/regexes.yaml";

	/** The ua parser. */
	private UserAgentParser uaParser;

	/** The os parser. */
	private OSParser osParser;

	/** The device parser. */
	private DeviceParser deviceParser;

	/** The timer. */
	private Timer timer;

	/**
	 * Instantiates a new parser.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Parser() throws IOException {
		this(Parser.class.getResourceAsStream(REGEX_YAML_PATH));
	}

	/**
	 * Instantiates a new parser.
	 *
	 * @param regexYaml
	 *            the regex yaml
	 */
	public Parser(InputStream regexYaml) {
		initialize(regexYaml);
	}

	/**
	 * Instantiates a new parser.
	 *
	 * @param remoteUrl
	 *            the remote url
	 */
	public Parser(String remoteUrl) {
		loadFromUrl(remoteUrl);
	}

	/**
	 * Parses the.
	 *
	 * @param agentString
	 *            the agent string
	 * @return the client
	 */
	public Client parse(String agentString) {
		UserAgent ua = parseUserAgent(agentString);
		OS os = parseOS(agentString);
		Device device = deviceParser.parse(agentString);
		return new Client(ua, os, device);
	}

	/**
	 * Parses the user agent.
	 *
	 * @param agentString
	 *            the agent string
	 * @return the user agent
	 */
	public UserAgent parseUserAgent(String agentString) {
		return uaParser.parse(agentString);
	}

	/**
	 * Parses the device.
	 *
	 * @param agentString
	 *            the agent string
	 * @return the device
	 */
	public Device parseDevice(String agentString) {
		return deviceParser.parse(agentString);
	}

	/**
	 * Parses the os.
	 *
	 * @param agentString
	 *            the agent string
	 * @return the os
	 */
	public OS parseOS(String agentString) {
		return osParser.parse(agentString);
	}

	/**
	 * Start remote update.
	 *
	 * @param remoteUrl
	 *            the remote url
	 */
	public void startRemoteUpdate(final String remoteUrl) {
		stopRemoteUpdate();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				loadFromUrl(remoteUrl);
			}
		}, 0, 60 * 60 * 1000l);
	}

	/**
	 * Load from url.
	 *
	 * @param remoteUrl
	 *            the remote url
	 */
	private void loadFromUrl(String remoteUrl) {
		try {
			initialize(new URL(remoteUrl).openStream());
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"wrong couldn't load from remoteUrl", e);
		}
	}

	/**
	 * Stop remote update.
	 */
	public void stopRemoteUpdate() {
		if (timer != null) {
			timer.cancel();
		}
	}

	/**
	 * Initialize.
	 *
	 * @param regexYaml
	 *            the regex yaml
	 */
	private void initialize(InputStream regexYaml) {
		Yaml yaml = new Yaml(new SafeConstructor());
		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, String>>> regexConfig = (Map<String, List<Map<String, String>>>) yaml
				.load(regexYaml);

		List<Map<String, String>> uaParserConfigs = regexConfig
				.get("user_agent_parsers");
		if (uaParserConfigs == null) {
			throw new IllegalArgumentException(
					"user_agent_parsers is missing from yaml");
		}
		uaParser = UserAgentParser.fromList(uaParserConfigs);

		List<Map<String, String>> osParserConfigs = regexConfig
				.get("os_parsers");
		if (osParserConfigs == null) {
			throw new IllegalArgumentException(
					"os_parsers is missing from yaml");
		}
		osParser = OSParser.fromList(osParserConfigs);

		List<Map<String, String>> deviceParserConfigs = regexConfig
				.get("device_parsers");
		if (deviceParserConfigs == null) {
			throw new IllegalArgumentException(
					"device_parsers is missing from yaml");
		}
		deviceParser = DeviceParser.fromList(deviceParserConfigs);
	}
}
