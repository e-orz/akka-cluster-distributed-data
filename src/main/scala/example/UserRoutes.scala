package example

import akka.http.scaladsl.server.Directives._
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, concat, delete, entity, get, onSuccess, path, pathEnd, pathPrefix, post, rejectEmptyResponse}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import example.UserRegistry.{ActionPerformed, GetData, UpdateData}

import scala.concurrent.Future

class UserRoutes(userRegistry: ActorRef[UserRegistry.Command])(implicit val system: ActorSystem[_]) {

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getData(): Future[String] =
    userRegistry.ask(GetData)
  def updateData(data: String): Future[ActionPerformed] =
    userRegistry.ask(UpdateData(data, _))

  val userRoutes: Route =
  path("api" / "resource") {
    concat(
      get {
        onSuccess(getData()) { data =>
          complete(HttpEntity(ContentTypes.`application/json`, data))
        }
      },
      post {
        entity(as[String]) { data =>
          onSuccess(updateData(data)) { performed: ActionPerformed =>
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, performed.description))
          }
        }
      }
    )
  }
}
