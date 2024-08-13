
// akka

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import dao.ProductDao
import handler.ProductHandler

import java.sql.DriverManager
// akka http
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*

@main
def main(): Unit = {
  implicit val actorSystem: ActorSystem[Any] = ActorSystem(Behaviors.empty, "akka-http")
  val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/challenge_scala", "postgres", "123456789")
  val dao = ProductDao(connection)
  val handler = ProductHandler(dao)

  Http().newServerAt("localhost", 8080).bind(concat(handler.getProductRoute, handler.getProductsRoute))
}

