package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.PreferenceValue;
import org.jboss.seam.wiki.preferences.PreferenceProvider;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceRegistry;
import org.hibernate.validator.InvalidValue;

import static org.jboss.seam.international.StatusMessage.Severity.WARN;

import java.util.*;
import java.io.Serializable;

@Name("prefEditor")
@Scope(ScopeType.CONVERSATION)
public class PreferenceEditor implements Serializable {

    @Logger static Log log;

    @In
    private StatusMessages statusMessages;

    @In
    PreferenceProvider preferenceProvider;

    @In
    PreferenceRegistry preferenceRegistry;

    private User user;
    private PreferenceVisibility[] visibilities;
    private List<PreferenceEntity> preferenceEntities;
    private PreferenceEntity preferenceEntity;
    private List<PreferenceValue> preferenceValues;
    boolean valid = true;

    public String save() {
        log.debug("saving preference values");
        if (preferenceEntity == null) return null;

        validate();
        if (!valid) return "failed";

        preferenceProvider.storeValues(new HashSet<PreferenceValue>(preferenceValues), user, null);
        preferenceProvider.flush();
        log.debug("completed saving of preference values");

        return null;
    }

    public void validate() {
        log.debug("validating preference component values");
        if (preferenceEntity == null) return;
        valid = true;
        Map<PreferenceEntity.Property, InvalidValue[]> invalidProperties =
                preferenceEntity.validate(preferenceValues, Arrays.asList(visibilities));

        for (Map.Entry<PreferenceEntity.Property, InvalidValue[]> entry : invalidProperties.entrySet()) {
            for (InvalidValue validationError : entry.getValue()) {
                valid = false;

                statusMessages.addToControlFromResourceBundleOrDefault(
                    "preferenceValidationErrors",
                    WARN,
                    "preferenceValueValidationFailed." + preferenceEntity.getEntityName() + "." + entry.getKey().getFieldName(),
                    preferenceEntity.getDescription() + " - '" + entry.getKey().getDescription() + "': " + validationError.getMessage());
            }
        }
    }

    public void selectPreferenceEntity(PreferenceEntity selectedPreferenceEntity) {
        preferenceEntity = selectedPreferenceEntity;
        log.debug("selected preference entity: " + preferenceEntity);
        preferenceValues =
                new ArrayList<PreferenceValue>(
                    preferenceProvider.loadValues(preferenceEntity.getEntityName(),
                                                      user,
                                                      null,
                                                      Arrays.asList(visibilities))
                );
    }

    private void loadPreferenceEntities() {
        preferenceEntities = new ArrayList<PreferenceEntity>(preferenceRegistry.getPreferenceEntities(visibilities));
    }

    public List<PreferenceEntity> getPreferenceEntities() {
        if (preferenceEntities == null) loadPreferenceEntities();
        return preferenceEntities;
    }

    public PreferenceEntity getPreferenceEntity() {
        return preferenceEntity;
    }

    public List<PreferenceValue> getPreferenceValues() {
        return preferenceValues;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setVisibilities(PreferenceVisibility[] visibilities) {
        this.visibilities = visibilities;
    }

    public boolean isValid() {
        return valid;
    }
}