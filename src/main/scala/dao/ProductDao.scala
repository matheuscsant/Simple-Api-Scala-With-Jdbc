package dao

import resource.exception.ResourceNotFound

import java.sql.*
import scala.collection.immutable.Nil.:::

final case class Product(id: Long, name: String)

final case class ProductsList(productsList: List[Product])

object ProductDao {

  private val url: String = "jdbc:postgresql://localhost:5432/challenge_scala"
  private val user: String = "postgres"
  private val password: String = "123456789"

  // https://www.oreilly.com/library/view/scala-cookbook/9781449340292/ch16s02.html
  def getProductById(id: Long): Product = {
    // https://docs.oracle.com/javase/tutorial/jdbc/basics/connecting.html
    // Class.forName("org.postgresql.Driver") - Driver JDBC >= JDBC 4.0, don't need
    val connection: Connection = DriverManager.getConnection(url, user, password)
    var resultSet: ResultSet = null
    var produto: Product = null
    try {
      resultSet = connection.createStatement().executeQuery(s"""SELECT id, name FROM \"Product\" WHERE id = ${id} LIMIT 1""")
      if (resultSet.next()) {
        produto = Product(resultSet.getLong("id"), resultSet.getString("name"))
      } else {
        throw ResourceNotFound("Produto não encontrado")
      }
    } catch {
      case e: Exception => throw e
    } finally {
      resultSet.close()
      connection.close()
    }
    produto
  }

  def getProducts: ProductsList = {
    val connection: Connection = DriverManager.getConnection(url, user, password)
    var resultSet: ResultSet = null
    var produtos: List[Product] = List()
    try {
      // select "fields" from is better than to select * from
      resultSet = connection.createStatement().executeQuery(s"""SELECT id, name FROM \"Product\"""")
      while (resultSet.next()) {
        produtos = produtos ::: Product(resultSet.getLong("id"), resultSet.getString("name")) :: Nil
      }
      if produtos.isEmpty then
        throw ResourceNotFound("Nenhum produto encontrado")
    } catch {
      case e: Exception => throw e
    } finally {
      resultSet.close()
      connection.close()
    }
    ProductsList(produtos)
  }

  def updateProduct(id: Long, product: Product): Boolean = {
    val connection: Connection = DriverManager.getConnection(url, user, password)
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = connection.prepareStatement(s"""UPDATE "Product" SET name = ? WHERE id = ?""")
      preparedStatement.setString(1, product.name)
      preparedStatement.setLong(2, id)
      val rows: Integer = preparedStatement.executeUpdate()

      if rows == 0 then
        throw new SQLException("Nenhum registro afetado")
      true
    } catch {
      case e: Exception => throw e
    } finally {
      preparedStatement.close()
      connection.close()
    }
  }

  def deleteProduct(id: Long): Boolean = {
    val connection: Connection = DriverManager.getConnection(url, user, password)
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = connection.prepareStatement(s"""DELETE FROM "Product" WHERE id = ?""")
      preparedStatement.setLong(1, id)
      val rows: Integer = preparedStatement.executeUpdate()

      if rows == 0 then
        throw new SQLException("Nenhum registro afetado")
      true
    } catch {
      case e: Exception => throw e
    } finally {
      preparedStatement.close()
      connection.close()
    }
  }

  def insertProduct(product: Product): Boolean = {
    val connection: Connection = DriverManager.getConnection(url, user, password)
    var preparedStatement: PreparedStatement = null
    try {
      preparedStatement = connection.prepareStatement(s"""INSERT INTO "Product" (name) VALUES (?)""")
      preparedStatement.setString(1, product.name)
      val rows: Integer = preparedStatement.executeUpdate()
      if rows == 0 then
        throw new SQLException("Nenhum registro afetado")
      true
    } catch {
      case e: Exception => throw e
    } finally {
      preparedStatement.close()
      connection.close()
    }

  }
}
