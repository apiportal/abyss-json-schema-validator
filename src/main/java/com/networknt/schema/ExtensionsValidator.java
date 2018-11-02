/*
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class ExtensionsValidator extends BaseJsonValidator implements JsonValidator {
    private static final Logger logger = LoggerFactory.getLogger(ExtensionsValidator.class);

    private JsonType schemaType;
    private UnionTypeValidator unionTypeValidator;

    public ExtensionsValidator(String schemaPath, JsonNode schemaNode, JsonSchema parentSchema, ValidationContext validationContext) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.EXTENSIONS, validationContext);
        schemaType = TypeFactory.getSchemaNodeType(schemaNode);

        if (schemaType == JsonType.UNION) {
            unionTypeValidator = new UnionTypeValidator(schemaPath, schemaNode, parentSchema, validationContext);
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonNode node, JsonNode rootNode, String at) {
        debug(logger, node, rootNode, at);

        //TODO: Burayı değiştir.
        System.out.println("Extensions Validator Invoked...at:[" + at + "]\nNode:[" + node.asText() + "]\nPrivacyConfig:[" + getSchemaNode().get("x-abyss-privacy").toString() + "]");

        /*x-abyss-privacy:
        {
            "attributeClass": "id"/"qid"/"sensitive"/"auxiliary",
                "action": "remove"/"mask"/"generalize"/"passThrough",
                "matchPattern":"<regex>",
                "maskPattern":"<regex_replace>",
                "generalizationLevel":"2"
        }*/

        String action = getSchemaNode().get("x-abyss-privacy").get("action").asText();

        if (action == null || action.isEmpty()) {
            return Collections.emptySet();
        }

        if (!action.equals("passThrough")) {
            String matchPattern = getSchemaNode().get("x-abyss-privacy").get("matchPattern").asText();

            if (matchPattern == null || matchPattern.isEmpty()) {
                return Collections.emptySet();
            } else {
                String originalData = node.asText();

                if (originalData.matches(matchPattern)) {
                    return Collections.singleton(buildValidationMessage(at, getSchemaPath(), action, originalData, matchPattern));
                } else {
                    return Collections.emptySet();
                }
            }
        }

        //For Handling of Data Removal, Masking and Generalization Operations
        if (action.equals("remove")) {
            System.out.println("Action1:[" + action + "]");
        } else if (action.equals("mask")) {
            System.out.println("Action2:[" + action + "]");
        } else if (action.equals("generalize")) {
            System.out.println("Action3:[" + action + "]");
        } else if (action.equals("passThrough")) {
            System.out.println("Action4:[" + action + "]");
        } else {
            System.out.println("Action5:[" + action + "]");
            return Collections.emptySet();
        }


        //getSchemaPath().split("/")[getSchemaPath().split("/").length-1]
        //getSchemaPath().split("/")[getSchemaPath().split("/").length-2]


/*
        if (schemaType == JsonType.UNION) {
            return unionTypeValidator.validate(node, rootNode, at);
        }

        JsonType nodeType = TypeFactory.getValueNodeType(node);
        if (nodeType != schemaType) {
            if (schemaType == JsonType.ANY) {
                return Collections.emptySet();
            }

            if (schemaType == JsonType.NUMBER && nodeType == JsonType.INTEGER) {
                return Collections.emptySet();
            }

           return Collections.singleton(buildValidationMessage(at, nodeType.toString(), schemaType.toString()));
        }
*/
        return Collections.emptySet();
    }

}
