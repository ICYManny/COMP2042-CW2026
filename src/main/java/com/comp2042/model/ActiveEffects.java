package com.comp2042.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Keeps track of which {@link Effect}s are active and how long each has left.
 * Collecting an effect that is already active restarts its timer.
 */
public class ActiveEffects {

    private final Map<Effect, Double> remaining = new EnumMap<>(Effect.class);

    /**
     * Starts an effect, or restarts its timer if it is already active.
     *
     * @param effect the effect to start
     */
    public void activate(Effect effect) {
        remaining.put(effect, effect.getDurationSeconds());
    }

    /**
     * Counts down every active effect and removes the ones that have run out.
     *
     * @param elapsedSeconds play time since the last update
     */
    public void update(double elapsedSeconds) {
        remaining.replaceAll((effect, seconds) -> seconds - elapsedSeconds);
        remaining.values().removeIf(seconds -> seconds <= 0);
    }

    /**
     * @param effect an effect
     * @return true if the effect is currently active
     */
    public boolean isActive(Effect effect) {
        return remaining.containsKey(effect);
    }

    /**
     * @param effect an effect
     * @return seconds left, or 0 if the effect is not active
     */
    public double getRemaining(Effect effect) {
        return remaining.getOrDefault(effect, 0.0);
    }

    /** @return a read-only view of every active effect and its remaining seconds */
    public Map<Effect, Double> getAll() {
        return Collections.unmodifiableMap(remaining);
    }

    /** Ends every effect. */
    public void clear() {
        remaining.clear();
    }
}
