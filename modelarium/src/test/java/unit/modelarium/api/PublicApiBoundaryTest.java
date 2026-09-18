package unit.modelarium.api;

import modelarium.Model;
import modelarium.api.PublicApi;
import modelarium.internal.Internal;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verifies the supported/internal API classification of compiled production classes. */
public class PublicApiBoundaryTest {

    @Test
    public void testEveryPublicProductionTypeHasAnApiClassification() throws Exception {
        List<String> violations = new ArrayList<>();

        for (Class<?> type : productionTypes()) {
            if (!Modifier.isPublic(type.getModifiers()))
                continue;

            boolean internal = isInternal(type);
            boolean supported = isPublicApi(type);
            if (internal == supported)
                violations.add(type.getName() + " must be classified as exactly one of @PublicApi or @Internal");
        }

        assertTrue(violations.isEmpty(), String.join(System.lineSeparator(), violations));
    }

    @Test
    public void testSupportedSignaturesDoNotExposeInternalTypes() throws Exception {
        List<String> violations = new ArrayList<>();

        for (Class<?> type : productionTypes()) {
            if (!Modifier.isPublic(type.getModifiers()) || !isPublicApi(type))
                continue;

            checkType(type.getGenericSuperclass(), type.getName() + " superclass", violations);
            for (Type implementedInterface : type.getGenericInterfaces())
                checkType(implementedInterface, type.getName() + " interface", violations);

            for (Constructor<?> constructor : type.getDeclaredConstructors()) {
                if (isSupportedMember(constructor))
                    checkExecutableTypes(constructor.getGenericParameterTypes(), constructor.getGenericExceptionTypes(),
                            constructor.toGenericString(), violations);
            }

            for (Method method : type.getDeclaredMethods()) {
                if (!isSupportedMember(method))
                    continue;
                checkType(method.getGenericReturnType(), method.toGenericString(), violations);
                checkExecutableTypes(method.getGenericParameterTypes(), method.getGenericExceptionTypes(),
                        method.toGenericString(), violations);
            }

            for (Field field : type.getDeclaredFields()) {
                if (isSupportedMember(field))
                    checkType(field.getGenericType(), field.toGenericString(), violations);
            }
        }

        assertTrue(violations.isEmpty(), String.join(System.lineSeparator(), violations));
    }

    @Test
    public void testInternalMethodPrefixIsOnlyUsedForDeliberateInternalBridges() throws Exception {
        List<String> violations = new ArrayList<>();

        for (Class<?> type : productionTypes()) {
            for (Method method : type.getDeclaredMethods()) {
                if (method.getName().startsWith("internal") && !method.isAnnotationPresent(Internal.class))
                    violations.add(method.toGenericString() + " uses the internal prefix without @Internal");
            }
        }

        assertTrue(violations.isEmpty(), String.join(System.lineSeparator(), violations));
    }

    private static List<Class<?>> productionTypes() throws Exception {
        Path classesRoot = Path.of(Model.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path packageRoot = classesRoot.resolve("modelarium");
        ClassLoader loader = Model.class.getClassLoader();
        List<Class<?>> types = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(packageRoot)) {
            for (Path classFile : paths.filter(path -> path.toString().endsWith(".class")).toList()) {
                String relativeName = classesRoot.relativize(classFile).toString();
                if (relativeName.contains("$") || relativeName.endsWith("module-info.class")
                        || relativeName.endsWith("package-info.class"))
                    continue;

                String className = relativeName.substring(0, relativeName.length() - ".class".length())
                        .replace('/', '.')
                        .replace('\\', '.');
                types.add(Class.forName(className, false, loader));
            }
        }

        return types;
    }

    private static boolean isInternal(Class<?> type) {
        Package typePackage = type.getPackage();
        return type.isAnnotationPresent(Internal.class)
                || (typePackage != null && typePackage.isAnnotationPresent(Internal.class));
    }

    private static boolean isPublicApi(Class<?> type) {
        if (type.isAnnotationPresent(Internal.class))
            return false;
        Package typePackage = type.getPackage();
        return type.isAnnotationPresent(PublicApi.class)
                || (typePackage != null && typePackage.isAnnotationPresent(PublicApi.class));
    }

    private static boolean isSupportedMember(Member member) {
        int modifiers = member.getModifiers();
        if (!Modifier.isPublic(modifiers) && !Modifier.isProtected(modifiers))
            return false;
        return !((AnnotatedElement) member).isAnnotationPresent(Internal.class);
    }

    private static void checkExecutableTypes(
            Type[] parameterTypes,
            Type[] exceptionTypes,
            String location,
            List<String> violations
    ) {
        for (Type parameterType : parameterTypes)
            checkType(parameterType, location, violations);
        for (Type exceptionType : exceptionTypes)
            checkType(exceptionType, location, violations);
    }

    private static void checkType(Type type, String location, List<String> violations) {
        if (type == null)
            return;

        if (type instanceof Class<?> rawType) {
            Class<?> componentType = rawType.isArray() ? rawType.getComponentType() : rawType;
            if (isInternal(componentType))
                violations.add(location + " exposes internal type " + componentType.getName());
        } else if (type instanceof ParameterizedType parameterizedType) {
            checkType(parameterizedType.getRawType(), location, violations);
            for (Type argument : parameterizedType.getActualTypeArguments())
                checkType(argument, location, violations);
        } else if (type instanceof GenericArrayType arrayType) {
            checkType(arrayType.getGenericComponentType(), location, violations);
        } else if (type instanceof WildcardType wildcardType) {
            for (Type bound : wildcardType.getLowerBounds())
                checkType(bound, location, violations);
            for (Type bound : wildcardType.getUpperBounds())
                checkType(bound, location, violations);
        }
    }
}
