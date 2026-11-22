package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActiveEffectsTest {

    private ActiveEffects effects;

    @BeforeEach
    void setUp() {
        effects = new ActiveEffects();
    }

    @Test
    void nothingIsActiveAtFirst() {
        for (Effect effect : Effect.values()) {
            assertFalse(effects.isActive(effect));
            assertEquals(0, effects.getRemaining(effect), 1e-9);
        }
    }

    @Test
    void activatedEffectLastsItsFullDuration() {
        effects.activate(Effect.GHOST);
        effects.update(Effect.GHOST.getDurationSeconds() - 0.01);
        assertTrue(effects.isActive(Effect.GHOST));
        effects.update(0.02);
        assertFalse(effects.isActive(Effect.GHOST));
    }

    @Test
    void collectingAgainRestartsTheTimer() {
        effects.activate(Effect.SLOW);
        effects.update(5);
        effects.activate(Effect.SLOW);
        assertEquals(Effect.SLOW.getDurationSeconds(), effects.getRemaining(Effect.SLOW), 1e-9);
    }

    @Test
    void effectsCountDownIndependently() {
        effects.activate(Effect.GHOST);
        effects.activate(Effect.DOUBLE_POINTS);
        effects.update(Effect.GHOST.getDurationSeconds());
        assertFalse(effects.isActive(Effect.GHOST));
        assertTrue(effects.isActive(Effect.DOUBLE_POINTS));
    }

    @Test
    void clearEndsEverything() {
        effects.activate(Effect.GHOST);
        effects.activate(Effect.SLOW);
        effects.clear();
        assertTrue(effects.getAll().isEmpty());
    }

    @Test
    void viewCannotBeModified() {
        assertThrows(UnsupportedOperationException.class, () -> effects.getAll().put(Effect.SLOW, 1.0));
    }
}
