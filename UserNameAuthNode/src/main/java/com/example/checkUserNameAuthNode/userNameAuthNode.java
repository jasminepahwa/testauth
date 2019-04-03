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


package com.example.checkUserNameAuthNode;

import static org.forgerock.openam.auth.node.api.SharedStateConstants.USERNAME;

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



@Node.Metadata(outcomeProvider  = AbstractDecisionNode.OutcomeProvider.class,
               configClass      = userNameAuthNode.Config.class)
public class userNameAuthNode extends AbstractDecisionNode {

    private final Logger logger = LoggerFactory.getLogger(userNameAuthNode.class);
    private final Config config;
    private final Realm realm;

    public interface Config {
        
    }


    @Inject
    public userNameAuthNode(@Assisted Config config, @Assisted Realm realm) throws NodeProcessException {
        this.config = config;
        this.realm = realm;
    }

    @Override
    public Action process(TreeContext context) throws NodeProcessException {
    	String username = context.sharedState.get(USERNAME).asString();
		System.out.println("username: " + username);

		Pattern pattern = Pattern.compile("^[\\a-zA-Z]*$");
		if (username != null && !username.isEmpty() && !pattern.matcher(username).matches()) {
			System.out.println("User Name Validation Failed");
            return goTo(false).build();
        }

		else {
			System.out.println("User Name Validation Passed");
                return goTo(true)
                        .replaceSharedState(context.sharedState.copy().put(USERNAME, username))
                        .build();}
         
 
    }

   
}
