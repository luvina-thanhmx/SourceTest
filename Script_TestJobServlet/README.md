Source test for TestJobServlet (#400).
---------------------------------------------
A. How to test?  
- Step1: run ./run_test.sh with format  
	./run_test.sh -p /home/mrtit/WiperdogHome/ -c Case1  
- Step2: Check result  

Note: "Case1, Case2, Case3" test job connect to database => Need to config default.params and password file connect to SQLServer, MYSQL, POSTGRES.  

B. Case test  

 1. Test get data for create menu tree (Case1)  
  - Expected: menu tree is true.  
  
 2. Test get data of job (Case2)  
  - Expected: get data corresponding to jobfile successfully.  

 3. Test create job file (Case3)  
  - Expected: create job successfully.  

 4. Test connect to SQLServer use QUERY (Case4)  
  - Expected: process job and return data successfully.  
  
 5. Test connect to MYSQL (Case5)  
  - Expected: process job and return data successfully.  

 6. Test connect to POSTGRES (Case6)  
  - Expected: process job and return data successfully.  

 7. Test connect to OS (Case7)  
  - Expected: process job and return data successfully.  

 8. Test job processing with GROUPKEY + ACCUMULATE + FINALLY (Case8)  
  - Expected: data of job will be process by ACCUMULATE and FINALLY. Return data successfully.  

 9. Test job processing with data Subtyped (Case9)  
  - Expected: Return data with message corresponding to jobfile successfully.  

 10. Test job processing with COMMAND + FORMAT (Case10)  
  - Expected: Return data contains "id", "name", "desc" successfully.  

 11. Test process job have error (Case11)  
  - Expected: Get message error in wiperdog.log successfully.  
