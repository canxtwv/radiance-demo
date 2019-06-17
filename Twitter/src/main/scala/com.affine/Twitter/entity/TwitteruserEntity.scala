package com.affine.Twitter.entity

import java.time.LocalDateTime

import com.lightbend.lagom.scaladsl.persistence._
import com.affine.Twitter.data._
import scala.concurrent.Future

class TwitteruserEntity extends PersistentEntity {

  override type Command = TwitteruserCommand[_]
  override type Event = TwitteruserEvent
  override type State = TwitteruserState

  override def initialState = TwitteruserState(None, LocalDateTime.now().toString)

  override def behavior: (TwitteruserState) => Actions = {
    case TwitteruserState(_, _) => Actions()
      .onCommand[CreateTwitteruserCommand, Twitteruser] {
      case (CreateTwitteruserCommand(x), ctx, _) ⇒
        ctx.thenPersist(TwitteruserCreated(x))(_ ⇒ ctx.reply(x))
    }.onReadOnlyCommand[GetTwitteruserCommand, Twitteruser] {
      case (GetTwitteruserCommand(id), ctx, state) =>
        ctx.reply(state.entity.getOrElse(Twitteruser(id, "not found")))
    }
      .onEvent {
        case (TwitteruserCreated(x), _) ⇒
          TwitteruserState(Some(x), LocalDateTime.now().toString)
      }
  }
}

class TwitteruserEntityDatabase(persistentEntityRegistry: PersistentEntityRegistry) extends TwitteruserDatabase{
  def createTwitteruser(x: CreateTwitteruserCommand): Future[Twitteruser] = persistentEntityRegistry.refFor[TwitteruserEntity](x.entity.id.toString).ask(x)
  def getTwitteruser(x: GetTwitteruserCommand): Future[Twitteruser] = persistentEntityRegistry.refFor[TwitteruserEntity](x.id.toString).ask(x)
}
