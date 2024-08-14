package resource

import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Route
import dao.{Product, ProductDao, ProductsList}

// akka http
import akka.http.scaladsl.server.Directives.*
// spray
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import spray.json.DefaultJsonProtocol.*

object ProductResource {

  val dao: ProductDao.type = ProductDao

  implicit val productMarshaller: spray.json.RootJsonFormat[Product] = jsonFormat2(Product.apply)
  implicit val productsMarshaller: spray.json.RootJsonFormat[ProductsList] = jsonFormat1(ProductsList.apply)

  // https://doc.akka.io/docs/akka-http/current/introduction.html
  val allRoutesProduct: Route = {
    path("product" / LongNumber) { id =>
      put {
        entity(as[Product]) {
          product =>
            complete {
              val res: Boolean = dao.updateProduct(id, product)
              HttpResponse(StatusCodes.Accepted)
            }
        }
      }
    } ~
      path("product" / LongNumber) { id =>
        get {
          val result: Product = dao.getProductById(id)
          complete(result)
        }
      }
      ~
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
      } ~
      path("product") {
        get {
          complete {
            dao.getProducts
          }
        }
      }
  }

}
