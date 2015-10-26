package org.shkr.actors.basic.cluster.transformation

/**
 * Message
 * The types of messages used by the cluster module
 */
object Message {

  final case class TransformationJob(text: String)
  final case class TransformationResult(client: String, text: String, worker: String)
  final case class JobFailed(reason: String, job: TransformationJob)
  case object BackendRegistration
}
