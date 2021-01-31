/*
 *
 *  Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package message

import (
	"encoding/json"
	"github.com/google/uuid"
	"hermes/internal/message/payloads"
	"hermes/pkg/errors"
	"io"
	"time"
)

type Message struct {
	ID             uuid.UUID `json:"id"`
	CreatedAt      time.Time `json:"-"`
	SubscriptionId uuid.UUID `json:"subscriptionId"`
	LastStatus     string    `json:"lastStatus"`
	EventType      string    `json:"eventType"`
	Event          string    `json:"event" gorm:"type:jsonb"`
}

func (main Main) ParsePayload(request io.ReadCloser) (payloads.PayloadRequest, errors.Error) {
	var payload *payloads.PayloadRequest
	err := json.NewDecoder(request).Decode(&payload)
	if err != nil {
		return payloads.PayloadRequest{}, errors.NewError("Parse error", err.Error()).
			WithOperations("ParsePayload.ParseDecode")
	}
	return *payload, nil
}

func (main Main) Publish(messagesRequest []payloads.Request) ([]payloads.MessageResponse, errors.Error) {
	var msgList []Message
	var ids []uuid.UUID
	var response []payloads.MessageResponse

	for _, r := range messagesRequest {
		msg := Message{
			ID:             uuid.New(),
			SubscriptionId: r.SubscriptionId,
			EventType:      r.EventType,
			Event:          string(r.Event),
		}

		msgList = append(msgList, msg)
		ids = append(ids, msg.ID)
	}

	result := main.db.Model(&Message{}).Create(&msgList).Find(&response, ids)
	if result.Error != nil {
		return []payloads.MessageResponse{}, errors.NewError("Save Message error", result.Error.Error()).
			WithOperations("Save.Result")
	}

	return response, nil
}

func (main Main) FindAllNotEnqueued() ([]payloads.MessageResponse, errors.Error) {
	var response []payloads.MessageResponse

	query := main.db.Raw(FindAllNotEnqueuedQuery).Scan(&response)
	if query.Error != nil {
		return []payloads.MessageResponse{}, errors.NewError("FindAllNotEnqueued Message error", query.Error.Error()).
			WithOperations("FindAllNotEnqueued.Query")
	}

	return response, nil
}
