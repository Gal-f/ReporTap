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
    function inboxdr($department)
    {
        $response = array();
        $query="SELECT M.ID, M.sent_time, M.patient_ID, T.name, M.is_urgent FROM messages as M JOIN test_types as T ON M.test_type=T.ID WHERE M.recipient_dept = ? AND M.confirm_time IS NULL";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $department);

        $stmt->execute();

        $stmt->store_result();
        $rows=$stmt->num_rows;

        if ($stmt->num_rows > 0) {

            while ($rows>0){
                $stmt->bind_result($id, $sentTime, $patientId, $testName, $isUrgent);
                $stmt->fetch();
            
                $report[$stmt->num_rows-$rows] = array('id' =>$id,
                    'sent_time' => $sentTime,
                    'patient_id' => $patientId,
                    'name' => $testName,
                    'is_urgent' => $isUrgent,
                );
                $rows--;
            }
            $response['error'] = false;
            $response['message'] = 'new report for you';
            $response['report'] = $report;
        } else {
            $response['error'] = true;
            $response['message'] = 'שגיאה בהצגת הדיווח';
        }
        return $response;
        
    }

    function getDeptsAndTests()
    {
        $response = array();
        $response['message'] = '';
        //1st part - Get Departments
        $query = "SELECT ID, departments.name FROM departments";
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        $stmt->store_result();

        $rows = $stmt->num_rows;
        if ($rows == 0){
            $response['error'] = true;
            $response['message'] = 'Unable to retrieve departments from the server';
        } else {
            while ($rows > 0){
                $stmt->bind_result($deptID, $deptName);
                $stmt->fetch();

                $depts[$stmt->num_rows-$rows] = array('deptID' => $deptID, 'deptName' => $deptName);
                $rows--;
            }
            $response['error'] = false;
            $response['message'] = 'Departments pulled successfully';
            $response['departments'] = $depts;
            //2nd part - Get test types
            $query = "SELECT ID, name, result_type FROM test_types";
            $stmt2 = $this->conn->prepare($query);
            $stmt2->execute();
            $stmt2->store_result();

            $rows = $stmt2->num_rows;
            if ($rows == 0){
                $response['error'] = true;
                $response['message'] .= ', Unable to retrieve test types from the server';
            } else {
                while ($rows > 0){
                    $stmt2->bind_result($testID, $testName, $resultType);
                    $stmt2->fetch();

                    $testsTypes[$stmt2->num_rows()-$rows] = array('testID' => $testID, 'testName' => $testName, 'resultType' => $resultType);
                    $rows--;
                }
                $response['error'] = false;
                $response['message'] .= ', Test types pulled successfully';
                $response['testTypes'] = $testsTypes;
            }
        }
        return $response;
    }
}
