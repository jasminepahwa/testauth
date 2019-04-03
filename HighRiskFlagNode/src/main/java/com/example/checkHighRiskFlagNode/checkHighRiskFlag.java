/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2017-2018 ForgeRock AS.
 */


package com.example.checkHighRiskFlagNode;

import static org.forgerock.openam.auth.node.api.SharedStateConstants.PASSWORD;
import static org.forgerock.openam.auth.node.api.SharedStateConstants.USERNAME;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.AbstractDecisionNode;
import org.forgerock.openam.auth.node.api.Action;
import org.forgerock.openam.auth.node.api.Node;
import org.forgerock.openam.auth.node.api.NodeProcessException;
import org.forgerock.openam.auth.node.api.TreeContext;
import org.forgerock.openam.core.realms.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.iplanet.sso.SSOException;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;


@Node.Metadata(outcomeProvider  = AbstractDecisionNode.OutcomeProvider.class,
               configClass      = checkHighRiskFlag.Config.class)
public class checkHighRiskFlag extends AbstractDecisionNode {

    private final Logger logger = LoggerFactory.getLogger(checkHighRiskFlag.class);
    private final Config config;
    private final Realm realm;

   
    public interface Config {
    }


    
    @Inject
    public checkHighRiskFlag(@Assisted Config config, @Assisted Realm realm) throws NodeProcessException {
        this.config = config;
        this.realm = realm;
    }

    @Override
    public Action process(TreeContext context) throws NodeProcessException {
    	String username = context.sharedState.get(USERNAME).asString();
    	AMIdentity userIdentity = IdUtils.getIdentity(username, realm.asDN());
        logger.info("userIdentity", userIdentity);
        System.out.println("userIdentity:  "+ userIdentity);
        try {
        	if (userIdentity != null && userIdentity.isExists() && userIdentity.isActive()
                   && isHighRiskFlag(userIdentity) ) {
        		Map userattr=userIdentity.getAttributes();
        		Set<String> email=userIdentity.getAttribute("mail");
        		System.out.println("Userattr: "+userattr);
        		System.out.println("Email: "+email);
        		return goTo(true).build();
                
            }
          
        } catch (IdRepoException | SSOException e) {
            logger.warn("Error locating user '{}' ", username, e);
        }
        return goTo(false).build();
    
    }

    private boolean isHighRiskFlag(AMIdentity userIdentity) {
        try {
            Set<String> mail = userIdentity.getAttribute("mail");
            System.out.println("mail: "+mail);
            for (String group : mail) {
                if (group.contains("jpahwa@simeiosolutions.com")) {
                	System.out.println("High Risk Flag is True");
                    return true;
                }
              }
        } catch (IdRepoException | SSOException e) {
            logger.warn("Could not find Migration Flag for user {}", userIdentity);
        }
        System.out.println("High Risk Flag is False");
        return false;
    }
}
