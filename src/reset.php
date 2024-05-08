<!DOCTYPE html>
<html>
<body>

<form action="index.php" method="post">
<input type="hidden" name="rtype" value="resetPassword">
<input type="hidden" name="email" value="<?php echo $_GET["email"] ?>">
Password:<br>
<input type="text" name="password">
<br>
Confirm Password:<br>
<input type="text" name="confirmpassword">
<br><br>
<input type="submit" value="Submit">
</form> 

</body>
</html>