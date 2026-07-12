package unit.modelarium;

import modelarium.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static unit.modelarium.ConfigTestHelpers.*;

public class ModelTest {
    @Test
    public void testGetResults_BeforeRun_IllegalStateException() {
        Model model = new Model(syncedConfig(1, 1, 1));

        IllegalStateException exception = assertThrows(IllegalStateException.class, model::getResults);
        assertEquals("Results cannot be accessed before a model run has been completed", exception.getMessage());
    }
}
