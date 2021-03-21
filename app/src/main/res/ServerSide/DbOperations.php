<?php

class DbOperations{

    private $conn;
    private $response;
    function __construct(){
            require_once 'DbConnect.php';
            //require_once dirname(__FILE__).'/DbConnect.php';
            
            $db = new DbConnect();
            $this->conn = $db->connect();
            $this->response = array();
        }

    function send_message($sender, $department, $patientId, $patientName, $testName, $componentName, $measuredAmount, $isUrgent, $comments){
        $message = array($department, $patientId, $patientName, $testName, $componentName, $measuredAmount, $isUrgent, $comments);
        $stmt = $this->conn->prepare("INSERT INTO `messages`(`patient_ID`, `test_type`, `component`, `value_boolean`, `value_numeric`, `text`, `is_urgent`, `sender_user`, `recipient_dept`) VALUES (?,?,?,?,?,?,?,?,?,?);")
        $stmt->bind_params($patientId, $testName, $componentName, NULL, $measuredAmount, $comments, $isUrgent, $sender, $department);
        if (SUCCESSFUL){    //TODO complete condition
            $this->response['error'] = false; 
            $this->response['message'] = 'Message sent successfully'; 
            $this->response['sent_message'] = $message; 
        }
        return $this->response;
    }
}
    