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
            $response = isTheseParametersAvailable(array('password', 'employee_ID', 'full_name', 'email', 'role', 'phone_number', 'works_in_dept'));
            if (!$response['error']) {
                //getting the values
                $password = md5($_POST['password']);
                $employeeNumber = $_POST['employee_ID'];
                $fullName = $_POST['full_name'];
				$email = $_POST['email'];
                $jobTitle = $_POST['role'];
                $phoneNumber = $_POST['phone_number'];
                $deptID = $_POST['works_in_dept'];
                $response = $oper->signup($password, $employeeNumber, $fullName, $email, $jobTitle, $phoneNumber, $deptID);
            }
        break;

		case 'sendOTP':
			$response = isTheseParametersAvailable(array('email','phone_number','sendTo','otp'));
			if (!$response['error']) {
                //getting the values
				$email = $_POST['email'];
                $phoneNumber = $_POST['phone_number'];
				$otp = $_POST['otp'];
				$sendTo = $_POST['sendTo'];
                $response = $oper->sendOTP($email, $phoneNumber, $sendTo, $otp);
            }
		break;


		case 'verifiedUser':
			$response = isTheseParametersAvailable(array('employee_ID'));
			if (!$response['error']) {
			$employeeNumber = $_POST['employee_ID'];
			$response = $oper-> verifiedUser($employeeNumber);
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

        case 'notActiveUsers':
                $response = $oper->getNotActive();
        break;

       case 'approveUser':
			 if (isset($_POST['employee_ID'])){
                 $employeeNumber = $_POST['employee_ID'];
                 $response = $oper->approveUser($employeeNumber);
            }
            break;

		case 'suspendUser':
		    if (isset($_POST['employee_ID'])){
                 $employeeNumber = $_POST['employee_ID'];
                 $response = $oper->suspendUser($employeeNumber);
            }
            break;

        case 'newMessage':
            //TODO Don't ask for patientName, as it can be retrieved with patientId. Solve this by somehow presenting the patientName on the new message form, after typing the ID.
            $response = isTheseParametersAvailable(array('sender', 'department', 'patientId', 'patientName', 'testType', 'componentName', 'isValueBool', 'testResultValue', 'isUrgent', 'comments'));
            if (!$response['error']) {
                $response = $oper->send_message($_POST['sender'], $_POST['department'], $_POST['patientId'], $_POST['patientName'], $_POST['testType'], $_POST['componentName'], $_POST['isValueBool'], $_POST['testResultValue'], $_POST['isUrgent'], $_POST['comments']);
            }
        break;

        case 'getMessage':
            $response = isTheseParametersAvailable(array('messageID'));
            if (!$response['error']){
                $response = $oper->getMessage($_POST['messageID']);
            }
        break;

        case 'inboxdr':
            $response = isTheseParametersAvailable(array('department'));
            if (!$response['error']){
                $response=$oper->inboxdr($_POST['department']);
            }
        break;

        case 'sentdr':
            $response = isTheseParametersAvailable(array('works_in_dept'));
            if (!$response['error']){
                $response=$oper->sentdr($_POST['works_in_dept']);
            }
        break;
        case 'donedr':
            $response = isTheseParametersAvailable(array('department'));
            if (!$response['error']){
                $response=$oper->donedr($_POST['department']);
            }
        break;
        case 'inboxlab':
            $response = isTheseParametersAvailable(array('department'));
            if (!$response['error']){
                $response=$oper->inboxlab($_POST['department']);
            }
        break;
        case 'donelab':
            $response = isTheseParametersAvailable(array('department'));
            if (!$response['error']){
                $response=$oper->donelab($_POST['department']);
            }
        break;
        case 'sentlab':
            $response = isTheseParametersAvailable(array('department'));
            if (!$response['error']){
                $response=$oper->sentlab($_POST['department']);
            }
        break;
        case 'getdepttype':
            $response = isTheseParametersAvailable(array('id'));
            if (!$response['error']){
                $response=$oper->getdepttype($_POST['id']);
            }
        break;
        case 'getDeptsAndTests':
            $response = $oper->getDeptsAndTests();
        break;

        case 'markAsRead':
            $response = isTheseParametersAvailable(array('messageID', 'userID', 'isResponse'));
            if (!$response['error']){
               $response = $oper->markAsRead($_POST['messageID'], $_POST['userID'], $_POST['isResponse']);
            }
        break;

        case 'newReply':
            $response = isTheseParametersAvailable(array('sender', 'department', 'messageId', 'text'));
            if (!$response['error']){
                $response = $oper->send_reply($_POST['sender'], $_POST['department'], $_POST['messageId'], $_POST['text']);
            }
        break;

        case 'forwardMessage':
            $response = isTheseParametersAvailable(array('messageID', 'department', 'sender'));
            if (!$response['error']){
                $response = $oper->forward_message($_POST['messageID'], $_POST['department'], $_POST['sender']);
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
}