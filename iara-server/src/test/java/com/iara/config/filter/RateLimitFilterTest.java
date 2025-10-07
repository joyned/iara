package com.iara.config.filter;

import com.iara.core.exception.RateLimitException;
import com.iara.utils.IpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
@MockitoSettings(strictness = Strictness.LENIENT)
class RateLimitFilterTest {

    private RateLimitFilter rateLimitFilter;
    private HttpServletRequest request;
    private ServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        rateLimitFilter = new RateLimitFilter();
        request = mock(HttpServletRequest.class);
        response = mock(ServletResponse.class);
        filterChain = mock(FilterChain.class);

        ReflectionTestUtils.setField(rateLimitFilter, "rateLimit", 60);
        ReflectionTestUtils.setField(rateLimitFilter, "rateDuration", 60000L);

        ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> requestCounts =
                (ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>>)
                        ReflectionTestUtils.getField(rateLimitFilter, "requestCounts");
        requestCounts.clear();
    }

    @Test
    void Given_RequestsWithinRateLimit_ShouldProcessNormally() throws Exception {
        String ip = "192.168.1.1";
        String endpoint = "/v1/secrets";

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip);
            when(request.getRequestURI()).thenReturn(endpoint);

            for (int i = 0; i < 60; i++) {
                assertDoesNotThrow(() ->
                        rateLimitFilter.doFilter(request, response, filterChain)
                );
            }

            verify(filterChain, times(60)).doFilter(request, response);
        }
    }

    @Test
    void Given_RequestsExceedingRateLimit_ShouldThrowRateLimitException() throws Exception {
        String ip = "192.168.1.1";
        String endpoint = "/v1/kv";

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip);
            when(request.getRequestURI()).thenReturn(endpoint);

            for (int i = 0; i < 60; i++) {
                assertDoesNotThrow(() ->
                        rateLimitFilter.doFilter(request, response, filterChain)
                );
            }

            RateLimitException exception = assertThrows(RateLimitException.class, () ->
                    rateLimitFilter.doFilter(request, response, filterChain)
            );

            assertTrue(exception.getMessage().contains("Too many requests at endpoint " + endpoint));
            assertTrue(exception.getMessage().contains("from IP " + ip));
            assertTrue(exception.getMessage().contains("milliseconds"));

            verify(filterChain, times(60)).doFilter(request, response);
        }
    }

    @Test
    void Given_MultipleIps_ShouldTrackEachIpSeparately() throws Exception {
        String ip1 = "192.168.1.1";
        String ip2 = "192.168.1.2";
        String endpoint = "/v1/secrets";

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip1);
            when(request.getRequestURI()).thenReturn(endpoint);

            for (int i = 0; i < 60; i++) {
                assertDoesNotThrow(() ->
                        rateLimitFilter.doFilter(request, response, filterChain)
                );
            }

            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip2);
            for (int i = 0; i < 60; i++) {
                assertDoesNotThrow(() ->
                        rateLimitFilter.doFilter(request, response, filterChain)
                );
            }

            verify(filterChain, times(120)).doFilter(request, response);
        }
    }

    @Test
    void Given_OldRequests_ShouldCleanUpAutomatically() throws Exception {
        String ip = "192.168.1.1";
        String endpoint = "/v1/kv";

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip);
            when(request.getRequestURI()).thenReturn(endpoint);

            ReflectionTestUtils.setField(rateLimitFilter, "rateDuration", 1L);

            for (int i = 0; i < 60; i++) {
                rateLimitFilter.doFilter(request, response, filterChain);
            }

            Thread.sleep(10);

            for (int i = 0; i < 60; i++) {
                assertDoesNotThrow(() ->
                        rateLimitFilter.doFilter(request, response, filterChain)
                );
            }

            verify(filterChain, times(120)).doFilter(request, response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        }
    }

    @Test
    void Given_CustomRateLimit_ShouldRespectCustomLimit() throws Exception {
        String ip = "192.168.1.1";
        String endpoint = "/v1/secrets";
        int customRateLimit = 10;

        ReflectionTestUtils.setField(rateLimitFilter, "rateLimit", customRateLimit);

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip);
            when(request.getRequestURI()).thenReturn(endpoint);

            for (int i = 0; i < customRateLimit; i++) {
                assertDoesNotThrow(() ->
                        rateLimitFilter.doFilter(request, response, filterChain)
                );
            }

            RateLimitException exception = assertThrows(RateLimitException.class, () ->
                    rateLimitFilter.doFilter(request, response, filterChain)
            );

            assertTrue(exception.getMessage().contains("Too many requests"));
            verify(filterChain, times(customRateLimit)).doFilter(request, response);
        }
    }

    @Test
    void Given_SameIpDifferentEndpoints_ShouldTrackByIpOnly() throws Exception {
        String ip = "192.168.1.1";
        String endpoint1 = "/v1/secrets";
        String endpoint2 = "/v1/kv";

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip);

            when(request.getRequestURI()).thenReturn(endpoint1);
            for (int i = 0; i < 30; i++) {
                rateLimitFilter.doFilter(request, response, filterChain);
            }

            when(request.getRequestURI()).thenReturn(endpoint2);
            for (int i = 0; i < 30; i++) {
                rateLimitFilter.doFilter(request, response, filterChain);
            }

            RateLimitException exception = assertThrows(RateLimitException.class, () ->
                    rateLimitFilter.doFilter(request, response, filterChain)
            );

            assertTrue(exception.getMessage().contains(endpoint2));
            verify(filterChain, times(60)).doFilter(request, response);
        }
    }

    @Test
    void Given_EmptyRequestCounts_ShouldInitializeNewQueue() throws Exception {
        String ip = "192.168.1.1";
        String endpoint = "/v1/secrets";

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip);
            when(request.getRequestURI()).thenReturn(endpoint);

            ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> requestCounts =
                    (ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>>)
                            ReflectionTestUtils.getField(rateLimitFilter, "requestCounts");
            requestCounts.clear();

            assertDoesNotThrow(() ->
                    rateLimitFilter.doFilter(request, response, filterChain)
            );

            assertTrue(requestCounts.containsKey(ip));
            assertEquals(1, requestCounts.get(ip).size());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void Given_TimeIsTooOld_ShouldReturnTrue() {
        long currentTime = System.currentTimeMillis();
        long oldTime = currentTime - 70000;

        boolean result = (Boolean) ReflectionTestUtils.invokeMethod(
                rateLimitFilter, "timeIsTooOld", currentTime, oldTime
        );

        assertTrue(result);
    }

    @Test
    void Given_TimeIsNotTooOld_ShouldReturnFalse() {
        long currentTime = System.currentTimeMillis();
        long recentTime = currentTime - 30000;

        boolean result = ReflectionTestUtils.invokeMethod(
                rateLimitFilter, "timeIsTooOld", currentTime, recentTime
        );

        assertFalse(result);
    }

    @Test
    void Given_RequestsWithCleanup_ShouldMaintainAccurateCount() throws Exception {
        String ip = "192.168.1.1";
        String endpoint = "/v1/secrets";

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip);
            when(request.getRequestURI()).thenReturn(endpoint);

            ReflectionTestUtils.setField(rateLimitFilter, "rateDuration", 10L);

            rateLimitFilter.doFilter(request, response, filterChain);

            Thread.sleep(20);

            rateLimitFilter.doFilter(request, response, filterChain);

            for (int i = 0; i < 59; i++) {
                assertDoesNotThrow(() ->
                        rateLimitFilter.doFilter(request, response, filterChain)
                );
            }

            Thread.sleep(20);

            assertDoesNotThrow(() ->
                    rateLimitFilter.doFilter(request, response, filterChain)
            );

            verify(filterChain, times(62)).doFilter(request, response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        }
    }

    @Test
    void Given_ExceptionInFilterChain_ShouldStillCountRequest() throws Exception {
        String ip = "192.168.1.1";
        String endpoint = "/v1/secrets";

        try (MockedStatic<IpUtils> ipUtilsMock = mockStatic(IpUtils.class)) {
            ipUtilsMock.when(() -> IpUtils.getIp(request)).thenReturn(ip);
            when(request.getRequestURI()).thenReturn(endpoint);

            doThrow(new RuntimeException("Filter chain error")).when(filterChain).doFilter(request, response);

            assertThrows(RuntimeException.class, () ->
                    rateLimitFilter.doFilter(request, response, filterChain)
            );

            ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> requestCounts =
                    (ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>>)
                            ReflectionTestUtils.getField(rateLimitFilter, "requestCounts");

            assertTrue(requestCounts.containsKey(ip));
            assertEquals(1, requestCounts.get(ip).size());
        }
    }
}