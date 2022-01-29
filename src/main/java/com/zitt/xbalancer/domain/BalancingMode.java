/**
 * MIT License
 * <p>
 * Copyright (c) 2018-2022 Lukasz Krzywda
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
    KEY_HASH("KEY_HASH"),
    TIMESTAMP_HASH("TIMESTAMP_HASH"),
    LEAST_CONNECTIONS("LEAST_CONNECTIONS"), // NOT EXTERNAL
    WEIGHTED_LEAST_CONNECTIONS("WEIGHTED_LEAST_CONNECTIONS"); // NOT EXTERNAL


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
