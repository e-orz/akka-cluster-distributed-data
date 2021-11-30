package example

//#user-registry-actor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ddata.{Key, LWWRegister, LWWRegisterKey, SelfUniqueAddress}
import akka.cluster.ddata.typed.scaladsl.{DistributedData, Replicator}
import akka.cluster.typed.Cluster

import scala.collection.immutable
import scala.concurrent.duration.DurationInt

object UserRegistry {
  // actor protocol
  sealed trait Command
  final case class GetData(replyTo: ActorRef[String]) extends Command
  final case class UpdateData(data: String, replyTo: ActorRef[ActionPerformed]) extends Command
//  case object Unsubscribe extends Command
  private sealed trait InternalCommand extends Command
  private case class InternalUpdateResponse(rsp: Replicator.UpdateResponse[LWWRegister[String]], replyTo: ActorRef[ActionPerformed]) extends InternalCommand
  private case class InternalGetResponse(rsp: Replicator.GetResponse[LWWRegister[String]], replyTo: ActorRef[String]) extends InternalCommand
  private case class InternalSubscribeResponse(chg: Replicator.SubscribeResponse[LWWRegister[String]]) extends InternalCommand

  final case class ActionPerformed(description: String)
//  final case class DataHolder(data: String)

  def apply(key: String): Behavior[Command] = registry(LWWRegisterKey.create(key))

  private def registry(key: Key[LWWRegister[String]]): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      DistributedData.withReplicatorMessageAdapter[Command, LWWRegister[String]] { replicatorAdapter =>
        implicit val node: SelfUniqueAddress = DistributedData(context.system).selfUniqueAddress
        Behaviors.receiveMessage {
          case GetData(replyTo) =>
            replicatorAdapter.askGet(
              askReplyTo => Replicator.Get(key, Replicator.ReadLocal, askReplyTo),
              value => InternalGetResponse(value, replyTo))
            Behaviors.same
          case UpdateData(data, replyTo) =>
            replicatorAdapter.askUpdate(
              askReplyTo => Replicator.Update(key, LWWRegister.create(""), Replicator.WriteAll(10.seconds), askReplyTo)(_ => LWWRegister.create(data)),
              value => InternalUpdateResponse(value, replyTo))
            Behaviors.same

          case internal: InternalCommand =>
            internal match {
              case InternalUpdateResponse(_, replyTo) =>
                replyTo ! ActionPerformed(s"Data updated.")
                Behaviors.same
              case InternalGetResponse(rsp@Replicator.GetSuccess(`key`), replyTo) =>
                val value = rsp.get(key).value
                replyTo ! value
                Behaviors.same
              case InternalGetResponse(rsp@Replicator.NotFound(`key`), replyTo) =>
                replyTo ! "Not found! (Did you set it in the first place?)"
                Behaviors.same
              case InternalGetResponse(rsp, replyTo) =>
                replyTo ! s"An error has occurred. Internal error is: ${rsp.toString}"
                context.log.error(rsp.toString)
                Behaviors.unhandled // not dealing with failures
              case InternalSubscribeResponse(chg@Replicator.Changed(`key`)) =>
                Behaviors.same
              case InternalSubscribeResponse(Replicator.Deleted(_)) =>
                Behaviors.unhandled // no deletes
              case InternalSubscribeResponse(_) => // changed but wrong key
                Behaviors.unhandled
            }
        }
      }
    }
}
