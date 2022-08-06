package com.example.http4swebsocket

import cats.effect.Async
import cats.effect.Resource
import cats.syntax.all._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import org.http4s.server.websocket.WebSocketBuilder2
import scala.concurrent.duration._

object WsServer {

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      wsRoutes    <- Stream.eval(WsRoutes[F])
      httpApp      = (ws: WebSocketBuilder2[F]) => wsRoutes.routes(ws).orNotFound
      finalHttpApp = (ws: WebSocketBuilder2[F]) => Logger.httpApp(true, true)(httpApp(ws))
      exitCode    <- Stream.resource(
                       EmberServerBuilder
                         .default[F]
                         .withHost(ipv4"0.0.0.0")
                         .withPort(port"50444")
                         .withHttpWebSocketApp(finalHttpApp)
                         .withShutdownTimeout(1.second)
                         .build >>
                         Resource.eval(Async[F].never)
                     )
    } yield exitCode
  }.drain
}
