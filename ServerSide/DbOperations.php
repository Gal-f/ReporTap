<?php

class DbOperations
{

    private $conn;
    function __construct()
    {
        require_once 'DbConnect.php';

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

    function send_message($sender, $department, $patientId, $patientName, $testType, $componentName, $isValueBool, $testResultValue, $isUrgent, $comments)
    {
        $response = array();
        $message = array($department, $patientId, $patientName, $testType, $componentName, $isValueBool, $testResultValue, $isUrgent, $comments);
        $stmt = $this->conn->prepare("INSERT INTO `messages`(`patient_ID`, `test_type`, `component`, `is_value_boolean`, `test_result_value`, `text`, `is_urgent`, `sender_user`, `recipient_dept`) VALUES (?,?,?,?,?,?,?,?,?);");
        //TODO Change test_type from simple string to relation with test_types table
        $stmt->bind_param("sisidsiii", $patientId, $testType, $componentName, $isValueBool, $testResultValue, $comments, $isUrgent, $sender, $department); //If there's a problem with sqli query, try changing boolean columns to tinyint and use 'i' instead of 's' in the first parameter for bind_param.
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
        $query="SELECT M.ID, M.sent_time, M.patient_ID, T.name, M.is_urgent FROM messages as M JOIN test_types as T ON M.test_type=T.ID WHERE M.recipient_dept = ? AND M.confirm_time IS NULL order by M.sent_time desc ";
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
                //TODO add a 'recieve_time' to each message only the first time it is presented in the inboxdr
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
    function sentdr($works_in_dept){
        $response = array();
        $query="SELECT R.ID, R.sent_time, R.text, U.full_name, M.patient_ID, T.name, (CASE WHEN R.confirm_time IS NULL THEN 0 ELSE R.confirm_time END) AS confirm_time FROM responses as R JOIN messages M on R.response_to_messageID=M.ID JOIN users as U ON R.sender_user=U.employee_ID JOIN test_types as T ON M.test_type=T.ID   WHERE U.works_in_dept = ? order by R.sent_time desc ";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $works_in_dept);

        $stmt->execute();

        $stmt->store_result();
        $rows=$stmt->num_rows;

        if ($stmt->num_rows > 0) {

            while ($rows>0){
                $stmt->bind_result($id, $sentTime, $text,$fullNameU, $patientId, $testName, $confirmTime);
                $stmt->fetch();
            
                $report[$stmt->num_rows-$rows] = array('id' =>$id,
                    'sent_time' => $sentTime,
                    'text' =>$text,
                    'sender_name' => $fullNameU,
                    'patient_id' => $patientId,
                    'name' => $testName,
                    'confirm_time' => $confirmTime,
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

    function donedr($department)
    {
        $response = array();
        $query="SELECT M.ID, M.sent_time, M.patient_ID, T.name, M.text, U.full_name FROM messages as M JOIN test_types as T ON M.test_type=T.ID JOIN users as U ON M.confirm_user=U.employee_ID WHERE M.recipient_dept = ? AND M.confirm_time IS NOT NULL order by M.sent_time desc ";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $department);

        $stmt->execute();

        $stmt->store_result();
        $rows=$stmt->num_rows;

        if ($stmt->num_rows > 0) {

            while ($rows>0){
                $stmt->bind_result($id, $sentTime, $patientId, $testName, $text, $fullNameU);
                $stmt->fetch();
            
                $report[$stmt->num_rows-$rows] = array('id' =>$id,
                    'sent_time' => $sentTime,
                    'patient_id' => $patientId,
                    'name' => $testName,
                    'text' => $text,
                    'full_name' => $fullNameU
                );
                $rows--;
                //TODO add a 'recieve_time' to each message only the first time it is presented in the inboxdr
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

    function getMessage($messageID){
        //TODO Join Messages & Test-types tables on testType field, in order to get boolean or numeric value.
        // If this works, remove field is_value_bool from table Messages and change function send_message accordingly.
        $response = array();
        $stmt = $this->conn->prepare("SELECT M.ID, M.sent_time, M.patient_ID, T.ID, T.name, T.measurement_unit, M.is_value_boolean, M.test_result_value, M.text, M.component, M.is_urgent, M.sender_user FROM messages as M JOIN test_types as T ON M.test_type=T.ID WHERE M.ID = ?");
        //TODO Join users on message.sender_user=users.ID and add to SELECT the user name and department, to be displayed in the message screen
        //TODO perform the last TODO again for patient name
        $stmt->bind_param("s", $messageID);
        $stmt->execute();
        $stmt->store_result();
        if ($stmt->num_rows > 0){
            if ($stmt->num_rows < 2){
                $stmt->bind_result($messageID, $sentTime, $patientId, $testID, $testName, $measurementUnit, $isValueBool, $testResultValue, $comments, $componentName, $isUrgent, $sender);
                $stmt->fetch();
                $requestedMessage = array(
                    'messageID' => $messageID,
                    'sentTime' => $sentTime,
                    'patientId' => $patientId,
                    'testID' => $testID,
                    'testName' => $testName,
                    'measurementUnit' => $measurementUnit,
                    'isValueBool' => $isValueBool,
                    'testResultValue' => $testResultValue,
                    'comments' => $comments,
                    'componentName' => $componentName,
                    'isUrgent' => $isUrgent,
                    'sender' => $sender
                );
                $response['error'] = false;
                $response['message'] = 'Pulled message successfully';
                $response['requestedMessage'] = $requestedMessage;
            } else {
                $response['error'] = true;
                $response['message'] = 'נמצאה יותר מהודעה אחת במזהה המבוקש';
            }
        } else {
            $response['error'] = true;
            $response['message'] = 'לא נמצאה הודעה במזהה המבוקש';
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

    function markAsRead($messageID, $userID){
        $response = array();
        $stmt = $this->conn->prepare("UPDATE messages SET confirm_time = CURRENT_TIMESTAMP, confirm_user = ? WHERE messages.ID = ?;");
        $stmt->bind_param("ss", $userID, $messageID);
        if ($stmt->execute()) {
            $response['error'] = false;
            $response['message'] = 'Message marked as read successfully';
        } else {
            $response['error'] = true;
            $response['message'] = 'Error while trying to mark the message as read';
        }
        return $response;
    }

    function send_reply($sender, $department, $messageID, $text){
        $response = array();
        $stmt = $this->conn->prepare("INSERT INTO responses(response_to_messageID, responses.text, sender_user, recipient_dept) VALUES (?,?,?,?);");
        $stmt->bind_param("ssss", $messageID, $text, $sender, $department);
        if ($stmt->execute()) {
            $response['error'] = false;
            $response['message'] = 'Response sent successfully';
        } else {
            $response['error'] = true;
            $response['message'] = 'Error while sending the response';
        }
        return $response;
    }

    function forward_message($messageID, $department, $userID){
        $response = array();
        //TODO validate that the message wasn't approved before allowing forward
        $stmtMessages = $this->conn->prepare("UPDATE messages SET sent_time = CURRENT_TIMESTAMP, recipient_dept = ?, sender_user = ? WHERE messages.ID = ?;");
        // To be added in phase B: save history of forwarded messages in DB table 'forwarded_messages', while only the most recent sender and time will be saved in messages table. Currently irrelevant as interrogation interface isn't being developed in current development phase.

        $stmtMessages->bind_param("sss", $department, $userID, $messageID);
        if ($stmtMessages->execute()) {
            $response['error'] = false;
            $response['message'] = 'Message forwarded successfully';
        } else {
            $response['error'] = true;
            $response['message'] = 'Error while trying to forward message';
        }
        return $response;
    }
}
