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

package io.charlescd.moove.legacy.moove.service

import io.charlescd.moove.commons.exceptions.NotFoundExceptionLegacy
import io.charlescd.moove.commons.representation.UserRepresentation
import io.charlescd.moove.legacy.moove.request.user.UpdateUserRequest
import io.charlescd.moove.legacy.repository.UserRepository
import io.charlescd.moove.legacy.repository.entity.User
import spock.lang.Specification

import java.time.LocalDateTime

class UserServiceLegacyGroovyUnitTest extends Specification {

    private String decodedEmail = "email"
    private User user = new User(
            "81861b6f-2b6e-44a1-a745-83e298a550c9",
            "John Doe",
            decodedEmail,
            "https://www.photos.com/johndoe",
            false,
            LocalDateTime.now()
    )

    private User root = new User(
            "82861b6f-2b6e-44a1-a745-83e298a550c2",
            "John Doe Root",
            decodedEmail,
            "https://www.photos.com/johndoe",
            true,
            LocalDateTime.now()
    )
    private UserRepresentation representation = new UserRepresentation(
            "81861b6f-2b6e-44a1-a745-83e298a550c9",
            "John Doe",
            decodedEmail,
            "https://www.photos.com/johndoe",
            false,
            LocalDateTime.now()
    )

    private UserRepository repository = Mock(UserRepository)
    private KeycloakServiceLegacy keycloakServiceLegacy = Mock(KeycloakServiceLegacy)
    private KeycloakService keycloackService = Mock(KeycloakService)
    private UserServiceLegacy service
    private Boolean idmEnabled = true

    def setup() {
        service = new UserServiceLegacy(repository, keycloackService, keycloakServiceLegacy, idmEnabled)
    }

    def "should update user by id"() {
        given:
        def request = new UpdateUserRequest("John Doe", "email", "https://www.photos.com/johndoe")

        when:
        def response = service.update(representation.id, request)

        then:
        1 * repository.findById(representation.id) >> Optional.of(user)
        1 * repository.saveAndFlush(user) >> user
        response.id == representation.id
        response.name == request.name
        response.photoUrl == request.photoUrl
    }

    def "should throw exception on update if user id do not exist"() {
        given:
        def request = new UpdateUserRequest("John Doe", "email", "https://www.photos.com/johndoe")

        when:
        service.update("test", request)

        then:
        1 * repository.findById("test") >> Optional.empty()
        def ex = thrown(NotFoundExceptionLegacy)
        ex.resourceName == "user"
        ex.id == "test"
    }

    def "should delete by id"() {
        when:
        def response = service.delete("81861b6f-2b6e-44a1-a745-83e298a550c9")

        then:
        1 * repository.findById("81861b6f-2b6e-44a1-a745-83e298a550c9") >> Optional.of(user)
        1 * keycloackService.deleteUserByEmail('email')
        1 * repository.delete(user)
        response.id == representation.id
        response.name == representation.name
        response.photoUrl == representation.photoUrl
        notThrown()
    }

    def "should throw exception on delete if user id do not exist"() {
        when:
        service.delete("test")

        then:
        1 * repository.findById("test") >> Optional.empty()
        def ex = thrown(NotFoundExceptionLegacy)
        ex.resourceName == "user"
        ex.id == "test"
    }

    def "should get user by id"() {

        when:
        def response = service.findUser(representation.id)

        then:
        1 * repository.findById(representation.id) >> Optional.of(user)
        response.id == representation.id
    }

    def "should throw NotFoundException when get invalid user by id"() {

        when:
        service.findUser(representation.id)

        then:
        1 * repository.findById(representation.id) >> Optional.empty()
        thrown(NotFoundExceptionLegacy)
    }

    def "should get users "() {

        given:
        def ids = ['id']

        when:
        service.findUsers(ids)

        then:
        1 * repository.findAllById(ids) >> [user]
        notThrown()
    }

    def "should throw NotFoundException when get invalid users"() {

        given:
        def ids = ['id']

        when:
        service.findUsers(ids)

        then:
        1 * repository.findAllById(ids) >> []
        thrown(NotFoundExceptionLegacy)
    }

    def "should get user by authorization header"() {

        given:
        def authorization = "Bearer qwerty"
        def email = "email@email.com"

        when:
        service.findByToken(authorization)

        then:
        1 * keycloakServiceLegacy.getEmailByToken(authorization) >> email
        1 * repository.findByEmail(email) >> Optional.of(user)
    }

    def "should throw NotFoundException when get invalid user by authorization header"() {

        given:
        def authorization = "Bearer qwerty"
        def email = "email@email.com"

        when:
        service.findByToken(authorization)

        then:
        1 * keycloakServiceLegacy.getEmailByToken(authorization) >> email
        1 * repository.findByEmail(email) >> Optional.empty()
        thrown(NotFoundExceptionLegacy)
    }
}
