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

package io.charlescd.circlematcher.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import org.springframework.util.Assert;

public class KeyMetadata {

    private String reference;

    private String key;

    private SegmentationType type;

    private String name;

    private String circleId;

    private String workspaceId;

    private Boolean isDefault;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer percentage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean active;

    private LocalDateTime createdAt;

    public KeyMetadata() {
    }

    public KeyMetadata(String key, Segmentation segmentation) {
        this.reference = segmentation.getReference();
        this.key = key;
        this.type = segmentation.getType();
        this.circleId = segmentation.getCircleId();
        this.name = segmentation.getName();
        this.workspaceId = segmentation.getWorkspaceId();
        this.isDefault = segmentation.getIsDefault();
        this.percentage = segmentation.getPercentage();
        this.active = segmentation.isActive();
        this.createdAt = segmentation.getCreatedAt();
    }

    public String getReference() {
        return reference;
    }

    public String getKey() {
        return key;
    }

    public SegmentationType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCircleId() {
        return circleId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public boolean isPercentage() {
        return type.equals(SegmentationType.PERCENTAGE);
    }

    public Integer getPercentage() {
        return this.percentage;
    }

    public void setPercentage(Integer percentage) {
        this.validatePercentage(percentage, "Percentage must be between 0 and 100");
        this.percentage = percentage;
    }

    private void validatePercentage(Integer percentage, String message) {
        if (percentage != null) {
            Assert.isTrue(percentage <= 100 && percentage >= 0, message);
        }
    }

    public int sumPercentage(Integer percentageToSum) {
        this.validatePercentage(
                this.percentage + percentageToSum, "Sum of percentage of circles exceeded 100 or is lower than 0"
        );
        return this.percentage += percentageToSum;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
