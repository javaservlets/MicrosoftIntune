package com.example.microsoft;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.identity.shared.debug.Debug;
import org.forgerock.json.JsonValue;
import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.*;
import org.forgerock.openam.core.CoreWrapper;
import org.forgerock.util.i18n.PreferredLocales;

import java.util.List;

@Node.Metadata(outcomeProvider = Intune.MyOutcomeProvider.class, configClass = Intune.Config.class)

public class Intune implements Node {
    private final Config config;
    private final CoreWrapper coreWrapper;
    private final static String DEBUG_FILE = "microsoft";
    protected Debug debug = Debug.getInstance(DEBUG_FILE);
    JsonValue context_json;

    @Override
    public Action process(TreeContext context) throws NodeProcessException {
        debug.error("+++     starting microsoft");

        context_json = context.sharedState.copy();
        String search_key = context_json.get("device_id").asString();

        UserInfo userinfo = new UserInfo(config.msTokenUrl(), config.msScope(), config.msAdmin(), config.msPassword(), config.msClientId(), config.msClientSecret());
        String status = userinfo.getStatus(config.msComplianceUrl(), search_key); // qry could be for either "is" ENROLLED or COMPLIANT
        Action action = null ;

        debug.error("+++   finally  " + status.toString());

        if (status.equals("compliant")) {
            action = goTo(MyOutcome.COMPLIANT).build();
        } else if (status.equals("noncompliant")) {
            action = goTo(MyOutcome.NONCOMPLIANT).build();
        } else if (status.equals("unknown")) {
            action = goTo(MyOutcome.UNKNOWN).build();
        } else {
            action = goTo(MyOutcome.CONNECTION_ERROR).build();
        }
        return action;
    }


    public enum MyOutcome {
        /**
         * Successful parsing of cert for a dev id.
         */
        COMPLIANT,
        /**
         * dev id found in cert but device isn't compliant
         */
        NONCOMPLIANT,
        /**
         * no device found with ID from cert
         */
        UNKNOWN,
        /**
         * no connection to mdm
         */
        CONNECTION_ERROR,
    }

    private Action.ActionBuilder goTo(MyOutcome outcome) {
        return Action.goTo(outcome.name());
    }

    public static class MyOutcomeProvider implements org.forgerock.openam.auth.node.api.OutcomeProvider {
        @Override
        public List<Outcome> getOutcomes(PreferredLocales locales, JsonValue nodeAttributes) {
            return ImmutableList.of(
                    new Outcome(MyOutcome.COMPLIANT.name(), "Compliant"),
                    new Outcome(MyOutcome.NONCOMPLIANT.name(), "Non Compliant"),
                    new Outcome(MyOutcome.UNKNOWN.name(), "Unknown"),
                    new Outcome(MyOutcome.CONNECTION_ERROR.name(), "Connection Error"));
        }
    }

    public interface Config {

        @Attribute(order = 100)
        default String msScope() {
            return "https://graph.microsoft.com/.default";
        }

        @Attribute(order = 200)
        default String msClientId() {
            return "cb17ccd4-0e70-48dc-a694-e6910418c70b";
        }

        @Attribute(order = 300)
        default String msClientSecret() {
            return "[a)PaGdK1*|0Ci1q";
        }

        @Attribute(order = 400)
        default String msTokenUrl() {
            return "https://login.microsoftonline.com/94781b09-3000-41eb-93bc-c7915241c40e/oauth2/v2.0/token";
        }

        @Attribute(order = 500)
        default String msComplianceUrl() {
            return "https://graph.microsoft.com/v1.0/deviceManagement/manageddevices/";
        }

        @Attribute(order = 600)
        default String msAdmin() {
            return "info@javaservlets.onmicrosoft.com";
        }

        @Attribute(order = 700)
        default String msPassword() {
            return "Ch2019angeit!";
        }

    }

    @Inject
    public Intune(@Assisted Config config, CoreWrapper coreWrapper) throws NodeProcessException {
        this.config = config;
        this.coreWrapper = coreWrapper;
    }

}