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

import React, { Suspense, useState, useEffect } from 'react';
import { Switch, Route, useHistory } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import isEmpty from 'lodash/isEmpty';
import { isNotBlank, maxValue, required } from 'core/utils/validation';
import Text from 'core/components/Text';
import Icon from 'core/components/Icon';
import Page from 'core/components/Page';
import Modal from 'core/components/Modal';
import routes from 'core/constants/routes';
import { useGlobalState } from 'core/state/hooks';
import { getProfileByKey } from 'core/utils/profile';
import { isRequired, maxLength } from 'core/utils/validations';
import Menu from './Menu';
import Tabs from './Tabs';
import { addParamUserGroup, getSelectedUserGroups } from './helpers';
import { useFindAllUserGroup, useCreateUserGroup } from './hooks';
import Styled from './styled';

export enum FormAction {
  view = 'view',
  edit = 'edit'
}

const UserGroups = () => {
  const profileName = getProfileByKey('name');
  const history = useHistory();
  const [search, setSearch] = useState('');
  const [toggleModal, setToggleModal] = useState(false);
  const [isDisabled, setIsDisabled] = useState(true);
  const [getUserGroups, loading] = useFindAllUserGroup();
  const { list } = useGlobalState(state => state.userGroups);
  const { register, watch, handleSubmit, errors } = useForm({
    mode: 'onChange'
  });
  const watchName = watch('name');
  const {
    createUserGroup,
    response: userGroupResponse,
    loading: loadingCreate
  } = useCreateUserGroup();

  useEffect(() => {
    setIsDisabled(isEmpty(watchName));
  }, [watchName]);

  useEffect(() => {
    getUserGroups(search);
  }, [search, getUserGroups]);

  useEffect(() => {
    if (userGroupResponse) {
      setToggleModal(false);
    }
  }, [userGroupResponse]);

  const onSubmit = ({ name }: Record<string, string>) => {
    createUserGroup(name);
  };

  const renderModal = () =>
    toggleModal && (
      <Modal.Default onClose={() => setToggleModal(false)}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <Styled.Modal.Title color="light">New user group</Styled.Modal.Title>
          <Styled.Modal.Input
            name="name"
            label="Type a name"
<<<<<<< HEAD
            ref={register({
              required: required(),
              maxLength: maxValue(64),
              validate: isNotBlank
=======
            error={errors?.name?.message}
            ref={register({
              required: isRequired(),
              maxLength: maxLength()
>>>>>>> aea819ab22120e5f192f4fae6442c88b06571467
            })}
          />
          {!!errors.name && (
            <Styled.FieldErrorWrapper>
              <Icon name="error" color="error" />
              <Text.h6 color="error">{errors.name.message}</Text.h6>
            </Styled.FieldErrorWrapper>
          )}
          <Styled.Modal.Button
            type="submit"
            isDisabled={isDisabled}
            isLoading={loadingCreate}
          >
            Create user group
          </Styled.Modal.Button>
        </form>
      </Modal.Default>
    );

  return (
    <Page>
      {renderModal()}
      <Page.Menu>
        <Menu
          items={list?.content}
          isLoading={loading}
          selectedItems={getSelectedUserGroups()}
          onSelect={id =>
            addParamUserGroup(history, `${id}~${FormAction.view}`)
          }
          onSearch={setSearch}
          onCreate={() => setToggleModal(true)}
        />
      </Page.Menu>
      <Suspense fallback="">
        <Switch>
          <Route path={routes.groupsShow} component={Tabs} />
          <Route path={routes.groups}>
            <Page.Placeholder
              icon="empty-groups"
              title={`Hello, ${profileName}!`}
              subtitle="Create user group using the side menu."
            />
          </Route>
        </Switch>
      </Suspense>
    </Page>
  );
};

export default UserGroups;
