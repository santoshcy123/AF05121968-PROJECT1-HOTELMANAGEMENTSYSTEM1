package hotel;

import java.sql.*;
import java.util.Scanner;

public class HotelManagement {

    static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    static final String user = "root";
    static final String password = "root123";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con =
                    DriverManager.getConnection(url, user, password);

            System.out.println("Database Connected");

            // ADMIN LOGIN

            System.out.print("Enter Username: ");
            String uname = sc.next();

            System.out.print("Enter Password: ");
            String pass = sc.next();

            String loginQuery =
                    "SELECT * FROM admin WHERE username=? AND password=?";

            PreparedStatement loginStmt =
                    con.prepareStatement(loginQuery);

            loginStmt.setString(1, uname);
            loginStmt.setString(2, pass);

            ResultSet loginRs = loginStmt.executeQuery();

            if (!loginRs.next()) {
                System.out.println("Invalid Login");
                return;
            }

            System.out.println("Login Successful");

            while (true) {

                System.out.println("\n===== HOTEL MANAGEMENT =====");
                System.out.println("1. View Rooms");
                System.out.println("2. Add Room");
                System.out.println("3. Book Room");
                System.out.println("4. View Bookings");
                System.out.println("5. Cancel Booking");
                System.out.println("6. Exit");

                System.out.print("Enter Choice: ");

                int choice = sc.nextInt();

                switch (choice) {

                    // VIEW ROOMS
                    case 1:

                        String roomQuery = "SELECT * FROM rooms";

                        Statement stmt = con.createStatement();

                        ResultSet rs = stmt.executeQuery(roomQuery);

                        System.out.println("\nROOM DETAILS");

                        while (rs.next()) {

                            System.out.println(
                                    rs.getInt("room_id") + " | " +
                                    rs.getString("room_type") + " | " +
                                    rs.getDouble("price") + " | " +
                                    rs.getString("status")
                            );
                        }

                        break;

                    // ADD ROOM
                    case 2:

                        System.out.print("Enter Room ID: ");
                        int roomId = sc.nextInt();

                        System.out.print("Enter Room Type: ");
                        String type = sc.next();

                        System.out.print("Enter Price: ");
                        double price = sc.nextDouble();

                        String addRoom =
                                "INSERT INTO rooms VALUES(?,?,?,?)";

                        PreparedStatement pst =
                                con.prepareStatement(addRoom);

                        pst.setInt(1, roomId);
                        pst.setString(2, type);
                        pst.setDouble(3, price);
                        pst.setString(4, "Available");

                        pst.executeUpdate();

                        System.out.println("Room Added");

                        break;

                    // BOOK ROOM
                    case 3:

                        System.out.print("Enter Booking ID: ");
                        int bookingId = sc.nextInt();

                        sc.nextLine();

                        System.out.print("Enter Customer Name: ");
                        String cname = sc.nextLine();

                        System.out.print("Enter Room ID: ");
                        int rId = sc.nextInt();

                        System.out.print("Enter Days: ");
                        int days = sc.nextInt();

                        // GET ROOM PRICE

                        String priceQuery =
                                "SELECT price,status FROM rooms WHERE room_id=?";

                        PreparedStatement priceStmt =
                                con.prepareStatement(priceQuery);

                        priceStmt.setInt(1, rId);

                        ResultSet prs = priceStmt.executeQuery();

                        if (prs.next()) {

                            String status = prs.getString("status");

                            if (status.equals("Booked")) {
                                System.out.println("Room Already Booked");
                                break;
                            }

                            double roomPrice =
                                    prs.getDouble("price");

                            double total =
                                    roomPrice * days;

                            String bookingQuery =
                                    "INSERT INTO bookings VALUES(?,?,?,?,?)";

                            PreparedStatement bookingStmt =
                                    con.prepareStatement(bookingQuery);

                            bookingStmt.setInt(1, bookingId);
                            bookingStmt.setString(2, cname);
                            bookingStmt.setInt(3, rId);
                            bookingStmt.setInt(4, days);
                            bookingStmt.setDouble(5, total);

                            bookingStmt.executeUpdate();

                            // UPDATE ROOM STATUS

                            String updateRoom =
                                    "UPDATE rooms SET status='Booked' WHERE room_id=?";

                            PreparedStatement updateStmt =
                                    con.prepareStatement(updateRoom);

                            updateStmt.setInt(1, rId);

                            updateStmt.executeUpdate();

                            System.out.println("Room Booked");
                            System.out.println("Total Amount: " + total);

                        } else {
                            System.out.println("Room Not Found");
                        }

                        break;

                    // VIEW BOOKINGS
                    case 4:

                        String bookingView =
                                "SELECT * FROM bookings";

                        Statement bookingStmt2 =
                                con.createStatement();

                        ResultSet brs =
                                bookingStmt2.executeQuery(bookingView);

                        System.out.println("\nBOOKING DETAILS");

                        while (brs.next()) {

                            System.out.println(
                                    brs.getInt("booking_id") + " | " +
                                    brs.getString("customer_name") + " | " +
                                    brs.getInt("room_id") + " | " +
                                    brs.getInt("days") + " | " +
                                    brs.getDouble("total_amount")
                            );
                        }

                        break;

                    // CANCEL BOOKING
                    case 5:

                        System.out.print("Enter Booking ID: ");
                        int cancelId = sc.nextInt();

                        // GET ROOM ID

                        String getRoom =
                                "SELECT room_id FROM bookings WHERE booking_id=?";

                        PreparedStatement getStmt =
                                con.prepareStatement(getRoom);

                        getStmt.setInt(1, cancelId);

                        ResultSet grs =
                                getStmt.executeQuery();

                        if (grs.next()) {

                            int roomNo =
                                    grs.getInt("room_id");

                            // DELETE BOOKING

                            String deleteBooking =
                                    "DELETE FROM bookings WHERE booking_id=?";

                            PreparedStatement delStmt =
                                    con.prepareStatement(deleteBooking);

                            delStmt.setInt(1, cancelId);

                            delStmt.executeUpdate();

                            // MAKE ROOM AVAILABLE AGAIN

                            String roomUpdate =
                                    "UPDATE rooms SET status='Available' WHERE room_id=?";

                            PreparedStatement roomStmt =
                                    con.prepareStatement(roomUpdate);

                            roomStmt.setInt(1, roomNo);

                            roomStmt.executeUpdate();

                            System.out.println("Booking Cancelled");

                        } else {
                            System.out.println("Booking Not Found");
                        }

                        break;

                    case 6:

                        System.out.println("Thank You");
                        con.close();
                        System.exit(0);

                    default:
                        System.out.println("Invalid Choice");
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}