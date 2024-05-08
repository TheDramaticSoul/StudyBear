<?php
#Need to implement try catch for database queries;

class DBConnector
{	
	private $conn;
	function __construct()
	{
		require_once 'DBConstants.php';
		#echo "Constructing DBConnector </br>";
		#require_once 'DBConfiguration.php';		
		try
		{
			$this->conn = new PDO("mysql:host=".HOST.";dbname=".DATABASE, USER, PASSWORD);
			// set the PDO error mode to exception
			$this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			#echo "Connected successfully </br>"; 
		}
		catch(PDOException $e)
		{
			echo "Connection failed: </br>" . $e->getMessage();
		}
	}
	
	function Login($uname, $pword)
	{
		#Getting user password hash from database and comparing against rehashed user provided password
		#If matched, return true and log user in, if false, return error string
		$sql = "SELECT password, accountStatus FROM USER WHERE userName = '$uname';";
	    $stm = $this->conn->prepare($sql);
		if($stm->execute())
		{
			$result = $stm->fetch();
			if(password_verify($pword, $result[0]) && $result[1] == 'A')
				return "success";
			else if (password_verify($pword, $result[0]) && $result[1] == 'I')
				return "inactive";
			else
				return "incorrect username/password.";
		}
		else
			return "error";
	}

		#require "PHPMailerAutoLoader.php"
	function Register($uname, $fname, $lname, $email, $pword, $university)
	{
		$password = $pword;
		#Creating password hash using BLOWFISH encryption with randomized SALT to add complexity
		$pw_hash = password_hash($password, PASSWORD_BCRYPT);
		
		#Checking for existing user name and returning true if exists, false otherwise
		$sql_checkuser = "SELECT userName, email FROM USER WHERE userName = '$uname';";
		$stm1 = $this->conn->prepare($sql_checkuser);
		$stm1->execute();
		$result = $stm1->fetch();

		if($result[0] == $uname || $result[1] == $email)
			return "uname_error";
		else
		{
			#Inserting new users into database and returning success message if sql passes, error otherwise
			$sql = "INSERT INTO USER VALUES('$uname', '$fname', '$lname', '$pw_hash', '$email', '', '$university', 'I');";
			$stm = $this->conn->prepare($sql);
			$stm->execute();

			$activationId = uniqid('', true);
			$sql2 = "INSERT INTO USER_ACTIVATION VALUES('$uname', '$activationId');";
			$stm2 = $this->conn->prepare($sql2);
			
			if($stm2->execute())
				return $this->accountActivation($uname, $activationId, $email);		
			else
				echo "Message no Sent";
		}			
			#Email functionality to be implemented 
		}
	
	function getProfile($uname)
	{
		$sql = "SELECT firstName,lastName, biography, universityName FROM USER WHERE userName = '$uname';";
		$stm = $this->conn->prepare($sql);
		if($stm->execute())		
			$result = $stm->fetch(PDO::FETCH_ASSOC);	
		else
			return "error";
		
		#Classes brings back multiple records from the database, so to avoid weird json_encoding, I just 
		#wrote a second query and appended the key/value pair to the end of the first array by class number.
		$classes_sql = 
		"SELECT C.classId, C.className, D.professorLname,D.professorFname
		FROM USER_ENROLLMENT A 
			inner join TEACHING B ON A.professorId = B.professorId
			and A.classId = B.classId
			inner join CLASS C ON B.classId = C.classId
			inner join PROFESSOR D ON B.professorId = D.professorId
		WHERE A.userName = '$uname';";
			
		$stm2 = $this->conn->prepare($classes_sql);
			if($stm2->execute())
			{
				$class = $stm2->fetch();

				$classes_array;
				if($class == false)				
					$result["classList"] = null;				
				else
				{			
					while($class[0] != null)
					{
						$classes_array[] = $class;
						$class = $stm2->fetch();
					}
					$result["classList"] = $classes_array;
				}
			}
		
		$university_sql = "SELECT * FROM UNIVERSITY;";
		$stm3 = $this->conn->prepare($university_sql);

		if($stm3->execute())
		{
			
			$university = $stm3->fetch();
			$universityArray = null;
			while ($university[0] != null)
			{
				$universityArray[] = $university;
				$university = $stm3->fetch();
			}
				$result["universityList"] = $universityArray;			
		}
		return json_encode($result);
		
		return "error";
	}
	
