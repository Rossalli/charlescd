/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.charlescd.moove.infrastructure.service.client.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class CircleMatcherRequest(
    val name: String,
    val reference: String,
    val node: Node?,
    val circleId: String,
    val type: String,
    val previousReference: String? = null,
    val workspaceId: String,
    @get:JsonProperty("isDefault")
    val isDefault: Boolean,
    val createdAt: LocalDateTime,
    val percentage: Int? = null,
    val active: Boolean
)

data class Node @JsonCreator constructor(
    @JsonProperty("type")
    val type: NodeTypeRequest?,
    @JsonProperty("logicalOperator")
    val logicalOperator: LogicalOperatorRequest?,
    @JsonProperty("clauses")
    val clauses: List<Node>?,
    @JsonProperty("content")
    val content: Rule?
) {
    enum class NodeTypeRequest {
        CLAUSE,
        RULE
    }

    enum class LogicalOperatorRequest {
        AND,
        OR
    }

    data class Rule @JsonCreator constructor(
        @JsonProperty("key")
        val key: String?,
        @JsonProperty("condition")
        val condition: String?,
        @JsonProperty("value")
        val value: List<String>?
    )
}

data class IdentifyRequest(
    val workspaceId: String,
    val requestData: Map<String, Any>
)
