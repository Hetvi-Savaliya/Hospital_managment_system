package HospitalManagementSystem;

import java.sql.*;
import java.util.Collection;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3307/hospital";
    private static final String username = "root";
    private static final String password = "hetu@11";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection,scanner);
            Doctors doctor = new Doctors(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. view Patient");
                System.out.println("3. view Doctor");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your Choice : ");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        // add patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // view patient
                        patient.ViewPatient();
                        System.out.println();
                        break;
                    case 3:
                        // view Doctor
                        doctor.ViewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        // Book Appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        // exit
                        return;
                    default:
                        System.out.println("Enter valid choice!!1");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctors doctors, Connection connection, Scanner scanner) {
        System.out.print("Enter patient Id : ");
        int patient_id = scanner.nextInt();
        System.out.print("Enter Doctor Id : ");
        int doctor_id = scanner.nextInt();
        System.out.print("Enter Appointment Date (yyyy-mm-dd) : ");
        String appointment_date = scanner.next();
        if(patient.getPatientById(patient_id) && doctors.getDoctorsById(doctor_id)){
            if(checkDoctorAvalilability(doctor_id, appointment_date, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appoinment_date) VALUES(?, ?, ?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patient_id);
                    preparedStatement.setInt(2, doctor_id);
                    preparedStatement.setString(3, appointment_date);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected > 0){
                        System.out.println("Appointment booked!!");
                    }else {
                        System.out.println("Failed to book appointment!!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor is not available on this date!!");
            }
        }else {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }

    public static boolean checkDoctorAvalilability(int doctor_id, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appoinment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctor_id);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else {
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
