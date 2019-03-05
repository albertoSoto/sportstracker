package de.saring.sportstracker.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportType;
import de.saring.util.unitcalc.FormatUtils.SpeedMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of class STDocument/Impl. All the involved components will be
 * mocked via Mockito.
 *
 * @author Stefan Saring
 */
public class STDocumentTest {

    private STDocument document;

    @BeforeEach
    public void setUp() {
        // STContext needs to be mocked
        STContext contextMock = mock(STContext.class);
        document = new STDocumentImpl(contextMock, null);
        document.loadOptions();
    }

    /**
     * Test of method evaluateCommandLineParameters().
     */
    @Test
    public void testEvaluateCommandLineParameters() {
        final String defaultDataDirectory = System.getProperty("user.home") + "/.sportstracker";

        // the document implementation must be used for checking
        STDocumentImpl documentImpl = (STDocumentImpl) document;

        document.evaluateCommandLineParameters(new ArrayList<>());
        assertEquals(defaultDataDirectory, documentImpl.getDataDirectory());

        // must not work, the user must use the '=' character
        document.evaluateCommandLineParameters(List.of("--datadir", "temp"));
        assertEquals(defaultDataDirectory, documentImpl.getDataDirectory());

        document.evaluateCommandLineParameters(List.of("--foo", "--datadir=temp"));
        assertEquals("temp", documentImpl.getDataDirectory());
    }

    /**
     * Test of method getSpeedModeForExercises(): when no exercise IDs were passed, then an IllegalArgumentException
     * needs to be thrown.
     */
    @Test
    public void testGetSpeedModeForExercisesEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
            document.getSpeedModeForExercises(null));
    }

    /**
     * Test of method getSpeedModeForExercises(): when there are multiple exercises to check and they have the same
     * speed mode in their sport types, then this speed mode needs to be returned.
     */
    @Test
    public void testGetSpeedModeForExercisesWithSameSpeedMode() {
        final int exerciseId1 = appendExerciseWithSpeedMode(SpeedMode.PACE);
        final int exerciseId2 = appendExerciseWithSpeedMode(SpeedMode.PACE);

        final SpeedMode speedMode = document.getSpeedModeForExercises(new int[]{exerciseId1, exerciseId2});

        assertEquals(SpeedMode.PACE, speedMode);
    }

    /**
     * Test of method getSpeedModeForExercises(): when there are multiple exercises to check and they have different
     * speed modes in their sport types, then the preferred speed mode needs to be returned.
     */
    @Test
    public void testGetSpeedModeForExercisesWithDifferentSpeedMode() {
        final int exerciseId1 = appendExerciseWithSpeedMode(SpeedMode.PACE);
        final int exerciseId2 = appendExerciseWithSpeedMode(SpeedMode.SPEED);

        final SpeedMode speedMode = document.getSpeedModeForExercises(new int[]{exerciseId1, exerciseId2});

        assertEquals(document.getOptions().getPreferredSpeedMode(), speedMode);
    }

    private int appendExerciseWithSpeedMode(SpeedMode speedMode) {
        final Exercise exercise = new Exercise(document.getExerciseList().getNewId());
        exercise.setDateTime(LocalDateTime.now());
        final SportType sportType = new SportType(document.getSportTypeList().getNewId());
        sportType.setSpeedMode(speedMode);
        exercise.setSportType(sportType);
        document.getExerciseList().set(exercise);
        return exercise.getId();
    }
}
