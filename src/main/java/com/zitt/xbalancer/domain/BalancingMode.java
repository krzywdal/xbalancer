package com.zitt.xbalancer.domain;

import java.util.Optional;

/**
 * Enum lists all supported balancing modes
 * <p>
 * Created by lkrzywda on 4/16/17.
 */
public enum BalancingMode {

    ROUND_ROBIN("ROUND_ROBIN"),
    RANDOM("RANDOM"),
    STICKY("STICKY"),
    IP_HASH("IP_HASH"),
    KEY_HASH("KEY_HASH");

    private String name;

    BalancingMode(String name) {
        this.name = name;
    }

    /**
     * @param name
     * @return
     */
    public static Optional<BalancingMode> getByName(String name) {
        for (BalancingMode balancingMode : BalancingMode.values()) {
            if (balancingMode.name.equals(name)) {
                return Optional.of(balancingMode);
            }
        }
        return Optional.empty();
    }
}
