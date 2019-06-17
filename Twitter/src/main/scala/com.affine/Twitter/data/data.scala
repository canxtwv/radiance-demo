package com.affine.Twitter

import java.util.UUID

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}

import io.surfkit.typebus._
import scala.concurrent.Future

package object data {

  trait TwitteruserCommand[R] extends ReplyType[R]

  case class CreateTwitteruserCommand(entity: Twitteruser) extends TwitteruserCommand[Twitteruser]
  case class GetTwitteruserCommand(id: UUID) extends TwitteruserCommand[Twitteruser]

  sealed trait TwitteruserEvent

  case class TwitteruserCreated(entity: Twitteruser) extends TwitteruserEvent
  case class TwitteruserState(entity: Option[Twitteruser], timeStamp: String)
  case class Twitteruser(id: UUID, data: String)

  object Implicits extends AvroByteStreams{
    implicit val createTwitteruserRW = Typebus.declareType[CreateTwitteruserCommand, AvroByteStreamReader[CreateTwitteruserCommand], AvroByteStreamWriter[CreateTwitteruserCommand]]
    implicit val TwitteruserRW = Typebus.declareType[Twitteruser, AvroByteStreamReader[Twitteruser], AvroByteStreamWriter[Twitteruser]]
    implicit val getTwitteruserRW = Typebus.declareType[GetTwitteruserCommand, AvroByteStreamReader[GetTwitteruserCommand], AvroByteStreamWriter[GetTwitteruserCommand]]
  }

  trait TwitteruserDatabase{
    def createTwitteruser(x: CreateTwitteruserCommand): Future[Twitteruser]
    def getTwitteruser(x: GetTwitteruserCommand): Future[Twitteruser]
  }
}



