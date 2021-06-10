package ua_parser;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * This class is concurrent version of CachingParser.
 *
 * @author kyryloholodnov
 */
public class ConcurrentCachingParser extends Parser {

    private static long cacheKeyExpiresAfterAccessMs = 24 * 60L * 60 * 1000; // 24 hours
    private static long cacheMaximumSize = 1000;

    private final LoadingCache<String, Client> cacheClient = CacheBuilder.newBuilder()
            .expireAfterAccess(cacheKeyExpiresAfterAccessMs, TimeUnit.MILLISECONDS)
            .maximumSize(cacheMaximumSize)
            .build(new CacheLoader<String, Client>() {
                @Override
                @ParametersAreNonnullByDefault
                public Client load(String agentString) throws Exception {
                    return ConcurrentCachingParser.super.parse(agentString);
                }
            });

    private final LoadingCache<String, UserAgent> cacheUserAgent = CacheBuilder.newBuilder()
            .expireAfterAccess(cacheKeyExpiresAfterAccessMs, TimeUnit.MILLISECONDS)
            .maximumSize(cacheMaximumSize)
            .build(new CacheLoader<String, UserAgent>() {
                @Override
                @ParametersAreNonnullByDefault
                public UserAgent load(String agentString) throws Exception {
                    return ConcurrentCachingParser.super.parseUserAgent(agentString);
                }
            });

    private final LoadingCache<String, Device> cacheDevice = CacheBuilder.newBuilder()
            .expireAfterAccess(cacheKeyExpiresAfterAccessMs, TimeUnit.MILLISECONDS)
            .maximumSize(cacheMaximumSize)
            .build(new CacheLoader<String, Device>() {
                @Override
                @ParametersAreNonnullByDefault
                public Device load(String agentString) throws Exception {
                    return ConcurrentCachingParser.super.parseDevice(agentString);
                }
            });

    private final LoadingCache<String, OS> cacheOS = CacheBuilder.newBuilder()
            .expireAfterAccess(cacheKeyExpiresAfterAccessMs, TimeUnit.MILLISECONDS)
            .maximumSize(cacheMaximumSize)
            .build(new CacheLoader<String, OS>() {
                @Override
                @ParametersAreNonnullByDefault
                public OS load(String agentString) throws Exception {
                    return ConcurrentCachingParser.super.parseOS(agentString);
                }
            });

    public ConcurrentCachingParser() throws IOException {
        super();
    }

    public ConcurrentCachingParser(InputStream regexYaml) {
        super(regexYaml);
    }

    @Override
    public Client parse(String agentString) {
        if (agentString == null) {
            return null;
        }
        return cacheClient.getUnchecked(agentString);
    }

    @Override
    public UserAgent parseUserAgent(String agentString) {
        if (agentString == null) {
            return null;
        }
        return cacheUserAgent.getUnchecked(agentString);
    }

    @Override
    public Device parseDevice(String agentString) {
        if (agentString == null) {
            return null;
        }
        return cacheDevice.getUnchecked(agentString);
    }

    @Override
    public OS parseOS(String agentString) {
        if (agentString == null) {
            return null;
        }
        return cacheOS.getUnchecked(agentString);
    }

    public static long getCacheKeyExpiresAfterAccessMs() {
        return cacheKeyExpiresAfterAccessMs;
    }

    public static long getCacheMaximumSize() {
        return cacheMaximumSize;
    }

    public static void setCacheMaximumSize(long cacheMaximumSize) {
        if (cacheMaximumSize < 1) {
            throw new IllegalArgumentException("Cache size should be positive value");
        }
        ConcurrentCachingParser.cacheMaximumSize = cacheMaximumSize;
    }

    public static void setCacheKeyExpiresAfterAccessMs(long cacheKeyExpiresAfterAccessMs) {
        if (cacheKeyExpiresAfterAccessMs < 1) {
            throw new IllegalArgumentException("Cache key expiration should be positive value");
        }
        ConcurrentCachingParser.cacheKeyExpiresAfterAccessMs = cacheKeyExpiresAfterAccessMs;
    }
}
