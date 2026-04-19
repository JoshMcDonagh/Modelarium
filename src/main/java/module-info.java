module modelarium {
    // Public API — users may import and extend these
    exports modelarium;
    exports modelarium.entities;
    exports modelarium.entities.agents;
    exports modelarium.entities.agents.sets;
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
    exports modelarium.entities.logging;
    exports modelarium.entities.logging.databases;
    exports modelarium.entities.logging.databases.factories;
    exports modelarium.exceptions;
    exports modelarium.results;
    exports modelarium.results.immutable;
    exports modelarium.scheduler;

    // NOT exported — entire packages hidden from external consumers:
    //   modelarium.multithreading
    //   modelarium.multithreading.requestresponse
    //   modelarium.results.mutable
    //   modelarium.utils

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires java.sql;
    requires cloning;
}