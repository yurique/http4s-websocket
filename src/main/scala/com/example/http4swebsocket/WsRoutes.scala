package com.example.http4swebsocket

import cats.effect.Async
import cats.implicits._
import fs2.concurrent.Topic
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame
import scala.util.control.NonFatal
import scala.concurrent.duration._

class WsRoutes[F[_]](topic: Topic[F, String])(implicit F: Async[F]) {

  private val connect: fs2.Pipe[F, WebSocketFrame, WebSocketFrame] =
    in =>
      topic
        .subscribe(1).map(WebSocketFrame.Text(_))
        .concurrently(
          fs2.Stream.awakeDelay(1.seconds).evalTap(_ => topic.publish1("Hello!").void) >>
            fs2.Stream.eval(in.compile.drain.recover { case NonFatal(_) => () })
        )

  def routes(ws: WebSocketBuilder2[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root => ws.build(connect) }
  }

}

object WsRoutes {

  def apply[F[_]](implicit F: Async[F]): F[WsRoutes[F]] =
    Topic[F, String].map { topic =>
      new WsRoutes(topic)
    }

}
