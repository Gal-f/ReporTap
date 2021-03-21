<?php 

    class Api{
	
        private $conn;
        private $response;
        
        function __construct(){
            require_once 'DbConnect.php';
            //require_once dirname(__FILE__).'/DbConnect.php'; //Try this if the former doesn't work
            
            $db = new DbConnect();
            $this->conn = $db->connect();
            $this->response = array();
        }
	
        //TODO seperate a "case" class (this one) from an implementation class (DbOperations)? [low priority in the backlog]
        if(isset($_GET['apicall'])){
		
            switch($_GET['apicall']){
			
                case 'signup':
                    if(isTheseParametersAvailable(array('password','employee_ID','full_name','role','phone_number','works_in_dept'))){
                        //getting the values 
                        $password = md5($_POST['password']);
                        $employeeNumber = $_POST['employee_ID']; 
                        $fullName = $_POST['full_name']; 
                        $jobTitle = $_POST['role']; 
                        $phoneNumber = $_POST['phone_number']; 
                        $deptID = $_POST['works_in_dept']; 

                        //checking if the user is already exist with this employee number (must be unique) 
                        $stmt = $this->conn->prepare("SELECT id FROM users WHERE employee_ID = ? OR phone_number = ?");
                        $stmt->bind_param("ss", $employeeNumber, $phoneNumber);
                        $stmt->execute();
                        $stmt->store_result();

                        //if the user already exist in the database 
                        if($stmt->num_rows > 0){
                            $this->response['error'] = true;
                            $this->response['message'] = 'משתמש קיים במערכת, יש לבדוק את מספר העובד ואת מספר הטלפון שהוזנו';
                            $stmt->close();
                        }else{
                            $stmt = $this->conn->prepare("INSERT INTO users (password, employee_ID, full_name, role, phone_number, works_in_dept) VALUES (?, ?, ?, ?, ?, ?)");
                            $stmt->bind_param("sssssi", $password, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);

                            if($stmt->execute()){
                                $stmt = $this->conn->prepare("SELECT id, employee_ID, full_name, role, phone_number, works_in_dept  FROM users WHERE employee_ID = ?"); 
                                $stmt->bind_param("s",$employeeNumber);
                                $stmt->execute();
                                $stmt->bind_result($id, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);
                                $stmt->fetch();
                                
                                $user = array(
                                    'id'=>$id, 
                                    'employee_ID' => $employeeNumber,
                                    'full_name' => $fullName,
                                    'role' => $jobTitle,
                                    'phone_number' => $phoneNumber,
                                    'works_in_dept' => $deptID
                                );
							
                                $stmt->close();
                                //adding the user data in response 
                                $this->response['error'] = false; 
                                $this->response['message'] = 'User registered successfully'; 
                                $this->response['user'] = $user; 
                            }
                        }
                    }
                    break; 
                
                case 'login':
				
                    if(isTheseParametersAvailable(array('employee_ID', 'password'))){
					
                        $employeeNumber = $_POST['employee_ID'];
                        $password = md5($_POST['password']); 
					
                        $stmt = $this->conn->prepare("SELECT id, employee_ID, full_name, role, phone_number, works_in_dept FROM users WHERE employee_ID = ? AND password = ?");
                        $stmt->bind_param("ss",$employeeNumber, $password);
					
                        $stmt->execute();
					
                        $stmt->store_result();
					
                        if($stmt->num_rows > 0){
						
                            $stmt->bind_result($id, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);
                            $stmt->fetch();
						
                            $user = array(
                                'id'=>$id, 
                                'employee_ID'=>$employeeNumber,
                                'full_name' => $fullName,
                                'role' => $jobTitle,
                                'phone_number' => $phoneNumber,
                                'works_in_dept' => $deptID
                            );
						
                            $this->response['error'] = false; 
                            $this->response['message'] = 'Login successfull'; 
                            $this->response['user'] = $user; 
                        }else{
                            $this->response['error'] = false; 
                            $this->response['message'] = 'שגיאה בפרטי ההזדהות';
                        }
                    }
                    break; 
                
                case 'newmessage':
                    if (isTheseParametersAvailable(array('sender','department','patientId','patientName','testName','componentName','measuredAmount','isUrgent','comments'))){
                        require_once 'DbOperations';
                        $oper = new DbOperations();
                        this->$response = $oper->send_message($_POST['sender'],$_POST['department'],$_POST['patientId'],$_POST['patientName'],$_POST['testName'],$_POST['componentName'],$_POST['measuredAmount'],$_POST['isUrgent'],$_POST['comments']);
                    }
                    break;
			
                default: 
                    $this->response['error'] = true; 
                    $this->response['message'] = 'Invalid Operation Called';
            }
        }else{
            $this->response['error'] = true; 
            $this->response['message'] = 'Invalid API Call';
        }
        
        echo json_encode($this->response);
	
        
        function isTheseParametersAvailable($params){
		
            $error = false;
            foreach($params as $param){
                if(!isset($_POST[$param])){ // Whether a parameter is missing
                    $error = true; 
                }
            }
            if ($error){    // If there's an error, set the response to 'missing parameters' error message
                $this->response['error'] = true; 
                $this->response['message'] = 'required parameters are not available'; 
            }
            return !($error); // Return true if all parameters are in order
        }
    }
