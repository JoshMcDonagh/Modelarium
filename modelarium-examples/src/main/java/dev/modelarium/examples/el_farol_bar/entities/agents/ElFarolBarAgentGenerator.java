package dev.modelarium.examples.el_farol_bar.entities.agents;

import dev.modelarium.examples.el_farol_bar.config.ElFarolBarSettings;
import dev.modelarium.examples.el_farol_bar.entities.agents.attributes.decision.*;
import dev.modelarium.examples.el_farol_bar.entities.agents.prediction.AttendancePredictor;
import dev.modelarium.examples.el_farol_bar.entities.agents.prediction.FocalPredictorFactory;
import modelarium.Config;
import modelarium.entities.agents.generators.DefaultAgentGenerator;
import modelarium.entities.agents.mutable.Agent;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.sets.mutable.AgentAttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/** Generates heterogeneous El Farol agents with independently sampled predictor portfolios. */
public final class ElFarolBarAgentGenerator extends DefaultAgentGenerator {
    private final ElFarolBarSettings settings;
    private int generatedAgentCount = 0;

    public ElFarolBarAgentGenerator(ElFarolBarSettings settings) {
        this.settings = settings;
    }

    @Override
    protected Agent generateAgent(Config config, RandomGenerator random) {
        int predictorsPerAgent = settings.agents().predictorsPerAgent();
        List<AttendancePredictor> predictors = FocalPredictorFactory.createPortfolio(
                predictorsPerAgent,
                config.populationSize(),
                settings.bar().crowdingThreshold(),
                random
        );

        List<Attribute> decisionAttributes = new ArrayList<>();
        decisionAttributes.add(new MakeAttendanceDecisionRoutine(
                predictors,
                settings.prediction().initialAttendanceHistory(),
                config.populationSize()
        ));
        decisionAttributes.add(new LastObservedAttendanceProperty());
        decisionAttributes.add(new ActivePredictorProperty());
        decisionAttributes.add(new PredictedAttendanceProperty());
        decisionAttributes.add(new AttendingProperty());

        Agent agent = new Agent(
                "agent_" + generatedAgentCount,
                List.of(new AgentAttributeSet("decision", decisionAttributes))
        );
        generatedAgentCount++;

        return agent;
    }
}
