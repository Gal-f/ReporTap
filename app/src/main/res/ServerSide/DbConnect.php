<?php

class DbConnect{
    private $conn;
    
    function __construct(){
        
    }

    function connect(){
        $servername = "edenpe.mtacloud.co.il";
        $username = "edenpe_eden";
        $password = "edengalyael";
        $database = "edenpe_ReporTapDB";

        /*
        $servername = "localhost";
        $username = "root";
        $password = "";
        $database = "android";
        */
 
        //creating a new connection object using mysqli 
        $this->conn = new mysqli($servername, $username, $password, $database);
 
        //if there is some error connecting to the database
        //with 'die' we will stop the further execution by displaying a message about the error 
        if ($conn->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        }   
        return $this->conn;
    }
}
