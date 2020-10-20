/*
 * Copyright 2020 Brambolt ehf.
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

package com.brambolt.gradle.wildfly.security.tasks

import com.brambolt.wildfly.security.credential.store.CredentialStores
import org.gradle.api.GradleException
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.wildfly.security.credential.store.CredentialStore

import static com.brambolt.wildfly.security.credential.store.CredentialStores.createClearPasswordCredential

/**
 * A Gradle task for creating a credential store.
 */
class AddAlias extends CredentialStoreTask {

  /**
   * The alias to add.
   */
  String alias

  /**
   * The secret.
   */
  String secret

  /**
   * True iff the secret is masked.
   */
  Boolean mask = true

  @OutputFile
  File getOutputFile() {
    location
  }

  /**
   * Executes the task.
   */
  @TaskAction
  void apply() {
    checkConfiguration()
    CredentialStore credentialStore = CredentialStores.create(type, outputFile, password)
    credentialStore.store(alias,
      (mask
        ? createMaskedPasswordCredential(secret)
        : createClearPasswordCredential(secret)))
    credentialStore.flush()
  }

  void checkConfiguration() {
    super.checkConfiguration()
    if (null == alias || alias.trim().isEmpty())
      throw new GradleException('No alias provided; configure alias value')
    if (null == secret || secret.trim().isEmpty())
      throw new GradleException('No secret provided for alias; configure secret value')
  }
}

