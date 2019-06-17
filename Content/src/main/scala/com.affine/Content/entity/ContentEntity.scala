package com.affine.Content.entity

import java.time.LocalDateTime

import com.lightbend.lagom.scaladsl.persistence._
import com.affine.Content.data._
import scala.concurrent.Future

class ContentEntity extends PersistentEntity {

  override type Command = ContentCommand[_]
  override type Event = ContentEvent
  override type State = ContentState

  override def initialState = ContentState(None, LocalDateTime.now().toString)

  override def behavior: (ContentState) => Actions = {
    case ContentState(_, _) => Actions()
      .onCommand[CreateContentCommand, Content] {
      case (CreateContentCommand(x), ctx, _) ⇒
        ctx.thenPersist(ContentCreated(x))(_ ⇒ ctx.reply(x))
    }.onReadOnlyCommand[GetContentCommand, Content] {
      case (GetContentCommand(id), ctx, state) =>
        ctx.reply(state.entity.getOrElse(Content(id, "not found")))
    }
      .onEvent {
        case (ContentCreated(x), _) ⇒
          ContentState(Some(x), LocalDateTime.now().toString)
      }
  }
}

class ContentEntityDatabase(persistentEntityRegistry: PersistentEntityRegistry) extends ContentDatabase{
  def createContent(x: CreateContentCommand): Future[Content] = persistentEntityRegistry.refFor[ContentEntity](x.entity.id.toString).ask(x)
  def getContent(x: GetContentCommand): Future[Content] = persistentEntityRegistry.refFor[ContentEntity](x.id.toString).ask(x)
}
