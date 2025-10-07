package com.iara.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenHolder {

    private final Map<String, String> active;
    private final Set<String> blocklist;

    public TokenHolder() {
        this.active = new ConcurrentHashMap<>();
        this.blocklist = ConcurrentHashMap.newKeySet();
    }

    public void putActive(String token, String ip) {
        this.active.putIfAbsent(token, ip);
    }

    public String getActive(String token) {
        return this.active.get(token);
    }

    public void removeActive(String token) {
        this.active.remove(token);
    }

    public void addBlocklist(String token) {
        this.blocklist.add(token);
    }

    public boolean isBlocked(String token) {
        return this.blocklist.contains(token);
    }

    @Scheduled(cron = "* */10 * * * *")
    public void clear() {
        Set<String> toRemove = new HashSet<>();
        for (String token : blocklist) {
            Date now = new Date();
            Claims claims = Jwts.parser()
                    .build()
                    .parseUnsecuredClaims(token)
                    .getPayload();

            if (now.getTime() >= claims.getExpiration().getTime()) {
                toRemove.add(token);
            }
        }

        this.blocklist.removeAll(toRemove);
    }
}
