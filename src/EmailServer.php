<?php
	require_once('PHPMailerAutoload.php');
	function sendActivationEmail($username, $activationId, $email){

		$mail             = new PHPMailer();
		$body             = "<!DOCTYPE html>
							<html>
							<title>Studybear</title>
							<body>
							<h1>Confirm Your StudyBear Account Now!</h1>
							<p><a href=\"http://10.10.168.169/index.php?rtype=accountConfirm&actId=$activationId&username=$username\">Click here to confirm your account</p>
							</body>
							</html>"; 
		//$body             = eregi_replace("[\]",'',$body);
		$mail->IsSMTP(); // telling the class to use SMTP
		$mail->Host       = "mail.studybear.com"; // SMTP server
		//$mail->SMTPDebug  = 2;                     // enables SMTP debug information (for testing)

		// 1 = errors and messages
		// 2 = messages only
		$mail->SMTPAuth   = true;                  // enable SMTP authentication
		$mail->SMTPSecure = "tls";                 // sets the prefix to the servier
		$mail->Host       = "smtp.gmail.com";      // sets GMAIL as the SMTP server
		$mail->Port       = 587;                   // set the SMTP port for the GMAIL server
		$mail->Username   = "studybearproject@gmail.com"; // GMAIL username. I used a lay around one that we had but we can change this to something different.
		$mail->Password   = "studybear1";          // GMAIL password
		$mail->SetFrom('noreply@studybear.com');
		$mail->AddReplyTo("studybearproject@studybear.com","StudyBear");
		$mail->Subject    = "Account Activation Required";
		//$mail->AltBody    = "Click the link to confirm. http://192.168.43.138/index.php?rtype=accountConfirm&actId=$activationId&username=$username"; // optional, comment out and test
		$mail->MsgHTML($body);
		$mail->AddAddress($email);
		//$mail->AddAttachment("images/phpmailer.gif");      // attachment
		//$mail->AddAttachment("images/phpmailer_mini.gif"); // attachment
	
		
		if(!$mail->Send()) 
			echo "Mailer Error: " . $mail->ErrorInfo;		 
		else
			return "success";
//include("class.smtp.php"); // optional, gets called from within class.phpmailer.php if not already loaded
}

	function sendPasswordLink($email){
		$mail             = new PHPMailer();
		$body             = "<!DOCTYPE html>
							<html>
							<title>Studybear</title>
							<body>
							<h1>StudyBear Password Reset</h1>
							<p>Click the link to reset your password. <a href=\"http://10.10.168.169/reset.php?email=$email\">Password Reset</a></p>
							</body>
							</html>"; 
		//$body             = eregi_replace("[\]",'',$body);
		$mail->IsSMTP(); // telling the class to use SMTP
		$mail->Host       = "mail.studybear.com"; // SMTP server
		//$mail->SMTPDebug  = 2;                     // enables SMTP debug information (for testing)

		// 1 = errors and messages
		// 2 = messages only
		$mail->SMTPAuth   = true;                  // enable SMTP authentication
		$mail->SMTPSecure = "tls";                 // sets the prefix to the servier
		$mail->Host       = "smtp.gmail.com";      // sets GMAIL as the SMTP server
		$mail->Port       = 587;                   // set the SMTP port for the GMAIL server
		$mail->Username   = "studybearproject@gmail.com"; // GMAIL username. I used a lay around one that we had but we can change this to something different.
		$mail->Password   = "studybear1";          // GMAIL password
		$mail->SetFrom('noreply@studybear.com');
		$mail->AddReplyTo("studybearproject@gmail.com","StudyBear");
		$mail->Subject    = "Password Reset";
		//$mail->AltBody    = "Click the link to confirm. http://192.168.43.138/index.php?rtype=accountConfirm&actId=$activationId&username=$username"; // optional, comment out and test
		$mail->MsgHTML($body);
		$mail->AddAddress($email);
		
		if(!$mail->Send()) 
			return "errorSending";//"Mailer Error: " . $mail->ErrorInfo;		
		else
			return "success";
//include("class.smtp.php"); // optional, gets called from within class.phpmailer.php if not already loaded
		
	}
?>