package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao implements Repository<Product, Integer> {
    private final String serverURL;
    private final String user;
    private final String password;

    public ProductDao(String serverURL, String user, String password) {
        this.serverURL = serverURL;
        this.user = user;
        this.password = password;
        init();
    }

    private void init() {
        try {
            Connection conn = DriverManager.getConnection(serverURL, user, password);
            createDatabaseIfNotExists(conn);
            createProductTableIfNotExists(conn);
            conn.close();
        } catch (SQLException SQLe) {
            System.out.println(SQLe.getMessage());
        }
    }

    private void createDatabaseIfNotExists(Connection conn) {
        try {
            String sql1 = "CREATE DATABASE IF NOT EXISTS ProductManagement";
            PreparedStatement ptm1 = conn.prepareStatement(sql1);
            String sql2 = "use ProductManagement";
            PreparedStatement ptm2 = conn.prepareStatement(sql2);

            ptm1.executeUpdate();
            ptm2.execute();

            ptm1.close();
            ptm2.close();
        } catch (SQLException SQLe) {
            System.out.println(SQLe.getMessage());
        }
    }

    private void createProductTableIfNotExists(Connection conn) {
        try {
            String sql1 = "DROP TABLE IF EXISTS Product";
            PreparedStatement ptm1 = conn.prepareStatement(sql1);
            String sql2 = "CREATE TABLE IF NOT EXISTS Product(id int NOT NULL AUTO_INCREMENT, name VARCHAR(200), price int, PRIMARY KEY (id))";
            PreparedStatement ptm2 = conn.prepareStatement(sql2);

            ptm1.executeUpdate();
            ptm2.executeUpdate();

            ptm1.close();
            ptm2.close();
        } catch (SQLException SQLe) {
            System.out.println(SQLe.getMessage());
        }
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(serverURL + "/ProductManagement", user, password);
            return conn;
        } catch (SQLException SQLe) {
            return null;
        }
    }

    @Override
    public Integer add(Product item) {
        Integer id = null;
        String sql = "INSERT INTO Product (name, price) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            ptm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ptm.setNString(1, (String) item.getName());
            ptm.setInt(2, (Integer) item.getPrice());
            ptm.executeUpdate();
            rs = ptm.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
            conn.commit();

            return id;
        } catch (SQLException SQLe) {
            System.out.println(SQLe.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ptm != null) {
                    ptm.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException SQLe) {
                System.out.println(SQLe.getMessage());
            }
        }
        return null;
    }

    @Override
    public List<Product> readAll() {
        List<Product> list = new ArrayList<Product>();
        String sql = "SELECT * FROM Product";
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            ptm = conn.prepareStatement(sql);
            rs = ptm.executeQuery();

            while (rs.next()) {
                Product product = new Product();

                product.setId(rs.getInt("id"));
                product.setName(rs.getNString("name"));
                product.setPrice(rs.getInt("price"));
                list.add(product);
            }
        } catch (SQLException SQLe) {
            System.out.println(SQLe.getMessage());
        } finally {
            try{
                if (conn != null) {
                    conn.close();
                }
                if (ptm != null) {
                    ptm.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException SQLe) {
                System.out.println(SQLe.getMessage());
            }
        }
        return list;
    }

    @Override
    public Product read(Integer id) {
        Product product = new Product();
        String queryById = "SELECT * FROM Product WHERE id = ?";


        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(queryById);) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getInt("price"));
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return product;
    }

    @Override
    public boolean update(Integer id, Product item) {
        String queryForUpdate = "UPDATE Product SET name = ?, price = ? WHERE id = ?";
        boolean updated = false;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(queryForUpdate)) {
            ps.setNString(1, (String) item.getName());
            ps.setInt(2, (Integer) item.getPrice());
            ps.setInt(3, id);

            updated = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }

    @Override
    public boolean delete(Integer id) {
        String queryForDeleteById = "DELETE FROM Product WHERE id = ?";
        boolean deleted = false;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(queryForDeleteById)) {
            ps.setInt(1, id);
            deleted = ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }
}
