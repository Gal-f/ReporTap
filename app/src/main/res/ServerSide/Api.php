<?php 

	require_once 'DbConnect.php';
	
	$response = array();
	
	if(isset($_GET['apicall'])){
		
		switch($_GET['apicall']){
			
			case 'signup':
				if(isTheseParametersAvailable(array('username','password','employee_ID','full_name','role','phone_number','works_in_dept'))){
					//getting the values 
					$username = $_POST['username']; 
					$password = md5($_POST['password']);
					$employeeNumber = $_POST['employee_ID']; 
					$fullName = $_POST['full_name']; 
					$jobTitle = $_POST['role']; 
					$phoneNumber = $_POST['phone_number']; 
					$deptID = $_POST['works_in_dept']; 
					
					//checking if the user is already exist with this username
					$stmt = $conn->prepare("SELECT id FROM users WHERE username = ?");
					$stmt->bind_param("s", $username);
					$stmt->execute();
					$stmt->store_result();
					
					//if the user already exist in the database 
					if($stmt->num_rows > 0){
						$response['error'] = true;
						$response['message'] = 'User already registered';
						$stmt->close();
					}else{
						$stmt = $conn->prepare("INSERT INTO users (username, password, employee_ID, full_name, role, phone_number, works_in_dept) VALUES (?, ?, ?, ?, ?, ?, ?)");
						$stmt->bind_param("ssssssi", $username, $password, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);

						if($stmt->execute()){
							$stmt = $conn->prepare("SELECT id, username, employee_ID, full_name, role, phone_number, works_in_dept  FROM users WHERE username = ?"); 
							$stmt->bind_param("s",$username);
							$stmt->execute();
							$stmt->bind_result($id, $username, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);
							$stmt->fetch();
							
							$user = array(
								'id'=>$id, 
								'username'=>$username, 
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
					
				}else{
					$response['error'] = true; 
					$response['message'] = 'required parameters are not available'; 
				}
				
			break; 
			
			case 'login':
				
				if(isTheseParametersAvailable(array('username', 'password'))){
					
					$username = $_POST['username'];
					$password = md5($_POST['password']); 
					
					$stmt = $conn->prepare("SELECT id, username, employee_ID, full_name, role, phone_number, works_in_dept FROM users WHERE username = ? AND password = ?");
					$stmt->bind_param("ss",$username, $password);
					
					$stmt->execute();
					
					$stmt->store_result();
					
					if($stmt->num_rows > 0){
						
						$stmt->bind_result($id, $username, $employeeNumber, $fullName, $jobTitle, $phoneNumber, $deptID);
						$stmt->fetch();
						
						$user = array(
							'id'=>$id, 
							'username'=>$username, 
							'employee_ID'=>$employeeNumber,
							'full_name' => $fullName,
							'role' => $jobTitle,
							'phone_number' => $phoneNumber,
							'works_in_dept' => $deptID
						);
						
						$response['error'] = false; 
						$response['message'] = 'Login successfull'; 
						$response['user'] = $user; 
					}else{
						$response['error'] = false; 
						$response['message'] = 'Invalid username or password';
					}
				}
			break; 
			
			default: 
				$response['error'] = true; 
				$response['message'] = 'Invalid Operation Called';
		}
		
	}else{
		$response['error'] = true; 
		$response['message'] = 'Invalid API Call';
	}
	
	echo json_encode($response);
	
	function isTheseParametersAvailable($params){
		
		foreach($params as $param){
			if(!isset($_POST[$param])){
				return false; 
			}
		}
		return true; 
	}