	function getUserClasses($username){
		$classes_sql = 
		"SELECT C.classId, C.className, D.professorLname,D.professorFname
		FROM USER_ENROLLMENT A 
			inner join TEACHING B ON A.professorId = B.professorId
			and A.classId = B.classId
			inner join CLASS C ON B.classId = C.classId
			inner join PROFESSOR D ON B.professorId = D.professorId
		WHERE A.userName = '$username';";
			
		$stm2 = $this->conn->prepare($classes_sql);
		$result;
			if($stm2->execute())
			{
				$class = $stm2->fetch();

				$classes_array;
				if($class == false)
				{
					$classes_array["classList"] = null;
					return json_encode($classes_array);
				}
				else
				{			
					while($class[0] != null)
					{
						$classes_array[] = $class;
						$class = $stm2->fetch();
					}
					$result["classList"] = $classes_array;
					return json_encode($result);
				}
			}
	}
	#Make sure client populates whats already saved in database first and then call this function
	function editProfile($fname, $lname, $oldPassword, $newPassword, $university, $uname){
		if(isset($oldPassword, $newPassword) && strlen($newPassword) >= 8){
			$sql = "SELECT password, accountStatus FROM USER WHERE userName = '$uname';";
			$stm = $this->conn->prepare($sql);
			if($stm->execute())
			{
				$result = $stm->fetch();
				if(password_verify($oldPassword, $result[0]) && $result[1] == 'A'){
					$password = $newPassword;
					$pw_hash = password_hash($password, PASSWORD_BCRYPT);
					
					$sql2 = "UPDATE USER SET firstName = '$fname', lastName = '$lname', universityname = '$university', password = '$pw_hash' WHERE userName = '$uname';";	
					$stm2 = $this->conn->prepare($sql2);
							if($stm2->execute())
								return "success";
							else
								return "error";	
				}
				else
					return "wrongPassword";
			}
		} else{
			$sql3 = "UPDATE USER SET firstName = '$fname', lastName = '$lname', universityname = '$university' WHERE userName = '$uname';";	
					$stm3 = $this->conn->prepare($sql3);
					if($stm3->execute())
						return "success";
					else
						return "error";
				}
	}
	
	function getUniversity($username){
		$sql_university_list = "SELECT universityName FROM user WHERE username ='$username'
								UNION						
								SELECT * FROM UNIVERSITY WHERE universityName NOT IN (SELECT universityName FROM user WHERE username ='$username');";		
		$stm = $this->conn->prepare($sql_university_list);
		$stm->execute();	
		$universityList["List"] = $stm->fetchAll();
		
		return json_encode($universityList);
	}
	
	function getUniversityList(){
	$sql_university_list = "SELECT universityName FROM university ORDER BY universityName ASC;";
							
	$stm = $this->conn->prepare($sql_university_list);
	$stm->execute();	
	$universityList["List"] = $stm->fetchAll();
		
		return json_encode($universityList);
	}
	
	function getMajor($university){
		$sql_major = "SELECT DISTINCT major FROM class WHERE universityName = '$university' ORDER BY universityName ASC;";
							
		$stm = $this->conn->prepare($sql_major);
		$stm->execute();	
		$majorList["majorList"] = $stm->fetchAll();
		return json_encode($majorList);
	}
	
	function getClasses($username, $university, $major){
		$classes_sql = 
		"SELECT B.classId, B.className,C.professorLname, C.professorFname, B.major
		FROM TEACHING A 
			inner join CLASS B on A.classId = B.classId
			inner join PROFESSOR C on A.professorId = C.professorId
		WHERE B.universityName = '$university' AND B.major = '$major' order by 1 asc;";
		$result;
		$stm2 = $this->conn->prepare($classes_sql);
			if($stm2->execute())
			{
				$class = $stm2->fetch();

				$classes_array;
				if($class == false){
					$classes_array["classList"] = null;
					return json_encode($classes_array);
				}
				else
				{			
					while($class[0] != null)
					{
						$classes_array[] = $class;
						$class = $stm2->fetch();
					}
					$result["classList"] = $classes_array;	
					return json_encode($result);					
				}
			}
			
	}
	
