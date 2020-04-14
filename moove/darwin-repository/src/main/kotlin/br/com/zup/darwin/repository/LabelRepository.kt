/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package br.com.zup.darwin.repository

import br.com.zup.darwin.entity.Label
import org.springframework.data.jpa.repository.JpaRepository

interface LabelRepository : JpaRepository<Label, String> {
    fun findAllByIdIn(ids: List<String>): List<Label>
}