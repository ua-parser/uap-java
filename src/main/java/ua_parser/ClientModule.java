package ua_parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Class that registers capability of serializing objects with Jackson core.
 *
 * This module can be manually registered with Jackson:
 * <pre>
 *     ObjectMapper mapper = new ObjectMapper();
 *     mapper.registerModule(new ClientModule());
 * </pre>
 *
 * This module also supports auto configuration as an SPI via the Java ServiceLoader api:
 * <pre>
 *     ObjectMapper mapper = new ObjectMapper();
 *     mapper.findAndRegisterModules();
 *
 * </pre>
 *
 * @author Adam J. Weigold
 */
public class ClientModule extends SimpleModule {

    public ClientModule() {
        setMixInAnnotation(UserAgent.class, UserAgentMixin.class);
        setMixInAnnotation(OS.class, OSMixin.class);
        setMixInAnnotation(Device.class, DeviceMixin.class);
        setMixInAnnotation(Client.class, ClientMixin.class);
    }

    abstract static class UserAgentMixin {
        @JsonCreator
        public UserAgentMixin(@JsonProperty("family") String family,
                              @JsonProperty("major") String major,
                              @JsonProperty("minor") String minor,
                              @JsonProperty("patch") String patch) {}
    }

    abstract static class OSMixin {
        @JsonCreator
        public OSMixin(@JsonProperty("family") String family,
                       @JsonProperty("major") String major,
                       @JsonProperty("minor") String minor,
                       @JsonProperty("patch") String patch,
                       @JsonProperty("patchMinor") String patchMinor) {}
    }

    abstract static class DeviceMixin {
        @JsonCreator
        public DeviceMixin(@JsonProperty("family") String family) {}
    }

    abstract static class ClientMixin {
        @JsonCreator
        public ClientMixin(@JsonProperty("userAgent") UserAgent userAgent,
                           @JsonProperty("os") OS os,
                           @JsonProperty("device") Device device) {}
    }
}