	#messages
	function getMessages($userName){
		$sql  = "SELECT *, DATE_FORMAT(dateTime, '%m/%d/%y') AS niceDate 
				from messages 
				where (sendingUser = '$userName'
							AND receivingUser NOT IN (
								SELECT blockeduserName
								FROM USER_BLOCKED
								WHERE userName='$userName')
							AND receivingUser NOT IN (
								SELECT userName
								FROM USER_BLOCKED
								WHERE blockeduserName='$userName'))
					
					or (receivingUser = '$userName'
							AND sendingUser NOT IN (
								SELECT userName
								FROM USER_BLOCKED
								WHERE blockeduserName='$userName')
							AND sendingUser NOT IN (
								SELECT blockeduserName
								FROM USER_BLOCKED
								WHERE userName='$userName'))
				order by dateTime DESC;";

		$stm = $this->conn->prepare($sql);
		if($stm->execute())

		$message = $stm->fetch();
		$messageArray;
		while ($message[0] != null){
			$messageArray[] = $message;
			$message = $stm->fetch();
		}

		$result["messageList"] = $messageArray;
		return json_encode($result);
	}

	function getConvo($buddy, $username){
		$sql  = "SELECT *, DATE_FORMAT(dateTime, '%l:%i%p %m/%d/%y ') AS niceDate from messages where (sendingUser = '$buddy' and receivingUser = '$username' ) or (receivingUser = '$buddy' and sendingUser = '$username') order by dateTime ASC;";

		$stm = $this->conn->prepare($sql);
		if($stm->execute())

		$message = $stm->fetch();
		$messageArray;
		while ($message[0] != null){
			$messageArray[] = $message;
			$message = $stm->fetch();
		}

		$result["messageList"] = $messageArray;
		return json_encode($result);
	}

	function newMessage($mTo, $mBody, $uName){
		$sql = "SELECT EXISTS(SELECT * FROM user WHERE username = '$mTo');";

		$stm = $this->conn->prepare($sql);
		$stm->execute();
		$result = $stm->fetch();
		
		if($result[0] == 0)
			return "error";
		else{
		$sql = "INSERT INTO messages (sendingUser, receivingUser, body, subject, dateTime) VALUES ('$uName', '$mTo', '$mBody', 'hi', now());";

		$stm = $this->conn->prepare($sql);
		if($stm->execute())
			echo "success";
		}
	}

	function getMatches($userName) {
		# weights for each portion of the query
		$classWeight = "3";
		$prevResponseWeight = "2";
		$sameResponseWeight = "1";
		
		$sql = "SELECT userName, firstName, lastName, biography, universityName,
				
					((IFNULL((SELECT COUNT(1) 
								FROM USER_ENROLLMENT A
									JOIN USER_ENROLLMENT B 
										ON A.professorId = B.professorId 
											AND A.classId = B.classId
								WHERE A.userName = '$userName'
									AND b.userName = U.userName),0) * $classWeight) +
					(IFNULL((SELECT COUNT(1)
								FROM MatchResponse
								WHERE userName = '$userName'
									AND otherUserName = U.userName
									AND response = 'study'
								GROUP BY userName, otherUserName, response),0) * $prevResponseWeight) -
					(IFNULL((SELECT COUNT(1)
							FROM MatchResponse
							WHERE userName = '$userName'
								AND otherUserName = U.userName
								AND response = 'pass'
							GROUP BY userName, otherUserName, response),0)) * $prevResponseWeight) +
					(IFNULL((SELECT COUNT(1) 
							FROM MatchResponse A 
								JOIN MatchResponse B 
									ON A.otherUserName = B.otherUserName 
										AND A.response = B.response 
							WHERE A.userName = '$userName' 
								AND B.userName = U.userName),0) * $sameResponseWeight)
				AS total_weight

				FROM USER U
				WHERE userName <> '$userName' 
					AND userName NOT IN (
						SELECT blockeduserName
						FROM USER_BLOCKED
						WHERE userName='$userName')
					AND userName NOT IN (
						SELECT userName
						FROM USER_BLOCKED
						WHERE blockeduserName='$userName')
				ORDER BY total_weight DESC;";
		
		$stm = $this->conn->prepare($sql);
		if ($stm->execute()) {
		
			$user = $stm->fetch();
			$userArray;
			while ($user[0] != null){
				$userArray[] = $user;
				$user = $stm->fetch();
			}
		
			$result["userList"] = $userArray;
			return json_encode($result);
		}
	}

