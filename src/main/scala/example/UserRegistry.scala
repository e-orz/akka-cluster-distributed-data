package example

//#user-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.Cluster

import scala.collection.immutable

object UserRegistry {
  // actor protocol
  sealed trait Command
  final case class GetData(replyTo: ActorRef[DataHolder]) extends Command
  final case class UpdateData(data: String, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class ActionPerformed(description: String)
  final case class DataHolder(data: String)

  def apply(): Behavior[Command] = registry()

  private def registry(): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetData(replyTo) =>
        replyTo ! DataHolder("something")
        Behaviors.same
      case UpdateData(data, replyTo) =>
        replyTo ! ActionPerformed(s"Data updated.")
        Behaviors.same
    }
}
