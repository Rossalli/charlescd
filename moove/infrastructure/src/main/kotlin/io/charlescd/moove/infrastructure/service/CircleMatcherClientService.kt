/*
 *
 *  * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.charlescd.moove.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.charlescd.moove.domain.*
import io.charlescd.moove.domain.service.CircleMatcherService
import io.charlescd.moove.infrastructure.repository.JdbcKeyValueRuleRepository
import io.charlescd.moove.infrastructure.service.client.CircleMatcherClient
import io.charlescd.moove.infrastructure.service.client.request.CircleMatcherRequest
import io.charlescd.moove.infrastructure.service.client.request.IdentifyRequest
import io.charlescd.moove.infrastructure.service.client.request.Node
import java.net.URI
import org.springframework.stereotype.Service

@Service
class CircleMatcherClientService(
    private val circleMatcherClient: CircleMatcherClient,
    private val jdbcKeyValueRuleRepository: JdbcKeyValueRuleRepository,
    private val objectMapper: ObjectMapper
) : CircleMatcherService {

    override fun create(circle: Circle, matcherUri: String, active: Boolean) {
        this.circleMatcherClient.create(URI(matcherUri), createMatcherRequest(circle, active))
    }

    override fun update(circle: Circle, previousReference: String, matcherUri: String, active: Boolean) {
        this.circleMatcherClient.update(
            URI(matcherUri),
            previousReference,
            createMatcherRequest(circle, active, previousReference)
        )
    }

    override fun delete(reference: String, matcherUri: String) {
        this.circleMatcherClient.delete(URI(matcherUri), reference)
    }

    override fun createImport(circle: Circle, nodes: List<JsonNode>, matcherUri: String, active: Boolean) {
        this.circleMatcherClient.createImport(
            URI(matcherUri),
            createImportRequest(nodes, circle, null, active)
        )
    }

    override fun updateImport(circle: Circle, previousReference: String, nodes: List<JsonNode>, matcherUri: String, active: Boolean) {
        this.circleMatcherClient.updateImport(URI(matcherUri), createImportRequest(nodes, circle, previousReference, active))
    }

    override fun identify(workspace: Workspace, request: Map<String, Any>): List<SimpleCircle> {
        return this.circleMatcherClient.identify(
            URI(workspace.circleMatcherUrl!!),
            IdentifyRequest(
                workspace.id,
                request
            )
        ).circles.map { SimpleCircle(it.id, it.name) }
    }

    private fun createImportRequest(
        nodes: List<JsonNode>,
        circle: Circle,
        previousReference: String? = null,
        active: Boolean
    ): List<CircleMatcherRequest> {
        return nodes.map { createImportMatcherRequest(circle, it, previousReference, active) }
    }

    private fun createImportMatcherRequest(circle: Circle, jsonNode: JsonNode, previousReference: String? = null, isActive: Boolean) =
        CircleMatcherRequest(
            name = circle.name,
            reference = circle.reference,
            previousReference = previousReference,
            node = objectMapper.treeToValue(
                jsonNode,
                Node::class.java
            ),
            circleId = circle.id,
            type = circle.matcherType.name,
            workspaceId = circle.workspaceId,
            isDefault = circle.defaultCircle,
            active = isActive,
            createdAt = circle.createdAt,
            percentage = circle.percentage
        )

    private fun createMatcherRequest(circle: Circle, isActive: Boolean, previousReference: String? = null): CircleMatcherRequest =
        CircleMatcherRequest(
            name = circle.name,
            reference = circle.reference,
            previousReference = previousReference,
            node = circle.rules?.let {
                objectMapper.treeToValue(
                    it,
                    Node::class.java
                )
            },
            circleId = circle.id,
            type = circle.matcherType.name,
            workspaceId = circle.workspaceId,
            isDefault = circle.defaultCircle,
            createdAt = circle.createdAt,
            percentage = circle.percentage,
            active = isActive
        )

    override fun deleteAllFor(circles: Circles, matcherUri: String) {
        circles.getReferences().forEach { delete(it, matcherUri) }
    }

    override fun saveAllFor(circles: Circles, matcherUri: String) {
        circles.forEach { circle ->
            if (circle.matcherType == MatcherTypeEnum.SIMPLE_KV) {
                val rules = jdbcKeyValueRuleRepository.findByCircle(circle.id)
                rules.map { it.rule }.chunked(100).forEach {
                    createImport(circle, it, matcherUri, circle.active)
                }
            } else {
                create(circle, matcherUri, circle.active)
            }
        }
    }
}
