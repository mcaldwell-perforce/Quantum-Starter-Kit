package com.quantum.listeners;

import com.google.gson.Gson;
import com.qmetry.qaf.automation.step.StepExecutionTracker;
import com.qmetry.qaf.automation.step.TestStep;
import com.qmetry.qaf.automation.step.client.text.BDDDefinitionHelper;
import com.quantum.utils.ConsoleUtils;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;

public class QuantumReportiumListenerPatch extends QuantumReportiumListener {
    @Override
    public void beforExecute(StepExecutionTracker stepExecutionTracker) {
        String stepDescription = getProcessStepDescription(stepExecutionTracker.getStep());
        String msg = "BEGIN STEP: " + stepDescription;
        ConsoleUtils.logInfoBlocks(msg, ConsoleUtils.lower_block + " ", 10);
        if (!stepDescription.contains("AI") || stepDescription.contains("AI Validates"))
            logStepStart(stepDescription);
    }

    @Override
    public void afterExecute(StepExecutionTracker stepExecutionTracker) {
        if (!stepExecutionTracker.getStep().getDescription().contains("AI") || stepExecutionTracker.getStep().getDescription().contains("AI Validates"))
            logStepEnd();
        String msg = "END STEP: " + stepExecutionTracker.getStep().getDescription();
        ConsoleUtils.logInfoBlocks(msg, ConsoleUtils.upper_block + " ", 10);
    }

    @SuppressWarnings("unchecked")
    private String getProcessStepDescription(TestStep step) {
        // process parameters in step;

        String description = step.getDescription();

        // if (step instanceof CustomStep) {

        Object[] actualArgs = step.getActualArgs();
        String def = step.getDescription();

        if ((actualArgs != null) && (actualArgs.length > 0)) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.putAll(step.getStepExecutionTracker().getContext());
            List<String> paramNames = getArgNames(def);

            if ((paramNames != null) && (!paramNames.isEmpty())) {

                for (int i = 0; i < paramNames.size(); i++) {
                    String paramName = paramNames.get(i).trim();
                    // remove starting { and ending } from parameter name
                    paramName = paramName.substring(1, paramName.length() - 1).split(":", 2)[0];

                    // in case of data driven test args[0] should not be overriden
                    // with steps args[0]
                    if ((actualArgs[i] instanceof String)) {

                        String pstr = (String) actualArgs[i];

                        if (pstr.startsWith("${") && pstr.endsWith("}")) {
                            String pname = pstr.substring(2, pstr.length() - 1);
                            actualArgs[i] = paramMap.containsKey(pstr) ? paramMap.get(pstr)
                                    : paramMap.containsKey(pname) ? paramMap.get(pname)
                                    : getBundle().containsKey(pstr) ? getBundle().getObject(pstr)
                                    : getBundle().getObject(pname);
                        } else if (pstr.indexOf("$") >= 0) {
                            pstr = getBundle().getSubstitutor().replace(pstr);
                            actualArgs[i] = StrSubstitutor.replace(pstr, paramMap);
                        }
                        // continue;
                        BDDDefinitionHelper.ParamType ptype = BDDDefinitionHelper.ParamType.getType(pstr);
                        if (ptype.equals(BDDDefinitionHelper.ParamType.MAP)) {
                            Map<String, Object> kv = new Gson().fromJson(pstr, Map.class);
                            paramMap.put(paramName, kv);
                            for (String key : kv.keySet()) {
                                paramMap.put(paramName + "." + key, kv.get(key));
                            }
                        } else if (ptype.equals(BDDDefinitionHelper.ParamType.LIST)) {
                            List<Object> lst = new Gson().fromJson(pstr, List.class);
                            paramMap.put(paramName, lst);
                            for (int li = 0; li < lst.size(); li++) {
                                paramMap.put(paramName + "[" + li + "]", lst.get(li));
                            }
                        }
                    }

                    paramMap.put("${args[" + i + "]}", actualArgs[i]);
                    paramMap.put("args[" + i + "]", actualArgs[i]);
                    paramMap.put(paramName, actualArgs[i]);

                }

                description = StrSubstitutor.replace(description, paramMap);

            }
        }
        return description;
    }
}
