/**
 * @author Pedro Martins
 * @version 1.1.1
 * 
 * MINA server 1. This MINA server will receive a generated report from the load
 * balancer, add its name to the report, and then send it to MINA server 3. It 
 * will always be called by the load balancer first, no matter what. 
 * 
 * @see	{@link MyApp_A}
 * @see	{@link MyApp_C}
 * @see	{@link Report}
 * 
 * @see	<a href="http://camel.apache.org/mina2.html">MINA 2</a>
 * @see	<a href="http://camel.apache.org/running-camel-standalone-and-have-it-keep-running.html">Java Main in Camel</a>
 */
package servers;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class MyApp_B {

	private Main main;

	public static void main(String... args) throws Exception {
		MyApp_B loadbalancer = new MyApp_B();
		loadbalancer.boot();
	}

	public void boot() throws Exception {
		main = new Main();
		main.enableHangupSupport();

		main.addRouteBuilder(
				new RouteBuilder(){
					@Override
					public void configure() throws Exception {
						from("mina:tcp://localhost:9991")
						.setHeader("minaServer", constant("localhost:9991"))
						.beanRef("service.Reporting", "updateReport")
						.to("direct:messageSender1");

						from("direct:messageSender1").to("mina:tcp://localhost:9993")
						.log("${body}");
					}
				}
		);

		System.out.println("Starting Camel MyApp_B. Use ctrl + c to terminate the JVM.\n");
		main.run();
	}
}

