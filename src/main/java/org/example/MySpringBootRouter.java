package org.example;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MySpringBootRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        //Insert Route
        from("direct:insert").process(new Processor() {
            public void process(Exchange xchg) throws Exception {
                //Take the Employee object from the exchange and create the insert query
                Employee employee = xchg.getIn().getBody(Employee.class);
                String query = "INSERT INTO employee(empId,empName)values('" + employee.getEmpId() + "','"
                        + employee.getEmpName() + "')";
                // Set the insert query in body and call camel jdbc
                xchg.getIn().setBody(query);
            }
        }).to("jdbc:dataSource");

        // Select Route
        from("direct:select").setBody(constant("select * from Employee")).to("jdbc:dataSource")
                .process(new Processor() {
                    public void process(Exchange xchg) throws Exception {
                        //the camel jdbc select query has been executed. We get the list of employees.
                        ArrayList<Map<String, String>> dataList = (ArrayList<Map<String, String>>) xchg.getIn()
                                .getBody();
                        List<Employee> employees = new ArrayList<Employee>();
                        System.out.println(dataList);
                        for (Map<String, String> data : dataList) {
                            Employee employee = new Employee();
                            employee.setEmpId(data.get("empId"));
                            employee.setEmpName(data.get("empName"));
                            employees.add(employee);
                        }
                        xchg.getIn().setBody(employees);
                    }
                });
    }

}
