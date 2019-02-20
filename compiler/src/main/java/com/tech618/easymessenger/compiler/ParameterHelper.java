package com.tech618.easymessenger.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.VariableElement;

/**
 * Created by 82538 on 2018/4/30.
 */

public class ParameterHelper {
    public static String getMethodParameterStringByParameterElements(List<? extends VariableElement> parameterElements) {
        List<String> names = new ArrayList<>(parameterElements.size());
        for (VariableElement element : parameterElements) {
            names.add(element.getSimpleName().toString());
        }
        return getMethodParameterStringByParameterNames(names);
    }

    public static String getMethodParameterStringByParameterNames(List<String> parameterNames) {
        if (parameterNames.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parameterNames.size() - 1; i++) {
            builder.append(parameterNames.get(i));
            builder.append(", ");
        }
        builder.append(parameterNames.get(parameterNames.size() - 1));
        return builder.toString();
    }
}
