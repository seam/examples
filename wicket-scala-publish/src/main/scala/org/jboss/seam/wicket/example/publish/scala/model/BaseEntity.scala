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
package org.jboss.seam.wicket.example.publish.scala.model

import javax.persistence._
import java.lang.Long
import java.util.Date
import org.jboss.seam.solder.core.Veto
import scala.reflect._

/**
 * @author oranheim
 */
@MappedSuperclass
@Veto
@serializable
class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @BeanProperty
    var id: Long = _

    @Temporal(value = TemporalType.TIMESTAMP)
    @BeanProperty
    var createdOn: Date = _

    @Temporal(value = TemporalType.TIMESTAMP)
    @BeanProperty
    var modifiedOn: Date = _

    @PrePersist
    def initTimeStamps = {
        if (createdOn == null) 
            createdOn = new Date        
        modifiedOn = createdOn
    }

    @PreUpdate
    def updateTimeStamp = modifiedOn = new Date
    
}
