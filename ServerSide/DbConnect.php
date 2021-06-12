<?php

class DbConnect
{
    private $conn;

    function __construct()
    {
    }

    function connect()
    {
        $servername = "localhost";
        $username = "edenpeis_eden";          //new server
        $password = "edengalyael";
        $database = "edenpeis_ReporTapDB";    //new server

        //creating a new connection object using mysqli
        mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT); //This configures PHP and MySQLi to send full MySQL errors
        $this->conn = new mysqli($servername, $username, $password, $database);

        //if there is some error connecting to the database
        //with 'die' we will stop the further execution by displaying a message about the error
        if ($this->conn->connect_error) {
            die("Connection failed: " . $this->conn->connect_error);
        }
        return $this->conn;
    }
}