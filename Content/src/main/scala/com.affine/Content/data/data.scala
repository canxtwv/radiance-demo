package com.affine.Content

import java.util.UUID

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}

import io.surfkit.typebus._
import scala.concurrent.Future

package object data {

  trait ContentCommand[R] extends ReplyType[R]

  case class CreateContentCommand(entity: Content) extends ContentCommand[Content]
  case class GetContentCommand(id: UUID) extends ContentCommand[Content]

  sealed trait ContentEvent

  case class ContentCreated(entity: Content) extends ContentEvent
  case class ContentState(entity: Option[Content], timeStamp: String)
  case class Content(id: UUID, data: String)

  object Implicits extends AvroByteStreams{
    implicit val createContentRW = Typebus.declareType[CreateContentCommand, AvroByteStreamReader[CreateContentCommand], AvroByteStreamWriter[CreateContentCommand]]
    implicit val ContentRW = Typebus.declareType[Content, AvroByteStreamReader[Content], AvroByteStreamWriter[Content]]
    implicit val getContentRW = Typebus.declareType[GetContentCommand, AvroByteStreamReader[GetContentCommand], AvroByteStreamWriter[GetContentCommand]]
  }

  trait ContentDatabase{
    def createContent(x: CreateContentCommand): Future[Content]
    def getContent(x: GetContentCommand): Future[Content]
  }
}