	function getMatchesClasses($uname){
		$classes_sql = 
		"SELECT C.classId, C.className, D.professorLname,D.professorFname
		FROM USER_ENROLLMENT A 
			inner join TEACHING B ON A.professorId = B.professorId
			and A.classId = B.classId
			inner join CLASS C ON B.classId = C.classId
			inner join PROFESSOR D ON B.professorId = D.professorId
		WHERE A.username = '$uname';";
			
		$stm2 = $this->conn->prepare($classes_sql);
			if($stm2->execute())
			{
				$class = $stm2->fetch();

				$classes_array;
				if($class == false)				
					$result["classList"] = null;				
				else
				{			
					while($class[0] != null)
					{
						$classes_array[] = $class;
						$class = $stm2->fetch();
					}
					$result["classList"] = $classes_array;
					return json_encode($result);
				}
			}
	}
	
	function storeMatchResponse($userName, $otherUserName, $response) {
		$userName = str_replace('%20', ' ', $userName);
		$otherUserName = str_replace('%20', ' ', $otherUserName);
		$sql = "INSERT INTO MatchResponse(userName, otherUserName, response)
				VALUES('$userName', '$otherUserName', '$response');";
				
		$stm = $this->conn->prepare($sql);
		if ($stm->execute())
			return "match response success";
		else
			return "match response fail";
	}
	
	function storeBlock($userName, $otherUserName) {
		$userName = str_replace('%20', ' ', $userName);
		$otherUserName = str_replace('%20', ' ', $otherUserName);
		$sql = "INSERT INTO USER_BLOCKED(userName, blockeduserName)
				VALUES('$userName', '$otherUserName');";
				
		$stm = $this->conn->prepare($sql);
		if ($stm->execute())
			return "block success";
		else
			return "block fail";
	}
	
	function saveClasses($username, $removeList, $insertList){	
		$deleteDecode = json_decode($removeList);
		$insertDecode = json_decode($insertList);
		
		$classId;
		$professorFname;
		$professorLname;
		
		#return $removeList;
		for($i = 0; $i < count($insertDecode); $i++){
			$insertRow = explode(", ", $insertDecode[$i]);
			$classId = $insertRow[0];
			$professorFname = $insertRow[3];
			$professorLname = $insertRow[2];
			
			$sql = "INSERT into USER_ENROLLMENT 
				select '$username', C.professorId, B.classId, 'A'
				from TEACHING A 
			inner join CLASS B on A.classId = B.classId
			inner join PROFESSOR C on A.professorId = C.professorId
		WHERE B.classId = '$classId' and C.professorFname = '$professorFname' and C.professorLname = '$professorLname';";
		
		$stm = $this->conn->prepare($sql);
		$stm->execute();
		}
		
		for($i = 0; $i < count($deleteDecode); $i++){
			$deleteRow = explode(", ", $deleteDecode[$i]);
			$classId = $deleteRow[0];
			$professorFname = $deleteRow[3];
			$professorLname = $deleteRow[2];
			
			$sql = "DELETE FROM USER_ENROLLMENT
			WHERE professorId IN (SELECT professorId from PROFESSOR where professorFname = '$professorFname' and professorLname = '$professorLname')
			and classId = '$classId' and username = '$username';";
			$stm = $this->conn->prepare($sql);
			$stm->execute();
		}				
	}

	function accountActivation($username, $activationId, $email){	
		include 'EmailServer.php';
		return sendActivationEmail($username, $activationId, $email);
	}
	
