<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">  
  <head>
    <meta charset="utf-8">
    <title>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Le styles -->
    <link href="assets/css/bootstrap.css" rel="stylesheet">
    <style>
      body { padding-top: 60px; /* 60px to make the container go all the way
      to the bottom of the topbar */ }
    </style>
    <link href="assets/css/bootstrap-responsive.css" rel="stylesheet">
 
    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="assets/ico/favicon.ico">
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="assets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="assets/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="assets/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-57-precomposed.png">
   	<script type="text/javascript" src="jquery-1.8.3.js"></script>
   <script type="text/javascript">
		$(document).ready(
			function() {
								
			});
		
	</script>
   
    <style>
		.error {
			color: #ff0000;
		}
		
		.errorblock {
			color: #000;
			background-color: #ffEEEE;
			border: 3px solid #ff0000;
			padding: 8px;
			margin: 16px;
		}
	</style>
  </head>
  <body>
    <div class="navbar navbar-fixed-top navbar-inverse">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#">
           Encrpted Function
          </a>
          <ul class="nav">
          </ul>
        </div>
      </div>
    </div>
    <div class="container">
      <div>
        <h1>
         	Encrypted Messages
        </h1>
        <p>
          Add your workout goal in minutes for the day.
          <br>
          &nbsp;
        </p>
      </div>
     <form:form method="post" action="encryptMessage.html" commandName="Crypto">
	 <form:errors path="*" cssClass="errorblock" element="div" />
	 <label for="textinput1">
	          Enter Text:
	 </label>	
	 <form:textarea path="inputEncrypt" rows="5" cols="30" />
	 <input type="submit" class="btn" value="Encrypt Message"/>
   
	 </form:form>
	 <textarea name="input" cols="50" rows="10">${Crypto.encryptedMessage}</textarea>
      
      <form:form method="post" action="decryptMessage.html" commandName="Crypto">
	 <form:errors path="*" cssClass="errorblock" element="div" />
	 <label for="textinput1">
	          Enter Text:
	 </label>	
	 <form:textarea path="inputDecrypt" rows="5" cols="30" />
	 <input type="submit" class="btn" value="Decrypt Message"/>
   
	 </form:form>
      <textarea name="input" cols="50" rows="10">${Crypto.decrytedMessage}</textarea>
      <div class="control-group">
      </div>
      
    </div>
     <script src="jquery-1.8.3.js">
    </script>
    <script src="assets/js/bootstrap.js">
    </script>
  </body>
</html>
