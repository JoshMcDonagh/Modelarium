package dev.modelarium.examples.el_farol_bar.entities.agents.attributes.decision;

import dev.modelarium.examples.el_farol_bar.entities.agents.prediction.AttendanceHistory;
import dev.modelarium.examples.el_farol_bar.entities.agents.prediction.AttendancePredictor;
import modelarium.entities.agents.immutable.ReadOnlyAgent;
import modelarium.entities.attributes.AttributeAccessLevel;
import modelarium.entities.attributes.routines.AgentRoutine;
import modelarium.entities.contexts.AgentContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Observes the previous week's aggregate attendance, scores the agent's predictors, selects its most accurate
 * predictor, and decides whether to attend this week.
 */
public final class MakeAttendanceDecisionRoutine extends AgentRoutine {
    /**
     * Shared identity lets each worker's per-tick context cache reuse one global filtered-agent request for all of
     * its agents. At the start of a tick, coordinator state represents the decisions from the preceding tick.
     */
    private static final Predicate<ReadOnlyAgent> PREVIOUS_WEEK_ATTENDEES = agent -> Boolean.TRUE.equals(agent
            .getProperty("decision", "attending")
            .get());

    private final List<AttendancePredictor> predictors;
    private final AttendanceHistory attendanceHistory;
    private final int populationSize;

    public MakeAttendanceDecisionRoutine(
            List<AttendancePredictor> predictors,
            List<Integer> initialAttendanceHistory,
            int populationSize
    ) {
        super("make_attendance_decision", AttributeAccessLevel.PRIVATE);
        this.predictors = new ArrayList<>(predictors);
        this.attendanceHistory = new AttendanceHistory(initialAttendanceHistory);
        this.populationSize = populationSize;
    }

    @Override
    protected void run(AgentContext context) {
        // On tick 0 there is no simulated previous week yet: the configured historical observations are used.
        // Thereafter, coordinator state at the tick boundary contains every agent's decision from the previous week.
        if (context.getClock().currentTick() > 0)
            attendanceHistory.observe(context.getFilteredAgents(PREVIOUS_WEEK_ATTENDEES).size());

        int latestObservedAttendance = attendanceHistory.latest();

        // The newly observed attendance is the outcome of the predictors' forecasts from the preceding week.
        for (AttendancePredictor predictor : predictors)
            predictor.scorePendingPrediction(latestObservedAttendance);

        // Every monitored predictor makes a fresh forecast so all of their accuracies can be updated next week.
        for (AttendancePredictor predictor : predictors)
            predictor.predict(attendanceHistory, populationSize);

        AttendancePredictor activePredictor = selectMostAccuratePredictor(context);
        int predictedAttendance = activePredictor.latestPrediction();
        int crowdingThreshold = (Integer) context
                .getEnvironment()
                .getProperty("bar", "crowding_threshold")
                .get();

        ((LastObservedAttendanceProperty) context
                .getThisEntity()
                .getProperty("decision", "last_observed_attendance"))
                .set(latestObservedAttendance);

        ((ActivePredictorProperty) context
                .getThisEntity()
                .getProperty("decision", "active_predictor"))
                .set(activePredictor.name());

        ((PredictedAttendanceProperty) context
                .getThisEntity()
                .getProperty("decision", "predicted_attendance"))
                .set(predictedAttendance);

        // The crowding threshold is the largest attendance the agent regards as comfortable, so a forecast exactly at
        // the threshold still leads to attendance. This is the convention used in standard El Farol replications.
        ((AttendingProperty) context
                .getThisEntity()
                .getProperty("decision", "attending"))
                .set(predictedAttendance <= crowdingThreshold);
    }

    private AttendancePredictor selectMostAccuratePredictor(AgentContext context) {
        double bestError = predictors.stream()
                .mapToDouble(AttendancePredictor::errorScore)
                .min()
                .orElseThrow();

        List<AttendancePredictor> bestPredictors = predictors.stream()
                .filter(predictor -> Double.compare(predictor.errorScore(), bestError) == 0)
                .toList();

        return bestPredictors.get(context.getRandom().nextInt(bestPredictors.size()));
    }
}
