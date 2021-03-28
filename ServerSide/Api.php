<?php

//This page calls the appropriate action from DbOperations.php according to the requested action in the address (GET method),
//And returns a 'response' array - containing error details and additional info specific to the action.

$response = array();
$response['error'] = false;
$response['message'] = "No action performed yet";

require_once 'DbOperations.php';
$oper = new DbOperations(); //All operations with the DB are performed using this object

if (isset($_GET['apicall'])) {

    switch ($_GET['apicall']) {

        case 'signup':
            $response = isTheseParametersAvailable(array('password', 'employee_ID', 'full_name', 'role', 'phone_number', 'works_in_dept'));
            if (!$response['error']) {
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
            $response = isTheseParametersAvailable(array('employee_ID', 'password'));
            if (!$response['error']) {

                $employeeNumber = $_POST['employee_ID'];
                $password = md5($_POST['password']);

                $response = $oper->login($employeeNumber, $password);
            }
            break;

        case 'newMessage':
            //TODO Don't ask for patientName, as it can be retrieved with patientId. Solve this by somehow presenting the patientName on the new message form, after typing the ID.
            $response = isTheseParametersAvailable(array('sender', 'department', 'patientId', 'patientName', 'testType', 'componentName', 'boolValue', 'measuredAmount', 'isUrgent', 'comments'));
            if (!$response['error']) {
                $response = $oper->send_message($_POST['sender'], $_POST['department'], $_POST['patientId'], $_POST['patientName'], $_POST['testType'], $_POST['componentName'], $_POST['boolValue'], $_POST['measuredAmount'], $_POST['isUrgent'], $_POST['comments']);
            }
            break;
        case 'inboxdr':
            $response = isTheseParametersAvailable(array('department'));
            if (!$response['error']){
                $response=$oper->inboxdr($_POST['department']);
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

function isTheseParametersAvailable($params) //TODO alter this func to return $response instead of a boolean, in order not to use global variables (and accordingly, the conditions to "if(isTheseParametersAvailable['error'])").
{
    $response = array();
    $error = false;
    foreach ($params as $param) {
        if (!isset($_POST[$param])) { // Whether a parameter is missing
            $error = true;
            $response['missingParameters'] .= $param . " ";
        }
    }
    if ($error) {    // If there's an error, set the response to 'missing parameters' error message
        $response['error'] = true;
        $response['message'] = 'Required parameters are not available';
    } else {
        $response['error'] = false;
        $response['message'] = 'All parameters recieved';
    }
    return $response;
    //return !($error); // Return true if all parameters are in order   //Old, here for backup purpose only, remove when everything works
}
