package ua_parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JacksonModuleDatabindingTest {

    @Test
    public void testJacksonModuleSpiRegistration() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        Class<?> clientMixin = objectMapper.findMixInClassFor(Client.class);
        assertTrue(clientMixin.isAssignableFrom(ClientModule.ClientMixin.class));
    }

    @Test
    public void testJacksonSerializationAndDeserialization() throws IOException, JSONException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Client testClient = new Client(new UserAgent("Firefox", "3", "5", "5"),
                new OS("Mac OS X", "10", "4", null, null),
                new Device("Other"));

        String expectedSerialization = IOUtils.resourceToString(
                "ua_parser/testClient.json", Charset.defaultCharset(), getClass().getClassLoader());

        String actualSerialization = objectMapper.writeValueAsString(testClient);
        JSONAssert.assertEquals(expectedSerialization, actualSerialization, true);

        Client deserializedClient = objectMapper.readValue(actualSerialization, Client.class);
        assertEquals(testClient, deserializedClient);
    }
}