	function accountConfirm($actId, $username){
		$sql = "SELECT actId FROM USER_ACTIVATION WHERE userName = '$username';";
		$stm = $this->conn->prepare($sql);
		$stm->execute();
		$confirmId = $stm->fetch();
		
		if($confirmId[0] == $actId)
		{
			$sql2 = "UPDATE USER SET accountStatus = 'A' WHERE userName = '$username';";
			$stm2 = $this->conn->prepare($sql2);
			if($stm2->execute()){
				$sql3 = "DELETE FROM USER_ACTIVATION WHERE userName = '$username';";
				$stm3 = $this->conn->prepare($sql3);		
				$stm3->execute();
				return "Thanks for activiating your account. You can now use StudyBear.";
			}
			else
				return "There was a problem activating your account.";
		}
		else
			return "There was a problem activating your account. Either it has already been activated or the account has expired.";
	}

	function saveBio($bio, $username){
	$sql = "UPDATE User SET biography = '$bio' WHERE username = '$username';";
		
	$stm = $this->conn->prepare($sql);
		if($stm->execute())
			echo $bio;
	}

	function editAccount ($username){
		$sql = "SELECT firstName, lastName, email, universityName FROM USER where username = '$username';";
		$stm = $this->conn->prepare($sql);
	
	if($stm->execute()){
		$account = $stm->fetch(PDO::FETCH_ASSOC);

		$result = $account;
		return json_encode($result);
	}
		else
			return "error";
	}

	function addBlockedUser ($username, $blockeduserName){
		$sql = "SELECT EXISTS(SELECT * FROM user WHERE username = '$blockeduserName');";

		$stm = $this->conn->prepare($sql);
		$stm->execute();
		$result = $stm->fetch();
		
		if($result[0] == 0)
			return "error";
		else{
			$sql = "SELECT EXISTS(SELECT * FROM user_blocked WHERE username = '$username' and blockeduserName = '$blockeduserName');";

			$stm = $this->conn->prepare($sql);
			$stm->execute();
			$result = $stm->fetch();

			if($result[0] != 0)
				return "already";
			else{
				$sql = "INSERT into user_blocked VALUES ('$username', '$blockeduserName');";

				$stm = $this->conn->prepare($sql);
				if($stm->execute())
					echo "success";
			}
		}
	}

	function removeBlockedUser ($username, $blockeduserName){
		$sql = "DELETE from user_blocked where username = '$username' and blockedusername = '$blockeduserName';";

		$stm = $this->conn->prepare($sql);
		if($stm->execute())
			echo "success";
	}

	function getBlockList ($username){
		$sql = "SELECT blockeduserName from user_blocked where userName = '$username';";

		$stm = $this->conn->prepare($sql);
		if($stm->execute())

			$blocked = $stm->fetch();
			$blockedArray;
			while ($blocked[0] != null){
				$blockedArray[] = $blocked;
				$blocked = $stm->fetch();
			}

			$result["blockedList"] = $blockedArray;
			return json_encode($result);
	}
	
	function verifyEmail($email){
	$sql = "SELECT EXISTS(SELECT * FROM user WHERE email = '$email');";
		$stm = $this->conn->prepare($sql);
		$stm->execute();
		$result = $stm->fetch();
		
		if($result[0] == 0)
			return false;
		else return true;
	}
	
	function resetPassword($email, $password, $confirmpassword){
				
		if(isset($password, $confirmpassword) && strlen($password) >= 8 && $password == $confirmpassword){
			$sql = "SELECT userName, accountStatus FROM USER WHERE email = '$email';";
			$stm = $this->conn->prepare($sql);
			if($stm->execute())
			{
				$result = $stm->fetch();
				if($result[0] != null && $result[1] == 'A'){
					$username = $result[0];
					$pw_hash = password_hash($password, PASSWORD_BCRYPT);
					
					$sql2 = "UPDATE USER SET password = '$pw_hash' WHERE userName = '$username';";	
					$stm2 = $this->conn->prepare($sql2);
					if($stm2->execute())
						return "Password successfully reset.";
					else
						return "Make sure passwords match!";	
				}
				else
					echo $sql. "addresserror";
			}
		}
	}
}
?>