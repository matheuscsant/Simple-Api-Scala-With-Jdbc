package dao

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import scala.collection.immutable.Nil.:::

final case class Product(id: Long, name: String)

final case class ProductsList(productsList: List[Product])

class ProductDao() {

  def getProductById(id: Long): Product = {
    val connection: Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/challenge_scala", "postgres", "123456789")
    var resultSet: ResultSet = null
    var produto: Product = null
    try {
      resultSet = connection.createStatement().executeQuery(s"""SELECT id, name FROM \"Product\" WHERE id = ${id} LIMIT 1""")
      if (resultSet.next()) {
        produto = Product(resultSet.getLong("id"), resultSet.getString("name"))
      }
    } catch {
      case e: Exception => println("Falha ao recuperar dados")
    } finally {
      resultSet.close()
      connection.close()
    }
    produto
  }

  def getProducts: ProductsList = {
    val connection: Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/challenge_scala", "postgres", "123456789")
    var resultSet: ResultSet = null
    var produtos: List[Product] = List()
    try {
      // select "fields" from is better than to select * from
      resultSet = connection.createStatement().executeQuery(s"""SELECT id, name FROM \"Product\"""")
      while (resultSet.next()) {
        produtos = produtos ::: Product(resultSet.getLong("id"), resultSet.getString("name")) :: Nil
      }
    } catch {
      case e: Exception => println("Falha ao recuperar dados")
    } finally {
      resultSet.close()
      connection.close()
    }
    ProductsList(produtos)
  }

  def updateProduct(id: Long, product: Product): Boolean = {
    val connection: Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/challenge_scala", "postgres", "123456789")
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = connection.prepareStatement(s"""UPDATE "Product" SET name = ? WHERE id = ?""")
      preparedStatement.setString(1, product.name)
      preparedStatement.setLong(2, id)
      preparedStatement.executeUpdate()
      true
    } catch {
      case e: Exception =>
        println("Falha ao atualizar dados")
        false
    } finally {
      preparedStatement.close()
      connection.close()
    }

  }

  def insertProduct(product: Product): Boolean = {
    val connection: Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/challenge_scala", "postgres", "123456789")
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = connection.prepareStatement(s"""INSERT INTO "Product" (name) VALUES (?)""")
      preparedStatement.setString(1, product.name)
      preparedStatement.execute()
      true
    } catch {
      case e: Exception =>
        println("Falha ao inserir dados")
        false
    } finally {
      preparedStatement.close()
      connection.close()
    }

  }
}
