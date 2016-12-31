package io.freefair.spring.okhttp.logging;

import lombok.NoArgsConstructor;
import org.springframework.boot.bind.RelaxedNames;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class EnumHelper {

    @SuppressWarnings("unchecked")
    static <E extends Enum<E>> E valueOf(Class<E> enumType, String name) {
        name = name.trim();
        for (E candidate : EnumSet.allOf(enumType)) {
            RelaxedNames names = new RelaxedNames(
                    candidate.name().replace("_", "-").toLowerCase());
            for (String relaxedName : names) {
                if (relaxedName.equals(name)) {
                    return candidate;
                }
            }
            if (candidate.name().equalsIgnoreCase(name)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("No enum constant "
                + enumType.getCanonicalName() + "." + name);
    }
}
