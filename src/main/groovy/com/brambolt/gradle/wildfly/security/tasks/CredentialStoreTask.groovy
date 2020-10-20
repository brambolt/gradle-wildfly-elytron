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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.wildfly.security.WildFlyElytronProvider
import org.wildfly.security.credential.PasswordCredential

import java.security.Security

import static com.brambolt.wildfly.security.credential.store.CredentialStores.createClearPasswordCredential
import static com.brambolt.wildfly.security.credential.store.CredentialStores.DEFAULT_CREDENTIAL_STORE_TYPE
import static org.wildfly.security.tool.MaskCommand.decryptMasked

/**
 * Shared code for working with credential stores.
 */
abstract class CredentialStoreTask extends DefaultTask {

  String type = DEFAULT_CREDENTIAL_STORE_TYPE

  File location

  String password

  /**
   * Configures the task.
   * @param closure The configuration closure
   * @return The configured task
   */
  @Override
  Task configure(Closure closure) {
    // Add a provider for `KeyStoreCredentialStore`:
    Security.addProvider(new WildFlyElytronProvider())
    super.configure(closure)
    this
  }

  void checkConfiguration() {
    if (null == location)
      throw new GradleException('No credential store path provided, set location')
    if (null == password || password.trim().isEmpty())
      throw new GradleException('No credential store password provided, set password')
  }

  /**
   * Creates a password credential object from a parameter masked secret. The
   * parameter must be masked, and will be decrypted (unmasked) before it is
   * embedded in the credential object.
   *
   * @param secret The masked secret, prefixed with MASK-
   * @return A password credential object holding the secret
   */
  static PasswordCredential createMaskedPasswordCredential(String secret) {
    if (!secret.startsWith('MASK-'))
      throw new GradleException('Secret does not appear to be masked')
    createClearPasswordCredential(decryptMasked(secret))
  }
}

