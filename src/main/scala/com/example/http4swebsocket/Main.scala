package com.example.http4swebsocket

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    WsServer.stream[IO].compile.drain.as(ExitCode.Success)
}
