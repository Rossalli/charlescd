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

import React from 'react';
import Text from 'core/components/Text';
import Icon from 'core/components/Icon';
import { useForm } from 'react-hook-form';
import Button from 'core/components/Button/Default';
import Styled from './styled';

type Props = {
  onGoBack: Function;
};

type ActionForm = {
  nickname: string;
};

const AddAction = ({ onGoBack }: Props) => {
  const {
    handleSubmit,
    register,
    errors,
    formState: { isValid }
  } = useForm<ActionForm>({ mode: 'onChange' });

  const onSubmit = (data: ActionForm) => {
    console.log(data);
  };

  return (
    <div data-testid="metric-group-action-form">
      <Styled.Layer>
        <Styled.Icon
          name="arrow-left"
          color="dark"
          onClick={() => onGoBack()}
        />
      </Styled.Layer>
      <Styled.Layer>
        <Text.h2 color="light">
          {/* {action?.id ? 'Update action' : 'Add action'} */}
          Add action
        </Text.h2>
      </Styled.Layer>
      <Styled.Layer>
        <Text.h5 color="dark">
          Fill in the information below to create an action.
        </Text.h5>
      </Styled.Layer>
      <Styled.Form
        onSubmit={handleSubmit(onSubmit)}
        data-testid="create-metric"
      >
        <Styled.Layer>
          <Styled.Input
            name="nickname"
            ref={register({ required: true })}
            label="Type a nickname for action"
            maxLength={100}
          />
          {!!errors.nickname && (
            <Styled.FieldErrorWrapper>
              <Icon name="error" color="error" />
              <Text.h6 color="error">{errors.nickname.message}</Text.h6>
            </Styled.FieldErrorWrapper>
          )}
          <Button
            type="submit"
            isLoading={false}
            isDisabled={!isValid}
            id="submit"
          >
            Save
          </Button>
        </Styled.Layer>
      </Styled.Form>
    </div>
  );
};

export default AddAction;