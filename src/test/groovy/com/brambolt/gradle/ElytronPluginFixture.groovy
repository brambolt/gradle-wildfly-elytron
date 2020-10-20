/*
 * Copyright 2017-2020 Brambolt ehf.
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

package com.brambolt.gradle

import org.junit.rules.TemporaryFolder

import static com.brambolt.gradle.testkit.Builds.createBuildFile

class ElytronPluginFixture {

  static applyFalse(TemporaryFolder testProjectDir) {
    createBuildFile('build-no-apply.gradle', """
plugins {
  id 'com.brambolt.gradle.wildfly.elytron' apply false
}
""", testProjectDir)
  }

  static applyOnly(TemporaryFolder testProjectDir) {
    createBuildFile('build-apply-only.gradle', """
plugins {
  id 'com.brambolt.gradle.wildfly.elytron' 
}
""", testProjectDir)
  }

  static executeTasks(TemporaryFolder testProjectDir) {
    createBuildFile('build-execute-tasks.gradle', """
plugins {
  id 'com.brambolt.gradle.wildfly.elytron' 
}

ext {
  elytronStoreLocation = new File(project.projectDir, 'elly.store')
  elytronStorePassword = 'elly.password'
  elytronAlias = 'elly.alias'
  elytronSecret = 'elly.secret'
}

import com.brambolt.gradle.wildfly.security.tasks.CreateCredentialStore
import com.brambolt.gradle.wildfly.security.tasks.AddAlias
import com.brambolt.gradle.wildfly.security.tasks.RetrieveClearPassword

task createCredentialStore(type: CreateCredentialStore) {
  location = elytronStoreLocation
  password = elytronStorePassword
  doFirst { 
    if (location.exists())
      throw new GradleException("File exists: \${location}")
  }
  doLast { 
    if (!location.exists())
      throw new GradleException("Credential store was not created: \${location}")
  }
}

task addAliasWithClearPassword(type: AddAlias, dependsOn: createCredentialStore) {
  location = elytronStoreLocation
  password = elytronStorePassword
  alias = elytronAlias
  secret = elytronSecret
  mask = false
  doFirst {
    logger.info("Adding alias \${alias}: \${location}")
  }
}

task retrieveClearPassword(type: RetrieveClearPassword, dependsOn: addAliasWithClearPassword) {
  location = elytronStoreLocation
  password = elytronStorePassword
  alias = elytronAlias
  doFirst {
    logger.info("Retrieving secret...")
  }
  doLast {
    logger.info("Retrieved not-so-secret cleartext password: \${secret}")
  }
}

task all(dependsOn: retrieveClearPassword) 

""", testProjectDir)
  }
}
