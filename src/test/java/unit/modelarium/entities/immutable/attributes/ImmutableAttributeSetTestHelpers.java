package unit.modelarium.entities.immutable.attributes;

import modelarium.entities.attributes.AgentAttributeSet;
import modelarium.entities.attributes.Attribute;
import modelarium.entities.attributes.AttributeSet;
import modelarium.entities.attributes.EnvironmentAttributeSet;
import modelarium.entities.contexts.AgentSimulationContext;
import modelarium.entities.contexts.Context;
import modelarium.entities.contexts.EnvironmentSimulationContext;
import modelarium.entities.contexts.SimulationContext;
import modelarium.entities.immutable.attributes.ImmutableAgentAttributeSet;
import modelarium.entities.immutable.attributes.ImmutableAttributeSet;
import modelarium.entities.immutable.attributes.ImmutableEnvironmentAttributeSet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

class ImmutableAttributeSetTestHelpers {
    private ImmutableAttributeSetTestHelpers() {}

    static <T extends AttributeSet<?,?>> T makeAttributeSet(
            Class<T> attributeSetClass,
            String attributeSetName,
            List<Attribute<?>> attributes
    ) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return attributeSetClass.getConstructor(
                String.class,
                String.class,
                List.class
        ).newInstance(
                "TestOwner",
                attributeSetName,
                attributes
        );
    }

    static <T extends ImmutableAttributeSet<?,?>> T makeImmutableAttributeSet(
            Class<T> attributeSetClass,
            String attributeSetName,
            List<Attribute<?>> attributes
    ) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (attributeSetClass.equals(ImmutableAgentAttributeSet.class)) {
            return (T) new ImmutableAgentAttributeSet(makeAttributeSet(
                    AgentAttributeSet.class,
                    attributeSetName,
                    attributes
            ));
        }
        else if (attributeSetClass.equals(ImmutableEnvironmentAttributeSet.class)) {
            return (T) new ImmutableEnvironmentAttributeSet(makeAttributeSet(
                    EnvironmentAttributeSet.class,
                    attributeSetName,
                    attributes
            ));
        }
        else {
            throw new IllegalArgumentException("'" + attributeSetClass.getName() + "' is not supported");
        }
    }

    private static <SC extends SimulationContext, C extends Context> AttributeSet<SC, C> getMutableAttributeSetFromImmutableAttributeSet(
            ImmutableAttributeSet<SC, C> immutableAttributeSet
    ) throws NoSuchFieldException, IllegalAccessException {
        Field attributeSetField = ImmutableAttributeSet.class.getDeclaredField("attributeSet");
        attributeSetField.setAccessible(true);
        return (AttributeSet<SC, C>) attributeSetField.get(immutableAttributeSet);
    }

    static AgentAttributeSet getMutableFromImmutable(
            ImmutableAgentAttributeSet immutableAttributeSet
    ) throws NoSuchFieldException, IllegalAccessException {
        return (AgentAttributeSet) getMutableAttributeSetFromImmutableAttributeSet(immutableAttributeSet);
    }

    static EnvironmentAttributeSet getMutableFromImmutable(
            ImmutableEnvironmentAttributeSet immutableAttributeSet
    ) throws NoSuchFieldException, IllegalAccessException {
        return (EnvironmentAttributeSet) getMutableAttributeSetFromImmutableAttributeSet(immutableAttributeSet);
    }
}
