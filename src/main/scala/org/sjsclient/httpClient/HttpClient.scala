package org.sjsclient.httpClient

import scala.util.Try

/**
  * Created by prashant on 17/4/17.
  */
trait HttpClient {

  type Json = String

  def postJson(url: String, jsonRequest: Json): Try[String]

  def get(url: String): Try[String]

}
