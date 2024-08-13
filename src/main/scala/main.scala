
// akka

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import handler.ProductHandler
// akka http
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*

@main
def main(): Unit = {
  // https://doc.akka.io/docs/akka-http/current/introduction.html
  implicit val actorSystem: ActorSystem[Any] = ActorSystem(Behaviors.empty, "akka-http")
  val productHandler = ProductHandler()

  Http().newServerAt("localhost", 8080).bind(concat(productHandler.getProductRoute, productHandler.getProductsRoute,
    productHandler.putProductRoute, productHandler.postProductRoute))
}

