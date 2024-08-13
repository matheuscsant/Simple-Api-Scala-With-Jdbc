package handler

import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server
import dao.{Product, ProductDao, ProductsList}
// akka http
import akka.http.scaladsl.server.Directives.*
// spray
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import spray.json.DefaultJsonProtocol.*

final case class Response(result: String)

case class ProductHandler() {

  val dao = ProductDao()

  implicit val productMarshaller: spray.json.RootJsonFormat[Product] = jsonFormat2(Product.apply)
  implicit val productsMarshaller: spray.json.RootJsonFormat[ProductsList] = jsonFormat1(ProductsList.apply)
  implicit val resultMarshaller: spray.json.RootJsonFormat[Response] = jsonFormat1(Response.apply)

  // https://doc.akka.io/docs/akka-http/current/introduction.html
  def putProductRoute: server.Route =
    path("product" / LongNumber) { id =>
      put {
        entity(as[Product]) {
          product =>
            complete {
              val res: Boolean = dao.updateProduct(id, product)
              Response(if (res) "Product has updated" else "Fail")
            }
        }
      }
    }

  // https://doc.akka.io/docs/akka-http/current/introduction.html
  def getProductsRoute: server.Route =
    path("product") {
      get {
        complete {
          dao.getProducts
        }
      }
    }

  def getProductRoute: server.Route =
    path("product" / LongNumber) { id =>
      get {
        val result: Product = dao.getProductById(id)
        // Pattern Matching
        result match {
          case Product(l, n) => complete(result)
          case null => complete(StatusCodes.NotFound -> Response("Not found"))
        }
      }
    }

  def postProductRoute: server.Route =
    path("product") {
      post {
        entity(as[Product]) {
          product =>
            val res: Boolean = dao.insertProduct(product)
            // https://doc.akka.io/docs/akka-http/current/common/http-model.html
            val headers = Location("http://localhost:8080/product/teste")
            if (res) complete(HttpResponse(StatusCodes.Created, headers = List(headers))) else complete(StatusCodes.BadRequest)
        }
      }
    }
}
