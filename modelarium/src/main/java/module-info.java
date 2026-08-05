module modelarium {
    // Public API
    exports modelarium;
    exports modelarium.clock;
    exports modelarium.entities;
    exports modelarium.entities.agents;
    exports modelarium.entities.agents.generators;
    exports modelarium.entities.attributes;
    exports modelarium.entities.attributes.events;
    exports modelarium.entities.attributes.events.functional;
    exports modelarium.entities.attributes.properties;
    exports modelarium.entities.attributes.properties.functional;
    exports modelarium.entities.attributes.routines;
    exports modelarium.entities.attributes.routines.functional;
    exports modelarium.entities.contexts;
    exports modelarium.entities.environments;
    exports modelarium.entities.immutable;
    exports modelarium.entities.immutable.attributes;
    exports modelarium.entities.logging;
    exports modelarium.entities.logging.databases;
    exports modelarium.entities.logging.databases.factories;
    exports modelarium.exceptions;
    exports modelarium.internal;
    exports modelarium.results;
    exports modelarium.results.immutable;
    exports modelarium.scheduler;

    // Private API
    //   modelarium.multithreading
    //   modelarium.multithreading.requestresponse
    //   modelarium.results.mutable
    //   modelarium.utils

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires java.sql;
    requires cloning;
    requires modelarium;

    // Reflective deep-clone access for the Rits Cloner (see modelarium.utils.Cloners)
    opens modelarium.entities to cloning;
    opens modelarium.entities.agents to cloning;
    opens modelarium.entities.attributes to cloning;
    opens modelarium.entities.attributes.properties to cloning;
    opens modelarium.entities.attributes.properties.functional to cloning;
    opens modelarium.entities.attributes.events.functional to cloning;
    opens modelarium.entities.attributes.routines.functional to cloning;
    opens modelarium.entities.environments to cloning;
    opens modelarium.entities.attributes.events to cloning;
    opens modelarium.entities.attributes.routines to cloning;
    exports modelarium.scheduler.functional;
}