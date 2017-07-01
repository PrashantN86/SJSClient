package org.sjsclient

import scala.concurrent.duration.Duration

/**
  * Created by prashant on 24/4/17.
  */
package object sjs {

  sealed trait JobType
  final case class Sync(val timeout : Duration) extends JobType
  final case object Async extends JobType

  type JobId = String
  type JobResponse = String

  object JobStatus extends Enumeration {
    type JobStatus = Value
    val FINISHED, RUNNING, ERROR = Value
  }

}
