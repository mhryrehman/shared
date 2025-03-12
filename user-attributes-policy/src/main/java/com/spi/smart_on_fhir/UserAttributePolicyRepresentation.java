/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spi.smart_on_fhir;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.keycloak.representations.idm.authorization.AbstractPolicyRepresentation;
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAttributePolicyRepresentation extends AbstractPolicyRepresentation {

    private String allowedAttributes;
    private String userAttributeKey;
    private boolean matchAll;


    @Override
    public String getType() {
        return "user-attribute";
    }

    public String getAllowedAttributes() {
        return allowedAttributes;
    }

    public void setAllowedAttributes(String allowedAttributes) {
        this.allowedAttributes = allowedAttributes;
    }

    public String getUserAttributeKey() {
        return userAttributeKey;
    }

    public void setUserAttributeKey(String userAttributeKey) {
        this.userAttributeKey = userAttributeKey;
    }

    public boolean isMatchAll() {
        return matchAll;
    }

    public void setMatchAll(boolean matchAll) {
        this.matchAll = matchAll;
    }
}
