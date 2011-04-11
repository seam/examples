/*
* JBoss, Home of Professional Open Source
* Copyright 2010, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.jboss.seam.wicket.example.publish.scala.test

import java.io.File;

/**
 * Port of MavenArtifactResolver
 * 
 * @author Dan Allen
 * @author oranheim
 */
object MavenArtifactResolver {

    var LOCAL_MAVEN_REPO: String = _

    if (System.getProperty("maven.repo.local") != null) {
        LOCAL_MAVEN_REPO = System.getProperty("maven.repo.local")
    } else {
        LOCAL_MAVEN_REPO = System.getProperty("user.home") + File.separatorChar + ".m2" + File.separatorChar + "repository"
    }

    def resolve(groupId: String, artifactId: String, version: String): File = {
        return new File(LOCAL_MAVEN_REPO + File.separatorChar
            + groupId.replace(".", File.separator) + File.separatorChar + artifactId
            + File.separatorChar + version + File.separatorChar + artifactId + "-" + version
            + ".jar")
    }

    def resolve(qualifiedArtifactId: String): File = {
        var segments = qualifiedArtifactId.split(":")
        return resolve(segments(0), segments(1), segments(2))
    }

}
