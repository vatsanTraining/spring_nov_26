package com.example.demo.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.model.EndowmentPolicy;
import com.example.demo.model.Policy;
import com.example.demo.model.TermLifePolicy;
import com.example.demo.service.*;

public class PolicyBillingProcessor {
    private final Map<Class<? extends Policy>, AbstractPolicyService<? extends Policy>> serviceRegistry = new HashMap<>();

    public PolicyBillingProcessor() {
    	    serviceRegistry.put(TermLifePolicy.class, new TermLifeService());
    	    serviceRegistry.put(EndowmentPolicy.class, new EndowmentPolicyService());
    	}
  

    @SuppressWarnings("unchecked")
    public <T extends Policy> double computePremium(T policy) {
        AbstractPolicyService<T> service =
            (AbstractPolicyService<T>) serviceRegistry.get(policy.getClass());

        if (service == null) {
            throw new UnsupportedOperationException("No service registered for " + policy.getClass().getSimpleName());
        }
        return service.calculateAnnualPremium(policy);
    }

    public void runBatchBilling(List<Policy> policies) {
        for (Policy policy : policies) {
            double premium = computePremium(policy);
            System.out.printf("Policy [%s] (%s) -> Annual Premium: %.2f%n",
                policy.getPolicyNumber(), policy.getClass().getSimpleName(), premium);
        }
    }
}
