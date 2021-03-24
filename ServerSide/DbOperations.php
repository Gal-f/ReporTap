<?php

class DbOperations
{

    private $conn;
    function __construct()
    {
        require_once 'DbConnect.php';
        //require_once dirname(__FILE__).'/DbConnect.php';

        $db = new DbConnect();
        $this->conn = $db->connect();
        $this->conn->query("SET NAMES 'utf8'");
    }

    function signup($password, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID)
    {
        $response = array();
        //checking if a user with this employee number already exists (must be unique)
        $stmt = $this->conn->prepare("SELECT id FROM users WHERE employee_ID = ? OR phone_number = ?");
        $stmt->bind_param("ss", $employeeNumber, $phoneNumber);
        $stmt->execute();
        $stmt->store_result();

        //if the user already exists in the database 
        if ($stmt->num_rows > 0) {
            $response['error'] = true;
            $response['message'] = 'משתמש קיים במערכת, יש לבדוק את מספר העובד ואת מספר הטלפון שהוזנו';
            $stmt->close();
        } else {
            $stmt = $this->conn->prepare("INSERT INTO users (password, employee_ID, full_name, role, phone_number, works_in_dept) VALUES (?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("sssssi", $password, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);

            if ($stmt->execute()) {
                $stmt = $this->conn->prepare("SELECT id, employee_ID, full_name, role, phone_number, works_in_dept  FROM users WHERE employee_ID = ?");
                $stmt->bind_param("s", $employeeNumber);
                $stmt->execute();
                $stmt->bind_result($id, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);
                $stmt->fetch();

                $user = array(
                    'id' => $id,
                    'employee_ID' => $employeeNumber,
                    'full_name' => $fullName,
                    'role' => $jobTitle,
                    'phone_number' => $phoneNumber,
                    'works_in_dept' => $deptID
                );

                $stmt->close();
                //adding the user data in response 
                $response['error'] = false;
                $response['message'] = 'User registered successfully';
                $response['user'] = $user;
            }
        }
        return $response;
    }

    function login($employeeNumber, $password)
    {
        $response = array();
        $stmt = $this->conn->prepare("SELECT id, employee_ID, full_name, role, phone_number, works_in_dept FROM users WHERE employee_ID = ? AND password = ?");
        $stmt->bind_param("ss", $employeeNumber, $password);

        $stmt->execute();

        $stmt->store_result();

        if ($stmt->num_rows > 0) {

            $stmt->bind_result($id, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);
            $stmt->fetch();

            $user = array(
                'id' => $id,
                'employee_ID' => $employeeNumber,
                'full_name' => $fullName,
                'role' => $jobTitle,
                'phone_number' => $phoneNumber,
                'works_in_dept' => $deptID
            );
            $response['error'] = false;
            $response['message'] = 'Login successfull';
            $response['user'] = $user;
        } else {
            $response['error'] = true;
            $response['message'] = 'שגיאה בפרטי ההזדהות';
        }
        return $response;
    }

    function send_message($sender, $department, $patientId, $patientName, $testType, $componentName, $boolValue, $measuredAmount, $isUrgent, $comments)
    {
        $response = array();
        $message = array($department, $patientId, $patientName, $testType, $componentName, $measuredAmount, $isUrgent, $comments);
        $stmt = $this->conn->prepare("INSERT INTO `messages`(`patient_ID`, `test_type`, `component`, `value_boolean`, `value_numeric`, `text`, `is_urgent`, `sender_user`, `recipient_dept`) VALUES (?,?,?,?,?,?,?,?,?);");
        //TODO Change test_type from simple string to relation with test_types table
        $stmt->bind_param("sisidsiii", $patientId, $testType, $componentName, $boolValue, $measuredAmount, $comments, $isUrgent, $sender, $department); //If there's a problem with sqli query, try changing boolean columns to tinyint and use 'i' instead of 's' in the first parameter for bind_param.
        if ($stmt->execute()) {
            $response['error'] = false;
            $response['message'] = 'Message sent successfully';
            $response['sent_message'] = $message;
        } else {
            $response['error'] = true;
            $response['message'] = 'Error while sending the message';
        }
        return $response;
    }
}
