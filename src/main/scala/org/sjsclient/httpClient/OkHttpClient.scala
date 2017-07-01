package org.sjsclient.httpClient

import java.util.concurrent.TimeUnit

import com.squareup.okhttp.{MediaType, OkHttpClient, Request, RequestBody}
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success, Try}

/**
  * Created by prashant on 17/4/17.
  */

final object OkHttpClient extends HttpClient with LazyLogging {

  val client = new OkHttpClient()
  val MediaType_JSON = MediaType.parse("application/json; charset=utf-8")

  override def postJson(url: String, jsonRequest: String): Try[String] = {

    logger.info(s"url : $url")
    logger.info(s"jsonRequest : $jsonRequest")
    val body = RequestBody.create(MediaType_JSON, jsonRequest)
    val request = new Request.Builder().url(url).post(body).build();
    client.setConnectTimeout(30, TimeUnit.MINUTES)
    val response = client.newCall(request).execute()
    val responseBody = response.body.string
    logger.info(s"response : $responseBody")
    response.code match {
      case 200 => Success(responseBody)
      case 202 => Success(responseBody)
      case _ => Failure(new Exception(responseBody))
    }

  }

  override def get(url: String): Try[String] = {

    val request = new Request.Builder().url(url).get().build();
    client.setConnectTimeout(30, TimeUnit.MINUTES)
    val response = client.newCall(request).execute()
    val responseBody = response.body.string
    logger.info(s"response : $responseBody")
    response.code match {
      case 200 => Success(responseBody)
      case _ => Failure(new Exception(responseBody))

    }
  }
}
