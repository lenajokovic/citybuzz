package com.proton.citybuzz

import android.util.Log
import com.proton.citybuzz.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.Properties

class SnowflakeCaller {
    // --- SECURITY WARNING: HARDCODED CREDENTIALS ---
    // These will be visible to anyone who decompiles your app.
    private val snowflakeUrl = "jdbc:snowflake://YQWDLKR-RM50856.snowflakecomputing.com/"
    private val snowflakeUser = "MARKOVKA"
    private val snowflakePassword = "y}#c)K9a2aUxD%q"
    private val snowflakeDb = "CITYBUZZ"
    private val snowflakeSchema = "PUBLIC"
    private val snowflakeWarehouse = "COMPUTE_WH"
    private val snowflakeRole = "ACCOUNTADMIN"
    private var connection : Connection? = null

    companion object {
        @Volatile
        private var instance: SnowflakeCaller? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SnowflakeCaller().also { instance = it }
        }
    }

    suspend fun executeQuery(query: String): ResultSet {
        return withContext(Dispatchers.IO) {
            try {
                if (connection == null || connection!!.isClosed) {
                    createConnection()
                    //Log.e("SnowflakeCaller", "Connection is null or closed. Cannot execute query.")
                }
                val statement = connection!!.createStatement()
                Log.d("SnowflakeCaller", "Executing query: $query")
                val resultSet = statement.executeQuery(query.trimIndent())
                Log.d("SnowflakeCaller", "Query successful.")
                resultSet
            } catch (e: SQLException) {
                Log.e("SnowflakeCaller", "Error executing query", e)
                null // Return null on failure instead of re-throwing
            } as ResultSet
        }
    }

    suspend fun executeUpdate(query: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                if (connection == null || connection!!.isClosed) {
                    Log.e("SnowflakeCaller", "Connection is closed.")
                }
                val statement = connection!!.createStatement()
                Log.d("SnowflakeCaller", "Executing update: $query")
                val result = statement.executeUpdate(query.trimIndent())
                Log.d("SnowflakeCaller", "Update OK.")
                result
            } catch (e: SQLException) {
                Log.e("SnowflakeCaller", "Error executing update", e)
                0
            }
        }
    }

    suspend fun getEvents() : List<Event> {
        val resultSet = executeQuery("""select * from EVENTS""")
        var events = mutableListOf<Event>()
        while (resultSet.next()) {
            //TODO: val dateTime = LocalDateTime.parse(resultSet.getString("DATE"))
            events.add(
                Event(
                    title = resultSet.getString("TITLE"),
                    description = resultSet.getString("DESCRIPTION"),
                    //date = dateTime.toLocalDateTime(),
                    location = resultSet.getString("LOC"),
                )
            )
        }
        return events
    }

    suspend fun createConnection(){
        return withContext(Dispatchers.IO) {
            val properties = Properties().apply {
                put("user", snowflakeUser)
                put("password", snowflakePassword)
                put("db", snowflakeDb)
                put("schema", snowflakeSchema)
                put("warehouse", snowflakeWarehouse)
                put("role", snowflakeRole)
                put("ocspFailOpen", "true")
                put("authenticator", "snowflake")
                put("JDBC_QUERY_RESULT_FORMAT", "JSON")
            }

            try {
                Class.forName("net.snowflake.client.jdbc.SnowflakeDriver")

                Log.d("SnowflakeCaller", "Attempting to connect to Snowflake...")
                connection = DriverManager.getConnection(snowflakeUrl, properties)
                Log.d("SnowflakeCaller", "Connection Successful!")
            } catch (e: Exception) {
                Log.e("SnowflakeCaller", "Error connecting to Snowflake", e)
            }
        }
    }
}