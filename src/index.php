<?php
#Client Application Request Handler Script
#Get Request will need to change to Post request in final app;
#echo "Script started </br>";
require_once "DBConnector.php";
$dbconn = new DBConnector();
//require_once "EmailServer.php";

if(isset($_GET["rtype"])){
	switch($_GET["rtype"])
	{
	case 'login': 
		if(isset($_POST["username"], $_POST["password"])){
			if(strpos($_POST["username"], ' ') === false && strlen($_POST["password"]) >= 8){
				echo $dbconn->Login($_POST["username"], $_POST["password"]);
				break;		
			}
			else
				echo "error";
		}
		else
			echo "error";
		break;
				
	case 'register':
		if(isset($_POST["fname"], $_POST["lname"], $_POST["uname"], $_POST["email"], $_POST["pword"], $_POST["pconfirm"], $_POST["university"]))
			echo $dbconn->Register($_POST["uname"], $_POST["fname"], $_POST["lname"], $_POST["email"], $_POST["pword"], $_POST["university"]);
				break;
				
	case 'getProfile':
		if(isset($_GET["username"]) )
			echo $dbconn->getProfile($_GET["username"]);
		else
			echo "error0";
				break;
			
	case 'editProfile':
		if($_POST["fname"] != null &&  $_POST["lname"] != null)
			echo $dbconn->editProfile($_POST["fname"], $_POST["lname"], $_POST["oldpassword"], $_POST["newpassword"], $_POST["university"], $_POST["uname"]);
		else
			echo "error";
		break;
			
	case 'getUniversity':
		echo $dbconn->getUniversity($_GET["username"]);
		break;
		
	case 'getUniversityList':
		echo $dbconn->getUniversityList();
		break;
		
	case 'getMajor':
		echo $dbconn->getMajor($_GET["university"]);
		break;
	
	case 'getUserClasses':
		echo $dbconn->getUserClasses($_GET["username"]);
		break;
			
	case 'getClasses':
		echo $dbconn->getClasses($_GET["username"], $_GET["university"], $_GET["major"]);
		break;
			
	case 'getMessages':
		echo $dbconn->getMessages($_GET["username"]);
		break;
		
	case 'getConvo':
		echo $dbconn->getConvo($_GET["buddy"], $_GET["username"]);
		break;
		
	case 'newMessage':
		echo $dbconn->newMessage($_POST["mTo"], $_POST["mBody"], $_POST["uName"]);
		break;
	
	case 'getMatches':
		if (isset($_GET["username"]))
			echo $dbconn->getMatches($_GET["username"]);
		else
			echo $dbconn->getMatches("");
		break;
		
	case 'sendMatchResponse':
		if (isset($_GET["username"], $_GET["otheruser"], $_GET["response"]))
			echo $dbconn->storeMatchResponse($_GET["username"], $_GET["otheruser"], $_GET["response"]);
		else
			echo "URL parameters not set for match response";
		break;
	
	case 'sendBlockRequest':
		if (isset($_GET["username"], $_GET["otheruser"]))
			echo $dbconn->storeBlock($_GET["username"], $_GET["otheruser"]);
		else
			echo "URL parameters not set for block request";
		break;
	
	case 'saveClasses':
		if (isset($_POST["username"]))
			echo $dbconn->saveClasses($_POST["username"], $_POST["removeList"], $_POST["insertList"]);
		break;

	case 'accountConfirm':
		echo $dbconn->accountConfirm($_GET["actId"], $_GET["username"]);
		break;

	case 'saveBio':
		echo $dbconn->saveBio($_POST["biography"], $_POST["username"]);
		break;

	case 'editAccount':
		echo $dbconn->editAccount($_GET["username"]);
		break;

	case 'addBlockedUser':
		echo $dbconn->addBlockedUser($_POST["username"], $_POST["blockedUserName"]);
		break;
		
	case 'removeBlockedUser':
		echo $dbconn->removeBlockedUser($_POST["username"], $_POST["blockedUserName"]);
		break;

	case 'getBlockList':
		echo $dbconn->getBlockList($_GET["username"]);
		break;
	
	case 'sendPasswordLink':
		if($dbconn->verifyEmail($_POST["email"])){
			include 'EmailServer.php';
			echo sendPasswordLink($_POST["email"]);
		}
		break;
	case 'getMatchesClasses':
		echo $dbconn->getMatchesClasses($_GET["uname"]);
			break;	
	}
}
	else{
			switch($_POST["rtype"]){
				case 'resetPassword':
				echo $dbconn->resetPassword($_POST["email"], $_POST["password"], $_POST["confirmpassword"]);
			break;
		}
	}
?>