package modelarium.attributes.functional.routines;

import modelarium.attributes.AttributeAccessLevel;
import modelarium.attributes.Routine;

public class FunctionalRoutine extends Routine {
    private final RoutineRunFunction runLogic;

    public FunctionalRoutine(String name, AttributeAccessLevel accessLevel, RoutineRunFunction runLogic) {
        super(name, accessLevel);
        this.runLogic = runLogic;
    }

    public FunctionalRoutine(String name, RoutineRunFunction runLogic) {
        super(name);
        this.runLogic = runLogic;
    }

    public FunctionalRoutine(AttributeAccessLevel accessLevel, RoutineRunFunction runLogic) {
        super(accessLevel);
        this.runLogic = runLogic;
    }

    public FunctionalRoutine(RoutineRunFunction runLogic) {
        super();
        this.runLogic = runLogic;
    }

    @Override
    public void run() {
        runLogic.run(owner());
    }
}
