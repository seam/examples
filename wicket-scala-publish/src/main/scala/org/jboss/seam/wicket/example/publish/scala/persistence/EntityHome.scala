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
package org.jboss.seam.wicket.example.publish.scala.persistence

import javax.enterprise.context.Conversation
import javax.inject.Inject
import java.lang.Long
import org.jboss.seam.wicket.example.publish.persistence.{ CustomEntityHome, EntityDao }
import org.jboss.seam.wicket.example.publish.scala.model.BaseEntity
import scala.reflect.BeanProperty

/**
 * @author oranheim
 */
class EntityHome[T <: BaseEntity] extends CustomEntityHome[T] {

    @Inject
    var dao: EntityDao = _

    @Inject
    var conversation: Conversation = _

    @BeanProperty
    var id: Long = _
    
    var bypassConversation = true

    var instance: T = _

    override def getInstance: T = {
        if (instance == null) {
            if (id == null) {
                instance = createInstance
            } else {
                instance = loadInstance
            }
        }
        return instance
    }

    override def setInstance(instance: T) = this.instance = instance

    override def createInstance: T = getClassType.newInstance

    override def loadInstance: T = dao.find(getClassType, id)

    override def isManaged: Boolean = getInstance.getId != null
    
    override def initConversation = {
        bypassConversation = false
        if (conversation.isTransient) {
            conversation.begin
        }
    }

    override def completeConversation = {
        if (!bypassConversation && !conversation.isTransient()) {
            conversation.end
        }
    }

    override def save: String = {
        if (isManaged) {
            dao.update(getInstance)
        } else {
            dao.create(getInstance)
        }
        completeConversation
        return "saved"

    }

    override def cancel: String = {
        completeConversation
        return "canceled"
    }

    override def delete: String = {
        dao.delete(getInstance)
        completeConversation
        return "deleted"
    }

}
