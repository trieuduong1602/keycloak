/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.saml.processing.core.parsers.saml.assertion;

import org.keycloak.dom.saml.v2.assertion.AudienceRestrictionType;
import org.keycloak.saml.common.exceptions.ParsingException;
import org.keycloak.saml.common.util.StaxParserUtil;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

/**
 * Parse the <conditions> in the saml assertion
 *
 * @since Oct 14, 2010
 */
public class SAMLAudienceRestrictionParser extends AbstractStaxSamlAssertionParser<AudienceRestrictionType> {

    private static final SAMLAudienceRestrictionParser INSTANCE = new SAMLAudienceRestrictionParser();

    private SAMLAudienceRestrictionParser() {
        super(SAMLAssertionQNames.AUDIENCE_RESTRICTION);
    }

    public static SAMLAudienceRestrictionParser getInstance() {
        return INSTANCE;
    }

    @Override
    protected AudienceRestrictionType instantiateElement(XMLEventReader xmlEventReader, StartElement element) throws ParsingException {
        return new AudienceRestrictionType();
    }

    @Override
    protected void processSubElement(XMLEventReader xmlEventReader, AudienceRestrictionType target, SAMLAssertionQNames element, StartElement elementDetail) throws ParsingException {
        switch (element) {
            case AUDIENCE:
                StaxParserUtil.advance(xmlEventReader);
                String audienceValue = StaxParserUtil.getElementText(xmlEventReader);
                try {
                    target.addAudience(URI.create(URLEncoder.encode(audienceValue, "UTF-8")));
                } catch (IllegalArgumentException e) {
                    // Ignore parse error
                    LOGGER.debug("IllegalArgumentException when create URI for audience element");
                    LOGGER.debug(e.toString());
                } catch (UnsupportedEncodingException e) {
                    // Ignore encode error
                    LOGGER.debug("UnsupportedEncodingException when encode audience element");
                    LOGGER.debug(e.toString());
                }
                break;

            default:
                throw LOGGER.parserUnknownTag(StaxParserUtil.getElementName(elementDetail), elementDetail.getLocation());
        }
    }
}