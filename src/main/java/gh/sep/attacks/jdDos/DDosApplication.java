package gh.sep.attacks.jdDos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DDosApplication {
	public static void main(String[] args) {
		System.out.println("======= JdDos =======");
		System.out.println("Simple Java based dDos tool");
		System.out.println("This project is developed just for experiment. ** USE IT AT YOUR OWN RISK **");
		System.out.println();
		System.out.println("jdDos can perform HTTP dDos attack from multiple threads.");
		System.out.println("ir Supports GET and POST (json based) requests, and forces you to enter at least one `Socks proxy` before you can use it.");
		System.out.println("This script is already tested on TOR network");
		System.out.println("For more information, visit: https://github.com/sepehr-gh/jddos");
		System.out.println();
		SpringApplication.run(DDosApplication.class, args);
	}

}
