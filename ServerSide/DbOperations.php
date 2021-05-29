<?php

require 'vendor/autoload.php';
require_once 'vendor/sendgrid/sendgrid/sendgrid-php.php';

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


    function signup($password, $employeeNumber, $fullName, $email, $jobTitle, $phoneNumber, $deptID)
    {
        $response = array();
        //checking if a user with this employee number or phone number or email number already exists (must be unique)
        $stmt = $this->conn->prepare("SELECT id FROM users WHERE employee_ID = ? OR phone_number = ? OR email = ?");
        $stmt->bind_param("sss", $employeeNumber, $phoneNumber, $email);
        $stmt->execute();
        $stmt->store_result();

        //if the user already exists in the database
        if ($stmt->num_rows > 0) {
            $response['error'] = true;
            $response['message'] = 'משתמש קיים במערכת עם אימייל, טלפון או מספר עובד זהה';
            $stmt->close();
        } else {
		    try{
				$stmt = $this->conn->prepare("INSERT INTO users (password, employee_ID, full_name, email, role, phone_number, works_in_dept) VALUES (?, ?, ?, ?, ?, ?, ?)");
				$stmt->bind_param("ssssssi", $password, $employeeNumber, $fullName, $email, $jobTitle, $phoneNumber, $deptID);

				if ($stmt->execute()) {
					$stmt = $this->conn->prepare("SELECT id, employee_ID, full_name, email, role, phone_number, works_in_dept  FROM users WHERE employee_ID = ?");
					$stmt->bind_param("s", $employeeNumber);
					$stmt->execute();
					$stmt->bind_result($id, $employeeNumber, $fullName, $email, $jobTitle, $phoneNumber, $deptID);
					$stmt->fetch();
					$stmt->close();

					$query="SELECT `dept_type` FROM `departments` WHERE `ID` = ? ";
            		$stmt1 = $this->conn->prepare($query);
            		$stmt1->bind_param("i", $deptID);
            		$stmt1->execute();
            		$stmt1->store_result();
            		$stmt1->bind_result($deptType);
            		$stmt1->fetch();

					$user = array(
						'id' => $id,
						'employee_ID' => $employeeNumber,
						'full_name' => $fullName,
						'email' => $email,
						'role' => $jobTitle,
						'phone_number' => $phoneNumber,
						'works_in_dept' => $deptID,
						'dept_type' => $deptType
					);

					$stmt->close();
					//adding the user data in response
					$response['error'] = false;
					$response['message'] = 'משתמש נרשם בהצלחה';
					$response['user'] = $user;
				}
			}
			catch (Exception $e) {
				echo 'Caught exception: '. $e->getMessage() ."\n";
			}
		}

        return $response;
    }

	function sendOTP($email, $phoneNumber, $sendTo, $otp){

    	include 'vendor/apiKeys.php';
    	//if the user wanted us to send him a code via sms
    	if($sendTo == "phone"){
    		$phoneNum = "972".substr($phoneNumber,1);
    		$basic  = new \Vonage\Client\Credentials\Basic($vonageApiKey, $vonageApiSecret);
    		$client = new \Vonage\Client($basic);
    		$res = $client->sms()->send(new \Vonage\SMS\Message\SMS($phoneNum, "ReporTap", "קוד האימות שלך הוא: ".$otp));
    		$apiRes = $res->current();
    		//if the message was sent successfully
    		if($apiRes->getStatus() == 0){
    			$response['error'] = false;
    			$response['message'] = 'קוד נשלח בהצלחה';
    		}
    		else{
    			$response['error'] = true;
    			$response['message'] = 'שגיאה בשליחת קוד האימות';
    		}
    	}
    	//the user wanted us to send him a code via email
    	else{
    		$mail = new \SendGrid\Mail\Mail();
    		$mail->setFrom("edenpe@mta.ac.il", "ReporTap");
    		$mail->setSubject("אימות חשבון חדש");
    	    $mail->addTo($email);
    		$mail->addContent("text/plain", "קוד האימות שלך הוא: ".$otp);
    		$sendgrid = new \SendGrid($sendGridApiKey);
    		try{
				$sendGridRes = $sendgrid->send($mail);
				if($sendGridRes->statusCode() >= 200 && $sendGridRes->statusCode() < 300){
					$response['error'] = false;
					$response['message'] = 'קוד נשלח בהצלחה';
				}
				else{
					$response['error'] = true;
					$response['message'] = 'שגיאה בשליחת קוד האימות';
					$response['sendGridError'] = print_r($sendGridRes);
				}

    		}
    		catch (Exception $e){
    			echo 'Caught exception: '. $e->getMessage() ."\n";
    		}
    	}
	return $response;
	}


	function verifiedUser($employeeNumber){
		$response = array();
		//it this method was called we know that the user has already typed the correct code
		$stmt = $this->conn->prepare('UPDATE `users` SET `otp_verified`=1 WHERE `employee_ID` = "'.$employeeNumber.'" ');
		$stmt->execute();
		$stmt->store_result();
	//	$stmt->fetch();

		//we would like to know if the system administrator approved the user's account
		$stmt2 = $this->conn->prepare('SELECT is_active FROM users WHERE employee_ID = "'.$employeeNumber.'"');
		$stmt2->execute();
        $stmt2->store_result();
		$stmt2->bind_result($isActive);
		$stmt2->fetch();
		if(!$isActive){
			$response['isActive'] = false;
		}
		else{
			$response['isActive'] = true;
		}
        return $response;
	}

     function login($employeeNumber, $password)
    {
        $response = array();
        $stmt = $this->conn->prepare("SELECT id, employee_ID, full_name, email, role, phone_number, works_in_dept, is_active, otp_verified, is_deleted FROM users WHERE employee_ID = ? AND password = ?");
		$stmt->bind_param("ss", $employeeNumber, $password);
        $stmt->execute();
        $stmt->store_result();

		//if there is a user with this employee number and password in the db
        if ($stmt->num_rows > 0) {
    		$stmt->bind_result($id, $employeeNumber, $fullName, $email, $jobTitle, $phoneNumber, $deptID, $isActive, $otp_verified, $is_deleted);
    		$stmt->fetch();
    		if($is_deleted){
    			$response['error'] = true;
    			$response['message'] = 'חשבונך הושעה, לעזרה יש לפנות למנהל המערכת';
    		}
    		else{
    			$query="SELECT `dept_type` FROM `departments` WHERE `ID` = ? ";
    			$stmt1 = $this->conn->prepare($query);
    			$stmt1->bind_param("i", $deptID);
    			$stmt1->execute();
    			$stmt1->store_result();
    			$stmt1->bind_result($deptType);
    			$stmt1->fetch();

    			$user = array(
    				'id' => $id,
    				'employee_ID' => $employeeNumber,
    				'full_name' => $fullName,
    				'email' => $email,
    				'role' => $jobTitle,
    				'phone_number' => $phoneNumber,
    				'works_in_dept' => $deptID,
    				'dept_type'=>$deptType
    				);
    			$response['error'] = false;
    			$response['user'] = $user;
    			//check whether the user completed the 2fa or not
    			if($otp_verified){
    			    $response['otpVerified'] = true;
    				//check whether the system administrator approved the user's account
    				if($isActive){
    					$response['message'] = 'התחברות בוצעה בהצלחה';
    					$response['isActive'] = true;
    				}
    				else{
    					$response['message'] = 'המשתמש ממתין לאישור מנהל';
    					$response['isActive'] = false;
    				}
    			}
    			else{
    				 $response['message'] = 'משתמש לא מאומת';
    				 $response['otpVerified'] = false;
    			}
			}
        } else {
            $response['error'] = true;
            $response['message'] = 'שגיאה בפרטי ההזדהות';
        }
        return $response;
    }

      function getNotActive(){
        $response = array();
        $stmt = $this->conn->prepare('SELECT `full_name`, `employee_ID`, `role`, `works_in_dept` FROM users WHERE `is_active`=0 order by `registration_date` DESC' );
		$stmt->execute();
		$stmt->store_result();
		$rows = $stmt->num_rows;
		 if ($stmt->num_rows > 0){
    	      while ($rows>0){
    	        $stmt->bind_result($fullName, $employeeNumber, $jobTitle, $deptID);
                $stmt->fetch();

                $users[$stmt->num_rows-$rows] = array('full_name' => $fullName,
    			'employee_ID' => $employeeNumber,
    			'role' => $jobTitle,
    			'works_in_dept' => $deptID
                );
                $rows--;
    	      }
    	     $response['error'] = false;
    	     $response['message'] = "יש משתמשים הממתינים לאישור";
    	     $response['users']= $users;
		 }
		 else{
		     $response['error'] = true;
		     $response['message']="אין משתמשים הממתינים לאישור";
		 }

		 return $response;
    }

    function approveUser($employeeNumber){
        include 'vendor/apiKeys.php';
		$response = array();
        $stmt = $this->conn->prepare('UPDATE users SET is_active=1 WHERE employee_ID ="'.$employeeNumber.'"');
        if ($stmt->execute()) {
            $msg = array
              (
            'body'  => 'איזה כיף, המנהל אישר אותך! על מנת לראות את השינוי יש להתחבר מחדש',
            'title' => 'ReporTap New Message'
              );
            $fields = array
                (

                    'to'        => '/topics/'.$employeeNumber,
                    'notification'  => $msg
                );


             $headers = array
                (
                    'Authorization: key='.$firebaseKey,
                    'Content-Type: application/json'
                );

            $ch = curl_init();
            curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
            curl_setopt( $ch,CURLOPT_POST, true );
            curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
            curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
            curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
            curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
            $result = curl_exec($ch );
            curl_close( $ch );
            $response['error'] = false;
            $response['message'] = 'הפעולה בוצעה בהצלחה';
            $response['notification']=$result;

        } else {
            $response['error'] = true;
            $response['message'] = 'שגיאה בביצוע הפעולה';
        }
        return $response;
    }

	function deleteUser($employeeNumber){

       $response = array();
		//because the admin user enters the employee number and dosen't choose it from a list, we should validate the input.
        $stmt = $this->conn->prepare('SELECT id, is_deleted FROM users WHERE employee_ID = "'.$employeeNumber.'"');
        $stmt->execute();
        $stmt->store_result();

        //if there is a user with this employee number
        if ($stmt->num_rows > 0) {
			$stmt->bind_result($id, $isDeleted);
            $stmt->fetch();
			if($isDeleted){
				$response['error'] = true;
				$response['message'] = 'המשתמש כבר נמצא במצב השעייה';
			}
			else{
				$stmt->close();
				$stmt1 = $this->conn->prepare('UPDATE `users` SET `is_deleted`= 1 WHERE `employee_ID` = "'.$employeeNumber.'" ');
				if ($stmt1->execute()) {
				$response['error'] = false;
				$response['message'] = 'משתמש הושעה הצלחה';
				} else {
					$response['error'] = true;
					$response['message'] = 'שגיאה בביצוע הפעולה';
				}
			}
		}
		 else {
			$response['error'] = true;
			$response['message'] = 'לא קיים משתמש עם מספר העובד שהוזן';
		 }
        return $response;
	}

    function send_message($sender, $department, $patientId, $patientName, $testType, $componentName, $isValueBool, $testResultValue, $isUrgent, $comments)
    {
        include 'vendor/apiKeys.php';
        $response = array();
        $message = array($department, $patientId, $patientName, $testType, $componentName, $isValueBool, $testResultValue, $isUrgent, $comments);
        $stmt = $this->conn->prepare("INSERT INTO `messages`(`patient_ID`, `test_type`, `component`, `is_value_boolean`, `test_result_value`, `text`, `is_urgent`, `sender_user`, `recipient_dept`) VALUES (?,?,?,?,?,?,?,?,?);");
        //TODO Change test_type from simple string to relation with test_types table
        $stmt->bind_param("sisidsiii", $patientId, $testType, $componentName, $isValueBool, $testResultValue, $comments, $isUrgent, $sender, $department); //If there's a problem with sqli query, try changing boolean columns to tinyint and use 'i' instead of 's' in the first parameter for bind_param.
        if ($stmt->execute()) {
            //send notifications to the users via firebase api

            $msg = array
              (
            'body'  => 'קיבלת הודעה חדשה ',
            'title' => 'ReporTap New Message'
              );
            $fields = array
                (

                    'to'        => '/topics/'.$department,
                    'notification'  => $msg
                );


             $headers = array
                (
                    'Authorization: key='.$firebaseKey,
                    'Content-Type: application/json'
                );

            $ch = curl_init();
            curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
            curl_setopt( $ch,CURLOPT_POST, true );
            curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
            curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
            curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
            curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
            $result = curl_exec($ch );
            curl_close( $ch );

            $response['error'] = false;
            $response['message'] = 'ההודעה נשלחה בהצלחה';
            $response['sent_message'] = $message;
            $response['notification']=$result;
        } else {
            $response['error'] = true;
            $response['message'] = 'שגיאה בשליחת המסר';
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
        $query="SELECT R.ID, R.sent_time, R.text, U.full_name, M.patient_ID,P.full_name, T.name, (CASE WHEN R.confirm_time IS NULL THEN 0 ELSE R.confirm_time END) AS confirm_time FROM responses as R JOIN messages M on R.response_to_messageID=M.ID JOIN users as U ON R.sender_user=U.employee_ID JOIN test_types as T ON M.test_type=T.ID JOIN patients as P ON M.patient_ID=P.patient_ID WHERE U.works_in_dept = ? order by R.sent_time desc ";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $works_in_dept);

        $stmt->execute();

        $stmt->store_result();
        $rows=$stmt->num_rows;

        if ($stmt->num_rows > 0) {

            while ($rows>0){
                $stmt->bind_result($id, $sentTime, $text,$fullNameU, $patientId, $fullNameP, $testName, $confirmTime);
                $stmt->fetch();

                $report[$stmt->num_rows-$rows] = array('id' =>$id,
                    'sent_time' => $sentTime,
                    'text' =>$text,
                    'sender_name' => $fullNameU,
                    'patient_id' => $patientId,
                    'full_name_p' => $fullNameP,
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
        $query="SELECT M.ID, M.sent_time, M.patient_ID, T.name, M.text, U.full_name FROM messages as M JOIN test_types as T ON M.test_type=T.ID JOIN users as U ON M.confirm_user=U.employee_ID WHERE M.recipient_dept = ? AND M.confirm_time IS NOT NULL order by M.confirm_time desc ";
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
        $stmt = $this->conn->prepare("SELECT M.ID, M.sent_time, M.patient_ID, P.full_name, T.ID, T.name, T.measurement_unit, M.is_value_boolean, M.test_result_value, M.text, M.component, M.is_urgent, M.sender_user, U.full_name, U.works_in_dept, D.name, M.confirm_time FROM messages as M JOIN test_types as T ON M.test_type=T.ID JOIN users as U ON M.sender_user=U.employee_ID JOIN departments as D ON U.works_in_dept=D.ID JOIN patients as P ON M.patient_ID=P.patient_ID WHERE M.ID = ?");
        //TODO Join users on message.sender_user=users.ID and add to SELECT the user name and department, to be displayed in the message screen
        //TODO perform the last TODO again for patient name
        $stmt->bind_param("s", $messageID);
        $stmt->execute();
        $stmt->store_result();
        if ($stmt->num_rows > 0){
            if ($stmt->num_rows < 2){
                $stmt->bind_result($messageID, $sentTime, $patientId, $patientName, $testID, $testName, $measurementUnit, $isValueBool, $testResultValue, $comments, $componentName, $isUrgent, $sender, $senderName, $senderDept, $senderDeptName, $confirmTime);
                $stmt->fetch();
                $requestedMessage = array(
                    'messageID' => $messageID,
                    'sentTime' => $sentTime,
                    'patientId' => $patientId,
                    'patientName' => $patientName,
                    'testID' => $testID,
                    'testName' => $testName,
                    'measurementUnit' => $measurementUnit,
                    'isValueBool' => $isValueBool,
                    'testResultValue' => $testResultValue,
                    'comments' => $comments,
                    'componentName' => $componentName,
                    'isUrgent' => $isUrgent,
                    'sender' => $sender,
                    'senderName' => $senderName,
                    'senderDept' => $senderDept,
                    'senderDeptName' => $senderDeptName,
                    'confirmTime' => $confirmTime
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
        $response['error'] = false; //Will contain 'true' if at least ONE of the three queries failed
        $response['message'] = ''; //Will contain a concatinated string of the three successes or failures

        //1st part - Get Departments
        $query = "SELECT ID, departments.name, dept_type FROM departments";
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        $stmt->store_result();

        $rows = $stmt->num_rows;
        if ($rows == 0){
            $response['error'] = true;
            $response['message'] = 'Unable to retrieve departments from the server';
        } else {
            while ($rows > 0){
                $stmt->bind_result($deptID, $deptName, $deptType);
                $stmt->fetch();

                $depts[$stmt->num_rows-$rows] = array('deptID' => $deptID, 'deptName' => $deptName, 'deptType' => $deptType);
                $rows--;
            }
            $response['error'] = false;
            $response['message'] = 'Departments pulled successfully';
            $response['departments'] = $depts;
        }

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

                $testsTypes[$stmt2->num_rows-$rows] = array('testID' => $testID, 'testName' => $testName, 'resultType' => $resultType);
                $rows--;
            }
            //Not setting $response['error']=false since it might have recieved 'true' for a previous query. If it remained 'false' thus far, we keep it false.
            $response['message'] .= ', Test types pulled successfully';
            $response['testTypes'] = $testsTypes;
        }

        //3rd part - Get patients
        $query = "SELECT patient_ID, full_name FROM patients";
        $stmt3 = $this->conn->prepare($query);
        $stmt3->execute();
        $stmt3->store_result();

        $rows = $stmt3->num_rows;
        if ($rows == 0){
            $response['error'] = true;
            $response['message'] .= ', Unable to retrieve patients\' details from the server';
        } else {
            while ($rows > 0){
                $stmt3->bind_result($patientID, $patientName);
                $stmt3->fetch();

                $patients[$stmt3->num_rows-$rows] = array('ID' => $patientID, 'name' => $patientName);
                $rows--;
            }
            //Not setting $response['error']=false since it might have recieved 'true' for a previous query. If it remained 'false' thus far, we keep it false.
            $response['message'] .= ', Patients\' details pulled successfully';
            $response['patients'] = $patients;
        }

        return $response;
    }

    function markAsRead($messageID, $userID, $isResponseStr){
        $isResponse = (strtolower($isResponseStr) === "true");  // Convert the string recieved to a boolean value
        $response = array();
        // Query to add a confirmation user and time
        if (!$isResponse)
        $stmtMark = $this->conn->prepare("UPDATE messages SET confirm_time = CURRENT_TIMESTAMP, confirm_user = ? WHERE messages.ID = ?;");
        else
        $stmtMark = $this->conn->prepare("UPDATE responses SET confirm_time = CURRENT_TIMESTAMP, confirm_user = ? WHERE responses.ID = ?;");
        $stmtMark->bind_param("si", $userID, $messageID);
        // Query to check whether the message had already been marked by another user
        if (!$isResponse)
        $stmtCheck = $this->conn->prepare("SELECT confirm_user FROM messages WHERE confirm_user IS NULL AND ID = ?");
        else
        $stmtCheck = $this->conn->prepare("SELECT confirm_user FROM responses WHERE confirm_user IS NULL AND ID = ?");
        $stmtCheck->bind_param("i",$messageID);
        $stmtCheck->execute();
        $stmtCheck->store_result();
        if ($stmtCheck->num_rows == 0){  // If it had been marked by another user, return a message and don't continue
            $response['error'] = true;
            $response['message'] = "ההודעה כבר אושרה ע\"י משתמש אחר";
            $response['alreadyMarked'] = true;
        } else {
            if ($stmtMark->execute()) {
                $response['error'] = false; // Successfully marked by the user
                $response['message'] = 'Message marked as read successfully';
                $response['alreadyMarked'] = false;
            } else {
                $response['error'] = true;  // Unable to mark, server-side error
                $response['message'] = 'Error while trying to mark the message as read';
                $response['alreadyMarked'] = false;
        }
    }
        return $response;
    }

     function send_reply($sender, $department, $messageID, $text){
		include 'vendor/apiKeys.php';
        $response = array();
        $stmt = $this->conn->prepare("INSERT INTO responses(response_to_messageID, responses.text, sender_user, recipient_dept) VALUES (?,?,?,?);");
        $stmt->bind_param("ssss", $messageID, $text, $sender, $department);
        if ($stmt->execute()) {
			//send notifications to the users via firebase api
            $msg = array
              (
            'body'  => 'קיבלת תגובה חדשה',
            'title' => 'ReporTap New Message'
              );
            $fields = array
                (

                    'to'        => '/topics/'.$department,
                    'notification'  => $msg
                );


             $headers = array
                (
                    'Authorization: key='.$firebaseKey,
                    'Content-Type: application/json'
                );

            $ch = curl_init();
            curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
            curl_setopt( $ch,CURLOPT_POST, true );
            curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
            curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
            curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
            curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
            $result = curl_exec($ch );
            curl_close( $ch );

            $response['error'] = false;
            $response['message'] = 'תגובה נשלחה בהצלחה';
            $response['notification']=$result;
        } else {
            $response['error'] = true;
            $response['message'] = 'Error while sending the response';
        }
        return $response;
    }

    function forward_message($messageID, $department, $userID){
        include 'vendor/apiKeys.php';
        $response = array();
        //TODO validate that the message wasn't approved before allowing forward
        $stmtMessages = $this->conn->prepare("UPDATE messages SET sent_time = CURRENT_TIMESTAMP, recipient_dept = ?, sender_user = ? WHERE messages.ID = ?;");
        // To be added in phase B: save history of forwarded messages in DB table 'forwarded_messages', while only the most recent sender and time will be saved in messages table. Currently irrelevant as interrogation interface isn't being developed in current development phase.

        $stmtMessages->bind_param("sss", $department, $userID, $messageID);
        if ($stmtMessages->execute()) {

            //send notifications to the users via firebase api
            $msg = array
              (
            'body'  => 'קיבלת הודעה חדשה',
            'title' => 'ReporTap New Message'
              );
            $fields = array
                (
                    'to'        => '/topics/'.$department,
                    'notification'  => $msg
                );

             $headers = array
                (
                    'Authorization: key='.$firebaseKey,
                    'Content-Type: application/json'
                );

            $ch = curl_init();
            curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
            curl_setopt( $ch,CURLOPT_POST, true );
            curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
            curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
            curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
            curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
            $result = curl_exec($ch );
            curl_close( $ch );

            $response['error'] = false;
            $response['message'] = 'Message forwarded successfully';
        } else {
            $response['error'] = true;
            $response['message'] = 'Error while trying to forward message';
        }
        return $response;
    }
    function inboxlab($department)
    {
        $response = array();
        $query="SELECT R.ID,M.ID, R.sent_time, M.patient_ID, T.name, R.text,T.measurement_unit, M.component, CASE WHEN M.is_value_boolean IS NULL THEN 0 ELSE M.is_value_boolean END AS is_value_boolean,M.test_result_value, U.full_name, D.name FROM responses as R JOIN messages as M on R.response_to_messageID=M.ID JOIN users as U ON M.sender_user=U.employee_ID JOIN test_types as T ON M.test_type=T.ID JOIN departments as D ON U.works_in_dept=D.ID WHERE R.recipient_dept = ? AND R.confirm_time IS NULL order by R.sent_time desc";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $department);

        $stmt->execute();

        $stmt->store_result();
        $rows=$stmt->num_rows;

        if ($stmt->num_rows > 0) {

            while ($rows>0){
                $stmt->bind_result($id,$messageID, $sentTime, $patientId, $testName, $text,$measurement,$component,$isValueBool,$resultValue,$fullName,$deptName);
                $stmt->fetch();

                $report[$stmt->num_rows-$rows] = array('id' =>$id,
                    'messageID' => $messageID,
                    'sent_time' => $sentTime,
                    'patient_id' => $patientId,
                    'name' => $testName,
                    'text' => $text,
                    'measurement' => $measurement,
                    'component' => $component,
                    'is_value_bool' => $isValueBool,
                    'result_value'=> $resultValue,
                    'full_name'=> $fullName,
                    'dept_name' => $deptName
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


    function donelab($department)
    {
        $response = array();
        $query="SELECT R.ID,M.ID, R.sent_time, M.patient_ID, T.name, R.text, T.measurement_unit, M.component, CASE WHEN M.is_value_boolean IS NULL THEN 0 ELSE M.is_value_boolean END AS is_value_boolean,M.test_result_value, U.full_name, D.name FROM responses as R JOIN messages as M on R.response_to_messageID=M.ID JOIN users as U ON M.sender_user=U.employee_ID JOIN test_types as T ON M.test_type=T.ID JOIN departments as D ON U.works_in_dept=D.ID WHERE R.recipient_dept = ? AND R.confirm_time IS NOT NULL order by R.confirm_time desc";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $department);

        $stmt->execute();

        $stmt->store_result();
        $rows=$stmt->num_rows;

        if ($stmt->num_rows > 0) {

            while ($rows>0){
                $stmt->bind_result($id,$messageID, $sentTime, $patientId, $testName, $text,$measurement,$component,$isValueBool,$resultValue,$fullName,$deptName);
                $stmt->fetch();

                $report[$stmt->num_rows-$rows] = array('id' =>$id,
                    'messageID' => $messageID,
                    'sent_time' => $sentTime,
                    'patient_id' => $patientId,
                    'name' => $testName,
                    'text' => $text,
                    'measurement' => $measurement,
                    'component' => $component,
                    'is_value_bool' => $isValueBool,
                    'result_value'=> $resultValue,
                    'full_name'=> $fullName,
                    'dept_name' => $deptName
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

    function sentlab($department)
    {
        $response = array();
        $query="SELECT M.ID, M.sent_time, M.patient_ID,P.full_name, T.name, M.is_urgent,CASE WHEN M.confirm_time IS NULL THEN 0 ELSE M.confirm_time END AS confirm_time, D.name, M.text, T.measurement_unit, M.component, CASE WHEN M.is_value_boolean IS NULL THEN 0 ELSE M.is_value_boolean END AS is_value_boolean,CASE WHEN M.test_result_value IS NULL THEN 0 ELSE M.test_result_value END AS test_result_value, U.full_name FROM messages as M JOIN test_types as T ON M.test_type=T.ID JOIN departments as D ON M.recipient_dept=D.ID JOIN users as U ON M.sender_user=U.employee_ID JOIN patients as P ON M.patient_ID=P.patient_ID WHERE U.works_in_dept = ? order by M.sent_time desc";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $department);

        $stmt->execute();

        $stmt->store_result();
        $rows=$stmt->num_rows;

        if ($stmt->num_rows > 0) {

            while ($rows>0){
                $stmt->bind_result($id, $sentTime, $patientId,$fullNameP, $testName,$isUrgent, $confirmTime,$deptName, $text, $measUnit, $component, $isValBool, $testResult, $fullNameU);
                $stmt->fetch();

                $report[$stmt->num_rows-$rows] = array('id' =>$id,
                    'sent_time' => $sentTime,
                    'patient_id' => $patientId,
                    'full_name_p' => $fullNameP,
                    'name' => $testName,
                    'is_urgent' => $isUrgent,
                    'confirm_time' => $confirmTime,
                    'dept_name' => $deptName,
                    'text' => $text,
                    'measurement_unit' => $measUnit,
                    'component' => $component,
                    'is_value_boolean' => $isValBool,
                    'test_result_value' => $testResult,
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
}