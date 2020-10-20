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
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.wildfly.security.credential.store.CredentialStore

/**
 * A Gradle task for retrieving a cleartext password from a credential store.
 */
class RetrieveClearPassword extends CredentialStoreTask {

  /**
   * The alias to retrieve a cleartext password for.
   */
  String alias

  /**
   * The credential store to retrieve from.
   * @return The file system location of the credential store
   */
  @InputFile
  File getInputFile() {
    location
  }

  /**
   * The password will be stored to this variable.
   */
  String secret

  /**
   * Executes the task.
   */
  @TaskAction
  void apply() {
    checkConfiguration()
    CredentialStore store = CredentialStores.create(type, inputFile, password)
    secret = new String(CredentialStores.retrieveClearPassword(store, alias))
  }
}

