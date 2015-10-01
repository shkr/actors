package org.shkr.actors.cluster.transformation

/**
 * Messages
 * The types of messages used by the cluster module
 */
object Messages {

  final case class TransformationJob(text: String)
  final case class TransformationResult(text: String)
  final case class JobFailed(reason: String, job: TransformationJob)
  case object BackendRegistration
}
