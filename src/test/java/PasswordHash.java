
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import junit.framework.TestCase;

public class PasswordHash extends TestCase {
	
	
	public void testMD5Hash(){
		
		String password="secret";
		Md5PasswordEncoder passwordEnc=new Md5PasswordEncoder();
		String hashedPasswd=passwordEnc.encodePassword(password,null);
		System.out.println(hashedPasswd);
		
	}

}
