package sanri.test.mini;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbutils.DbUtils;
import org.junit.Test;

public class TestJdbc {
	
	@Test
	public void testJdbc(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/question25","root","h123");
			PreparedStatement prepareStatement = connection.prepareStatement("select * from question");
			ResultSet executeQuery = prepareStatement.executeQuery();
			System.out.println(executeQuery);
			DbUtils.closeQuietly(connection, prepareStatement, executeQuery);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
		}
	}
}
