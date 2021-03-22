<?php

//This page calls the appropriate action from DbOperations.php according to the requested action in the address (GET method),
//And returns a 'response' array - containing error details and additional info specific to the action.

$response = array();
$response['error'] = false;
$response['message'] = "No action performed yet";

require_once 'DbOperations';
$oper = new DbOperations(); //All operations with the DB are performed using this object

if (isset($_GET['apicall'])) {

    switch ($_GET['apicall']) {

        case 'signup':
            if (isTheseParametersAvailable(array('password', 'employee_ID', 'full_name', 'role', 'phone_number', 'works_in_dept'), $response)) {
                //getting the values 
                $password = md5($_POST['password']);
                $employeeNumber = $_POST['employee_ID'];
                $fullName = $_POST['full_name'];
                $jobTitle = $_POST['role'];
                $phoneNumber = $_POST['phone_number'];
                $deptID = $_POST['works_in_dept'];

                $response = $oper->signup($password, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);
            }
            break;

        case 'login':

            if (isTheseParametersAvailable(array('employee_ID', 'password'), $response)) {

                $employeeNumber = $_POST['employee_ID'];
                $password = md5($_POST['password']);

                $response = $oper->login($employeeNumber, $password);
            }
            break;

        case 'newMessage':
            if (isTheseParametersAvailable(array('sender', 'department', 'patientId', 'patientName', 'testName', 'componentName', 'measuredAmount', 'isUrgent', 'comments'), $response)) {
                $response = $oper->send_message($_POST['sender'], $_POST['department'], $_POST['patientId'], $_POST['patientName'], $_POST['testName'], $_POST['componentName'], $_POST['measuredAmount'], $_POST['isUrgent'], $_POST['comments']);
            }
            break;

        default:
            $response['error'] = true;
            $response['message'] = 'Invalid Operation Called';
    }
} else {  //apicall parameter is missing from the address
    $response['error'] = true;
    $response['message'] = 'Invalid API Call';
}

echo json_encode($response); //Return all 'response' fields in JSON format

function isTheseParametersAvailable($params, $response) //TODO alter this func to return $response instead of a boolean, in order not to use global variables (and accordingly, the conditions to "if(isTheseParametersAvailable['error'])").
{
    $error = false;
    foreach ($params as $param) {
        if (!isset($_POST[$param])) { // Whether a parameter is missing
            $error = true;
        }
    }
    if ($error) {    // If there's an error, set the response to 'missing parameters' error message
        $response['error'] = true;
        $response['message'] = 'Required parameters are not available';
    }
    return !($error); // Return true if all parameters are in order
}